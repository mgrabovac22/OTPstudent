const express = require("express");
const session = require("express-session");
const morgan = require("morgan");
const helmet = require("helmet");
const jwt = require("jsonwebtoken");
const https = require("node:https");
const fs = require("node:fs");
const path = require("node:path");
const cors = require("cors");
const logger = require("../log/logger.js");
const { createAccessToken, checkToken, createRefreshToken } = require("./modules/jwtModul.js");
const RESTuser = require("./rest/RESTuser.js");
const {uploadCV, uploadImage} = require("./rest/RESTupload.js");
const RESTInstitution = require("./rest/RESTInstitution.js");
const RESTinformationalContent = require("./rest/RESTinformationalContent.js");
const RestUserRead = require("./rest/RESTuserRead.js");
const RESTinternship = require("./rest/RESTinternship.js"); 
const RESTjobs = require("./rest/RESTjobs.js");

require("dotenv").config({ path: path.join(__dirname, "../resources/.env") });

const server = express();
const port = process.env.PORT || 3000;

const privateKey = fs.readFileSync(path.join(__dirname, "../certificates/private.key"), "utf8");
const certificate = fs.readFileSync(path.join(__dirname, "../certificates/certificate.crt"), "utf8");
const ca = fs.readFileSync(path.join(__dirname, "../certificates/ca.key"), "utf8");

const credentials = { key: privateKey, cert: certificate, ca: ca };

server.use(cors({ origin: "https://localhost", methods: ["GET", "POST", "PUT"] }));
server.use(express.json());
server.use(express.urlencoded({ extended: true }));
server.use(helmet());
server.disable("x-powered-by");

const accessLogStream = fs.createWriteStream(
    path.join(__dirname, "../log/logs/all-logging.log"),
    { flags: "a" }
);
server.use(morgan("combined", { stream: accessLogStream }));

server.set("trust proxy", true);
server.use(session({
    name: "SessionCookie",
    secret: process.env.SESSION_SECRET || "default_secret",
    resave: false,
    saveUninitialized: false,
    cookie: { secure: true, sameSite: "Strict", maxAge: 3600000 }
}));

const restInformationalContent = new RESTinformationalContent();
const restUser = new RESTuser();
const restInstitution = new RESTInstitution();
const restUserRead = new RestUserRead();
const restInternship = new RESTinternship();
const restJobs = new RESTjobs();

server.post("/api/login", restUser.login.bind(restUser));
server.post("/api/register", restUser.postUser.bind(restUser));

//For web applications
server.get("/api/getJWT", (req, res) => {    
    if (req.session.user) {
        const korisnik = { email: req.session.user.email };
        const token = createToken({ korisnik }, process.env.JWT_SECRET);
        res.status(200).json({ token: token });
    } else {
        res.status(401).json({ Error: "Session not created" });
    }
});

//For mobile applications
server.post("/api/refresh", (req, res) => {    
  const { refreshToken } = req.body;
  if (!refreshToken) return res.status(401).json({ error: "No token" });

  try {
    const decoded = jwt.verify(refreshToken, process.env.JWT_REFRESH_SECRET);
    
    const tokenData = { email: decoded.email };

    const newAccessToken = createAccessToken(tokenData);
    const newRefreshToken = createRefreshToken(tokenData);

    res.json({ 
      accessToken: newAccessToken,
      refreshToken: newRefreshToken 
    });
    
  } catch (e) {
    console.error("Refresh Token Verification Failed:", e);
  }
});

server.get("/api/logout", (req, res) => {
    if (req.session) {
        req.session.destroy(err => {
            if (err) {
                logger.error("Error deleting session: " + err);
                res.status(500).json({ Error: "Invalid logout." });
            } else {
                logger.info("Session destroyed!");
                res.clearCookie("SessionCookie");
                res.status(200).json({ Success: "Logged out." });
            }
        });
    }
});

server.all(/(.*)/, (req, res, next) => {
    if (req.path === "/login" || req.path === "/register" || req.path === "/getJWT" || req.path === "/api/institutions") {
        return next();
    }
    
    try {      
          
        const tokenValid = checkToken(req, process.env.JWT_SECRET);
        req.user = tokenValid;
                
        if (!tokenValid) {            
            res.status(401).json({ Error: "Invalid token!" }); 
            return;
        }

        const { method, originalUrl, ip } = req;
        const timestamp = new Date().toISOString();
        const user = req.session.user || {};
        const { oib = 'N/A', email = 'N/A', type = 'N/A' } = user;
        
        logger.info(`JWT valid | Time: ${timestamp} | Method: ${method} | Path: ${originalUrl} | OIB: ${oib} | Email: ${email} | Role: ${type} | IP: ${ip}`);

        next();
    } catch (err) {        
        if (err.name === "TokenExpiredError") return res.status(401).json({ Error: "Token expired." });
        logger.error("Unhandled error: " + err);
        return res.status(500).json({ Error: "Internal server error." });
    }
});


server.get("/api/current-user", restUser.getCurrentUser.bind(restUser));
server.put("/api/update-user", restUser.updateUser.bind(restUser));
server.delete("/api/delete-user", restUser.deleteUser.bind(restUser));
server.post("/api/upload-cv", uploadCV.single("cv"), restUser.uploadCV.bind(restUser));
server.post("/api/upload-image", uploadImage.single("image"), restUser.uploadImage.bind(restUser));
server.post("/api/change-password", restUser.changePassword.bind(restUser));

server.get("/api/institutions", restInstitution.getAllInstitutions.bind(restInstitution));

server.post("/api/post-info-content", restInformationalContent.post.bind(restInformationalContent));
server.get("/api/info-content", restInformationalContent.get.bind(restInformationalContent));
server.get("/api/info-content/:id", restInformationalContent.getById.bind(restInformationalContent));

server.post("/api/info-content/read", restUserRead.markContentRead.bind(restUserRead));

server.get("/api/internship/jobs", restInternship.listJobs.bind(restInternship));
server.post("/api/internship/apply", restInternship.apply.bind(restInternship));

server.get("/api/jobs", restJobs.listJobs.bind(restJobs));
server.get("/api/jobs/applications", restJobs.getUserApplications.bind(restJobs));
server.get("/api/jobs/:id", restJobs.getJobDetails.bind(restJobs));
server.post("/api/jobs/apply", restJobs.applyToJob.bind(restJobs));

server.get(/(.*)/, (req, res) => {
    res.status(200).send(`
        <html>
            <head>
                <title>Backend Only</title>
            </head>
            <body style="font-family: sans-serif; text-align: center; margin-top: 50px;">
                <h1>⚠️ This is a backend service</h1>
            </body>
        </html>
    `);
});

https.createServer(credentials, server).listen(port, () => {
    logger.info(`Server started at: https://localhost:${port}`);
    console.log(`Server started at: https://localhost:${port}`);
});
