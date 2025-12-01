let DB = require("../db/database.js");
const bcrypt = require("bcrypt");

class UserDAO {
    constructor() {
        this.db = new DB();
    }

    async getUserByEmail(email) {
        return await this.db.executeQuery("SELECT * FROM User WHERE email = ?", [email]);
    }

    async changePassword(password, email) {
        const hashedPassword = await bcrypt.hash(password, 10);
        const sql = `UPDATE User SET password = ? WHERE email = ?`;
        return await this.db.executeQuery(sql, [hashedPassword, email]);
    }

    async add(user) {
        const sql = `
            INSERT INTO User (
                firstName,
                lastName,
                email,
                yearOfStudy,
                areaOfStudy,
                password,
                imagePath,
                cvPath,
                dateOfBirth,
                Higher_Education_Body_id,
                experiencePoints,
                unlockedLevel
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        `;

        return await this.db.executeQuery(sql, [
            user.firstName,
            user.lastName,
            user.email,
            user.yearOfStudy,
            user.areaOfStudy,
            user.password,
            user.imagePath || null,
            user.cvPath || null,
            user.dateOfBirth,
            user.HigherEducationBodyId || 1,
            user.experiencePoints || 0,
            user.unlockedLevel || 1
        ]);
    }

    async update(email, element, elementValue) {
        const allowedColumns = [
            'firstName',
            'lastName',
            'yearOfStudy',
            'areaOfStudy',
            'password',
            'imagePath',
            'cvPath',
            'dateOfBirth'
        ];

        if (!allowedColumns.includes(element)) {
            return;
        }

        const sql = `UPDATE User SET ${element} = ? WHERE email = ?`;
        return await this.db.executeQuery(sql, [elementValue, email]);
    }

    async addExperiencePoints(userId, points) {
        const sql = `
            UPDATE User 
            SET experiencePoints = experiencePoints + ? 
            WHERE id = ?
        `;
        
        return await this.db.executeQuery(sql, [points, userId]);
    }

    async delete(email) {
        return await this.db.executeQuery("DELETE FROM User WHERE email = ?", [email]);
    }
}

module.exports = UserDAO;
