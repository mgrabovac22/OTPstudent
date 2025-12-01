let DB = require("../db/database.js");

class UserReadDAO {
    constructor() {
        this.db = new DB();
    }

    async checkIfRead(userId, contentId) {
        const sql = `
            SELECT 1 
            FROM User_Has_Read_Info 
            WHERE User_id = ? AND Informational_Content_id = ?
        `;
        
        const result = await this.db.executeQuery(sql, [userId, contentId]);
        
        return result && result.length > 0;
    }

    async insertReadRecord(userId, contentId) {
        const sql = `
            INSERT INTO User_Has_Read_Info (
                User_id,
                Informational_Content_id
            ) VALUES (?, ?)
        `;

        return await this.db.executeQuery(sql, [
            userId,
            contentId
        ]);
    }

    async getReadContentIdsByUser(userId) {
        const sql = "SELECT Informational_Content_id FROM User_Has_Read_Info WHERE User_id = ?";
        const results = await this.db.executeQuery(sql, [userId]);
        
        return results.map(row => row.Informational_Content_id);
    }
}

module.exports = UserReadDAO;