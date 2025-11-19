const UserDAO = require("../dao/userDAO.js");
const { createAccessToken, checkToken, createRefreshToken } = require("../modules/jwtModul.js");
const bcrypt = require("bcrypt");

class RESTuser {

  constructor() {
    this.userDAO = new UserDAO();
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
      dateOfBirth
    } = req.body;

    if (
      !hashPassword ||
      !firstName ||
      !lastName ||
      !email ||
      !yearOfStudy ||
      !areaOfStudy ||
      !imagePath ||
      !cvPath ||
      !dateOfBirth
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
        dateOfBirth
      };

      const response = await this.userDAO.add(user);

      if (response) {
        res.status(201).json({ success: "User registered successfully!" });
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

      res.status(200).json(user[0]);
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
        if (key === 'password') {
          const saltRounds = 10;
          const hashedPassword = await bcrypt.hash(value, saltRounds);
          updatePromises.push(this.userDAO.update(email, key, hashedPassword));
          continue;
        }
        
        updatePromises.push(this.userDAO.update(email, key, value));
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
}

module.exports = RESTuser;
