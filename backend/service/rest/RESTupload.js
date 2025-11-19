const multer = require("multer");
const path = require("node:path");
const fs = require("node:fs");

const cvFolder = path.join(__dirname, "../../resources/uploads/cv");

if (!fs.existsSync(cvFolder)) {
    fs.mkdirSync(cvFolder, { recursive: true });
}

const storage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, cvFolder);
    },
    filename: function (req, file, cb) {
        const email = req.user.email;
        const namePart = email.split("@")[0];
        cb(null, `${namePart}.pdf`);
    }
});

function fileFilter(req, file, cb) {
    if (file.mimetype !== "application/pdf") {
        return cb(new Error("Only PDF files allowed!"), false);
    }
    cb(null, true);
}

const uploadCV = multer({ storage, fileFilter });

module.exports = uploadCV;
