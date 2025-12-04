const UserDAO = require("../dao/userDAO.js");
const { createAccessToken, checkToken, createRefreshToken } = require("../modules/jwtModul.js");
const bcrypt = require("bcrypt");
const fs = require("node:fs");
const path = require("node:path");
const { transporter } = require("../modules/nodemailer.js");

class RESTuser {

  constructor() {
    this.userDAO = new UserDAO();
    this.transporter = transporter;
  }

  async postUser(req, res) {
    res.type("application/json");

    const {
      hashPassword,
      firstName,
      lastName,
      email,
      yearOfStudy,
      areaOfStudy,
      imagePath,
      cvPath,
      dateOfBirth,
      Higher_Education_Body_id
    } = req.body;

    if (
      !hashPassword ||
      !firstName ||
      !lastName ||
      !email ||
      !yearOfStudy ||
      !areaOfStudy ||
      !dateOfBirth ||
      !Higher_Education_Body_id
    ) {
      return res.status(400).json({ error: "Required data missing!" });
    }

    try {
      const saltRounds = 10;
      const hashedPassword = await bcrypt.hash(hashPassword, saltRounds);

      const user = {
        firstName,
        lastName,
        email,
        yearOfStudy,
        areaOfStudy,
        password: hashedPassword,
        imagePath,
        cvPath,
        dateOfBirth,
        Higher_Education_Body_id
      };

      const response = await this.userDAO.add(user);

      if (response) {
        const tokenData = { email: user.email };
        const accessToken = createAccessToken(tokenData);
        const refreshToken = createRefreshToken(tokenData);

        res.status(201).json(
          {
            success: "User registered successfully!",
            accessToken: accessToken,
            refreshToken: refreshToken
          });
      } else {
        res.status(400).json({ error: "User registration failed!" });
      }
    } catch (err) {
      console.error("Error in postUser:", err);
      res.status(500).json({ error: "Internal server error" });
    }
  }

  async login(req, res) {
    res.type("application/json");

    const { email, hashPassword } = req.body;

    if (!email || !hashPassword) {
      return res.status(400).json({ error: "Required data missing!" });
    }

    try {
      const user = await this.userDAO.getUserByEmail(email);

      if (!user || !user[0] || !user[0].password) {
        return res.status(401).json({ error: "Invalid email or password!" });
      }

      const isPasswordValid = await bcrypt.compare(hashPassword, user[0].password);

      if (!isPasswordValid) {
        return res.status(401).json({ error: "Invalid email or password!" });
      }

      req.session.user = {
        email: user[0].email,
        firstName: user[0].firstName,
        lastName: user[0].lastName,
        yearOfStudy: user[0].yearOfStudy,
        areaOfStudy: user[0].areaOfStudy,
        imagePath: user[0].imagePath,
        cvPath: user[0].cvPath,
        dateOfBirth: user[0].dateOfBirth
      };

      const tokenData = { email: user[0].email };

      const accessToken = createAccessToken(tokenData);
      const refreshToken = createRefreshToken(tokenData);

        if (req.headers["x-mobile"] === "true") {
            return res.json({
                success: "Login successful",
                email,
                accessToken: accessToken,
                refreshToken: refreshToken
            });
        }
        else{
          res.status(200).json({
              success: "Login successful",
              accessToken,
              refreshToken
          });
        }
      if (req.headers["x-mobile"] === "true") {
        return res.json({
          success: "Login successful",
          email,
          accessToken: accessToken,
          refreshToken: refreshToken
        });
      }
      else {
        res.status(200).json({
          success: "Login successful",
          accessToken,
          refreshToken
        });
      }


    } catch (err) {
      console.error("Error during login:", err);
      res.status(500).json({ error: "Internal server error", details: err.message });
    }
  }

  async getCurrentUser(req, res) {
    res.type("application/json");

    try {
      const email = req.user.email;

      const user = await this.userDAO.getUserByEmail(email);

      if (!user || !user[0]) {
        return res.status(404).json({ error: "User not found" });
      }

      delete user[0].password;

      let imageBase64 = null;

      if (user[0].imagePath) {
          imageBase64 = loadImageBase64(user[0].imagePath);
      }

      res.status(200).json({
          ...user[0],
          image: imageBase64
      });
    } catch (err) {
      console.error("Error in getCurrentUser:", err);
      res.status(500).json({ error: "Internal server error" });
    }
  }

  async updateUser(req, res) {
    res.type("application/json");

    try {
      const email = req.user.email;
      const updatedUserData = req.body;

      const updatePromises = [];

      for (const [key, value] of Object.entries(updatedUserData)) {
        if (value === null || value === undefined || value === "") {
            continue;
        }
        
        const allowedColumns = [
            'firstName', 'lastName', 'yearOfStudy', 'areaOfStudy', 
            'password', 'imagePath', 'cvPath', 'dateOfBirth'
        ];
        if (!allowedColumns.includes(key)) continue;
        let valueToSave = value;
        if (key === 'dateOfBirth' && value.includes('T')) {
            valueToSave = value.split('T')[0];
        }
        if (key === 'password') {
          const saltRounds = 10;
          const hashedPassword = await bcrypt.hash(value, saltRounds);
          updatePromises.push(this.userDAO.update(email, key, hashedPassword));
          continue;
        }
        
        updatePromises.push(this.userDAO.update(email, key, valueToSave));
      }

      if (updatePromises.length === 0) {
         return res.status(200).json({ success: "Nothing to update or invalid fields." });
      }

      const results = await Promise.all(updatePromises);
      const hasFailures = results.some(result => !result);

      if (!hasFailures) {
        res.status(200).json({ success: "User updated successfully!" });
      } else {
        res.status(400).json({ error: "User update failed!" });
      }
    } catch (err) {
      console.error("Error in updateUser:", err);
      res.status(500).json({ error: "Internal server error" });
    }
  }

  async deleteUser(req, res) {
    res.type("application/json");

    try {
      const email = req.user.email;

      const response = await this.userDAO.delete(email);

      if (response) {
        res.status(200).json({ success: "User deleted successfully!" });
      } else {
        res.status(400).json({ error: "User deletion failed!" });
      }
    } catch (err) {
      console.error("Error in deleteUser:", err);
      res.status(500).json({ error: "Internal server error" });
    }
  }

  async uploadCV(req, res) {
    res.type("application/json");

    try {
        if (!req.file) {
            return res.status(400).json({ error: "No file uploaded!" });
        }

        const email = req.user.email;
        const namePart = email.split("@")[0];
        const cvPath = `/resources/uploads/cv/${namePart}.pdf`;

        const result = await this.userDAO.update(email, "cvPath", cvPath);

        if (result) {
            return res.status(200).json({
                success: "CV uploaded successfully!",
                cvPath: cvPath
            });
        }

        res.status(400).json({ error: "Database update failed!" });

    } catch (err) {
        console.error("Error in uploadCV:", err);
        res.status(500).json({ error: "Internal server error" });
    }
  }

  async uploadImage(req, res) {
    res.type("application/json");

    try {
        if (!req.file) {
            return res.status(400).json({ error: "No image uploaded!" });
        }

        const email = req.user.email;
        const namePart = email.split("@")[0];
        const ext = path.extname(req.file.originalname).toLowerCase();
        
        const imagePath = `/resources/uploads/images/${namePart}${ext}`;

        const result = await this.userDAO.update(email, "imagePath", imagePath);

        if (result) {
            return res.status(200).json({
                success: "Image uploaded successfully!",
                imagePath: imagePath
            });
        }

        res.status(400).json({ error: "Database update failed!" });

    } catch (err) {
        console.error("Error in uploadImage:", err);
        res.status(500).json({ error: "Internal server error" });
    }
  }

  async changePassword(req, res) {
    res.type("application/json");
  
    const { password, oldPassword } = req.body;    
  
    if (!password || !oldPassword) {
      res.status(400).json({ error: "Required data missing!" });
      return;
    }

    const email = req.user.email;        
  
    try {
      const user = await this.userDAO.getUserByEmail(email);
      if (!user?.[0]?.password) {
        res.status(404).json({ error: "User not found." });
        return;
      }
  
      const isMatch = await bcrypt.compare(oldPassword, user[0]?.password);
      if (!isMatch) {
        
        res.status(401).json({ error: "Incorrect old password." });
        return;
      }
  
      const saltRounds = 10;
      const hashedPassword = await bcrypt.hash(password, saltRounds);      
  
      await this.userDAO.changePassword(hashedPassword, email);

      const mailOptions = {
        from: 'info.otp.student@gmail.com',
        to: user[0]?.email,
        subject: 'Promjena lozinke',
        text: `Poštovani ${user[0]?.firstName} ${user[0]?.lastName},\n\nVaša lozinka je uspješno promijenjena.\n\nAko niste vi inicirali ovu promjenu, molimo vas da nas odmah kontaktirate na mail: info.otp.student@gmail.com.\nLijep pozdrav\nOTP student tim`
      };
      
      this.transporter.sendMail(mailOptions, function(error, info){
        if (error) {
          console.error('Greška pri slanju e-maila:', error);
        }
      });      
  
      res.status(200).json({ success: "Password changed." });
    } catch (err) {
      console.error("Error changing password: ", err);
      res.status(500).json({ error: "Internal server error" });
    }
  }
}


function loadImageBase64(relativePath) {
    try {
        const absPath = path.join(__dirname, "..", "..", relativePath);

        const dir = path.dirname(absPath);

        if (!fs.existsSync(dir)) {
            fs.mkdirSync(dir, { recursive: true });
            console.log("Created missing image directory:", dir);
        }

        if (!fs.existsSync(absPath)) {
            console.warn("Image file does not exist:", absPath);
            return null;
        }

        const imageBuffer = fs.readFileSync(absPath);
        const extension = path.extname(relativePath).substring(1);
        return `data:image/${extension};base64,${imageBuffer.toString("base64")}`;
    } catch (err) {
        console.error("Greška kod učitavanja slike:", err);
        return null;
    }
}

module.exports = RESTuser;
