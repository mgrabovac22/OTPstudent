const DB = require("../db/database.js");

class InternshipDAO {
    constructor() {
        this.db = new DB();
    }

    async nextApplicationId() {
        const rows = await this.db.executeQuery("SELECT COALESCE(MAX(id),0)+1 AS nextId FROM Student_Internship_Application");
        return rows[0].nextId;
    }

    async createApplication(app) {
        const sql = `
            INSERT INTO Student_Internship_Application (
                id, studentExpectations, studentAdress, contactNumber,
                dateOfApplication, duration, expectedStartDate, expectedEndDate, User_id
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        `;
        return this.db.executeQuery(sql, [
            app.id,
            app.studentExpectations,
            app.studentAdress,
            app.contactNumber,
            app.dateOfApplication,
            app.duration,
            app.expectedStartDate,
            app.expectedEndDate,
            app.userId
        ]);
    }

    async addExpectedJobs(applicationId, jobIds) {
        if (!jobIds || !jobIds.length) return;
        const values = jobIds.map(jobId => [applicationId, jobId]);
        const sql = `
            INSERT INTO Student_Internship_Application_Expected_Job (
                Student_Internship_Application_id, Student_Internship_Job_id
            ) VALUES ?
        `;
        return this.db.pool.query(sql, [values]);
    }

    async listApplicationsByUser(userId) {
        const sql = `
            SELECT a.id, a.studentExpectations, a.studentAdress, a.contactNumber,
                   a.dateOfApplication, a.duration, a.expectedStartDate, a.expectedEndDate,
                   GROUP_CONCAT(e.Student_Internship_Job_id) AS expectedJobs
            FROM Student_Internship_Application a
            LEFT JOIN Student_Internship_Application_Expected_Job e
              ON a.id = e.Student_Internship_Application_id
            WHERE a.User_id = ?
            GROUP BY a.id
            ORDER BY a.dateOfApplication DESC
        `;
        return this.db.executeQuery(sql, [userId]);
    }

    async getApplication(userId, applicationId) {
        const sql = `
            SELECT a.id, a.studentExpectations, a.studentAdress, a.contactNumber,
                   a.dateOfApplication, a.duration, a.expectedStartDate, a.expectedEndDate,
                   GROUP_CONCAT(e.Student_Internship_Job_id) AS expectedJobs
            FROM Student_Internship_Application a
            LEFT JOIN Student_Internship_Application_Expected_Job e
              ON a.id = e.Student_Internship_Application_id
            WHERE a.User_id = ? AND a.id = ?
            GROUP BY a.id
            LIMIT 1
        `;
        const rows = await this.db.executeQuery(sql, [userId, applicationId]);
        return rows[0];
    }

    async deleteApplication(userId, applicationId) {
        await this.db.executeQuery(
            "DELETE FROM Student_Internship_Application_Expected_Job WHERE Student_Internship_Application_id = ?",
            [applicationId]
        );
        return this.db.executeQuery(
            "DELETE FROM Student_Internship_Application WHERE id = ? AND User_id = ?",
            [applicationId, userId]
        );
    }

    async listInternshipJobs() {
        return this.db.executeQuery("SELECT id, name FROM Student_Internship_Job ORDER BY id");
    }
}

module.exports = InternshipDAO;