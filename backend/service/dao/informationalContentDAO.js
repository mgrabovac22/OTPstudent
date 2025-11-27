let DB = require("../db/database.js");

class InformationalContentDAO {
    constructor() {
        this.db = new DB();
    }

    async getContentById(id) {
        return await this.db.executeQuery("SELECT * FROM Informational_Content WHERE id = ?", [id]);
    }

    async getInformationalContent(userId) {
        const sql = `
            SELECT ic.* 
            FROM Informational_Content ic
            WHERE ic.id NOT IN (
                SELECT ur.Informational_Content_id 
                FROM User_Has_Read_Info ur 
                WHERE ur.User_id = ?
            )
        `;

        return await this.db.executeQuery(sql, [userId]);
    }

    async add(content) {
        const sql = `
            INSERT INTO Informational_Content (
                id,
                name,
                description,
                experiencePoints
            ) VALUES (?, ?, ?, ?)
        `;

        return await this.db.executeQuery(sql, [
            content.id,
            content.name,
            content.description,
            content.experiencePoints || 100
        ]);
    }

    async update(id, element, elementValue) {
        const allowedColumns = [
            'name',
            'description',
            'experiencePoints'
        ];

        if (!allowedColumns.includes(element)) {
            return;
        }

        const sql = `UPDATE Informational_Content SET ${element} = ? WHERE id = ?`;
        return await this.db.executeQuery(sql, [elementValue, id]);
    }

    async delete(id) {
        return await this.db.executeQuery("DELETE FROM Informational_Content WHERE id = ?", [id]);
    }
}

module.exports = InformationalContentDAO;