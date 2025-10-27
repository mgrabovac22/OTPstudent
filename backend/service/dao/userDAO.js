let DB = require("../db/database.js");
const bcrypt = require("bcrypt");

class UserDAO {
    constructor() {
        this.db = new DB();
    }

    async getUserByEmail(email) {
        return await this.db.executeQuery("SELECT * FROM user WHERE email = ?", [email]);
    }

    async changePassword(password, email) {
        const hashedPassword = await bcrypt.hash(password, 10);
        const sql = `UPDATE user SET password = ? WHERE email = ?`;
        return await this.db.executeQuery(sql, [hashedPassword, email]);
    }

    async add(user) {
        const sql = `
            INSERT INTO user (
                name,
                lastName,
                email,
                studyYear,
                courseOfStudy,
                password,
                imagePath,
                cvPath,
                birthDate
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        `;

        return await this.db.executeQuery(sql, [
            user.name,
            user.lastName,
            user.email,
            user.studyYear,
            user.courseOfStudy,
            user.password,
            user.imagePath,
            user.cvPath,
            user.birthDate
        ]);
    }

    async update(email, element, elementValue) {
        const allowedColumns = [
            'name',
            'lastName',
            'studyYear',
            'courseOfStudy',
            'password',
            'imagePath',
            'cvPath',
            'birthDate'
        ];

        if (!allowedColumns.includes(element)) {
            throw new Error('Invalid column name');
        }

        const sql = `UPDATE user SET ${element} = ? WHERE email = ?`;
        return await this.db.executeQuery(sql, [elementValue, email]);
    }

    async delete(email) {
        return await this.db.executeQuery("DELETE FROM user WHERE email = ?", [email]);
    }
}

module.exports = UserDAO;
