const InformationalContentDAO = require("../dao/informationalContentDAO.js");
const UserReadDAO = require("../dao/userReadDAO.js")
const UserDAO = require("../dao/userDAO.js")

class RestUserRead {
    constructor() {
        this.userReadDAO = new UserReadDAO();
        this.informationalContentDAO = new InformationalContentDAO();
        this.userDAO = new UserDAO();
    }

    async markContentRead(req, res) {
        res.type("application/json");

        const { userId, contentId } = req.body;

        if (!userId || !contentId) {
            return res.status(400).json({ error: "Missing userId or contentId" });
        }

        try {
            const isAlreadyRead = await this.userReadDAO.checkIfRead(userId, contentId);

            if (isAlreadyRead) {
                return res.status(409).json({ message: "The user has already read this content" });
            }

            const contentList = await this.informationalContentDAO.getContentById(contentId);
            
            if (!contentList || contentList.length === 0) {
                return res.status(404).json({ error: "Content not found" });
            }

            const pointsToAdd = contentList[0].experiencePoints;

            await this.userReadDAO.insertReadRecord(userId, contentId);

            await this.userDAO.addExperiencePoints(userId, pointsToAdd);

            return res.status(200).json({ 
                success: true, 
                message: "Content marked as read", 
                pointsAdded: pointsToAdd 
            });

        } catch (err) {
            console.error("Error in markContentRead:", err);
            return res.status(500).json({ error: "Internal server error" });
        }
    }
}

module.exports = RestUserRead;