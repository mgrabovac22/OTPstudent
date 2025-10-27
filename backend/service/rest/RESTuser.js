const UserDAO = require("../dao/userDAO.js");
const bcrypt = require("bcrypt");

class RESTuser {

  constructor() {
    this.userDAO = new UserDAO();
  }

  async postUser(req, res) {
    res.type("application/json");

    const {
      hashPassword,
      name,
      lastName,
      email,
      studyYear,
      courseOfStudy,
      imagePath,
      cvPath,
      birthDate
    } = req.body;

    if (
      !hashPassword ||
      !name ||
      !lastName ||
      !email ||
      !studyYear ||
      !courseOfStudy ||
      !imagePath ||
      !cvPath ||
      !birthDate
    ) {
      return res.status(400).json({ error: "Required data missing!" });
    }

    try {
      const saltRounds = 10;
      const hashedPassword = await bcrypt.hash(hashPassword, saltRounds);

      const user = {
        name,
        lastName,
        email,
        studyYear,
        courseOfStudy,
        password: hashedPassword,
        imagePath,
        cvPath,
        birthDate
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

      if (!user) {
        return res.status(401).json({ error: "Invalid email or password!" });
      }
      
      const isPasswordValid = await bcrypt.compare(hashPassword, user[0]?.password);
      if (!isPasswordValid) {
        return res.status(401).json({ error: "Invalid email or password!" });
      }

      req.session.user = {
        email: user[0].email,
        name: user[0].name,
        lastName: user[0].lastName,
        studyYear: user[0].studyYear,
        courseOfStudy: user[0].courseOfStudy,
        imagePath: user[0].imagePath,
        cvPath: user[0].cvPath,
        birthDate: user[0].birthDate
      };      

      res.status(200).json({ success: "Login successful!" });
    } catch (err) {
      console.error("Error in login:", err);
      res.status(500).json({ error: "Internal server error" });
    }
  }

  async getCurrentUser(req, res) {
    res.type("application/json");

    if (!req.session?.user) {
      return res.status(401).json({ error: "User not logged in!" });
    }

    try {
      const user = req.session.user;
      
      res.status(200).json(user);
    } catch (err) {
      console.error("Error in getCurrentUser:", err);
      res.status(500).json({ error: "Internal server error" });
    }
  }
}

module.exports = RESTuser;
