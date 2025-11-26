const InformationalContentDAO = require("../dao/informationalContentDAO.js");
const { createAccessToken, checkToken, createRefreshToken } = require("../modules/jwtModul.js");

class RESTinformationalContent {
    constructor() {
        this.InformationalContentDAO = new InformationalContentDAO();
    }

    async post(req, res) {
        res.type("application/json");
        
        const {
          id,
          name,
          description,
          experiencePoints
        } = req.body;
    
        if (
          !id ||
          !name ||
          !description ||
          !experiencePoints
        ) {
          return res.status(400).json({ error: "Required data missing!" });
        }
    
        try {
          const informationalContent = {
            id,
            name,
            description,
            experiencePoints
          };
    
          const response = await this.InformationalContentDAO.add(informationalContent);
    
          if (response) {
            res.status(201).json({ success: "Informational content added successfully!" });
          } else {
            res.status(400).json({ error: "Informational content adding failed!" });
          }
        } catch (err) {
          console.error("Error in postInformationalContent:", err);
          res.status(500).json({ error: "Internal server error" });
        }
    }

    async get(req, res) {
        res.type("application/json");

        try {
            const informationalContent = await this.InformationalContentDAO.getInformationalContent();

            if (!informationalContent) {
                return res.status(404).json({ error: "Informational content not found" });
            }

            res.status(200).json(informationalContent);
        } catch (err) {
            console.error("Error in getInformationalContent:", err);
            res.status(500).json({ error: "Internal server error" });
        }
    }

    async getById(req, res) {
        res.type("application/json");

        try {
            const id = req.params.id;

            const informationalContent = await this.InformationalContentDAO.getContentById(id);

            if (!informationalContent || !informationalContent[0]) {
                return res.status(404).json({ error: "Informational content not found" });
            }

            res.status(200).json(informationalContent[0]);
        } catch (err) {
            console.error("Error in getCurrentInformationalContent:", err);
            res.status(500).json({ error: "Internal server error" });
        }
    }
}

module.exports = RESTinformationalContent;