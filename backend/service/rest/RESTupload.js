const multer = require("multer");
const path = require("node:path");
const fs = require("node:fs");

const cvFolder = path.join(__dirname, "../../resources/uploads/cv");
if (!fs.existsSync(cvFolder)) {
    fs.mkdirSync(cvFolder, { recursive: true });
}

const cvStorage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, cvFolder);
    },
    filename: function (req, file, cb) {
        const email = req.user.email;
        const namePart = email.split("@")[0];
        cb(null, `${namePart}.pdf`);
    }
});

function cvFileFilter(req, file, cb) {
    if (file.mimetype !== "application/pdf") {
        return cb(new Error("Only PDF files allowed!"), false);
    }
    cb(null, true);
}

const uploadCV = multer({ storage: cvStorage, fileFilter: cvFileFilter });


const imageFolder = path.join(__dirname, "../../resources/uploads/images");
if (!fs.existsSync(imageFolder)) {
    fs.mkdirSync(imageFolder, { recursive: true });
}

const imageStorage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, imageFolder);
    },
    filename: function (req, file, cb) {
        const email = req.user.email;
        const namePart = email.split("@")[0];
        
        const ext = path.extname(file.originalname).toLowerCase();
        
        cb(null, `${namePart}${ext}`);
    }
});

function imageFileFilter(req, file, cb) {
    const allowedTypes = ["image/jpeg", "image/png", "image/jpg"];
    if (!allowedTypes.includes(file.mimetype)) {
        return cb(new Error("Only images (JPG, PNG) allowed!"), false);
    }
    cb(null, true);
}

const uploadImage = multer({ storage: imageStorage, fileFilter: imageFileFilter });

module.exports = { uploadCV, uploadImage };