let DB = require("../db/database.js");

class JobDAO {
    constructor() {
        this.db = new DB();
    }

    async getAllJobs() {
        const sql = `
            SELECT 
                sj.id,
                sj.name,
                sj.startDate,
                l.address AS location,
                l.city
            FROM Student_Job sj
            JOIN Location l ON sj.Location_id = l.id
            ORDER BY sj.startDate ASC
        `;
        return await this.db.executeQuery(sql);
    }

    async getJobDetailsById(jobId) {
        const sql = `
            SELECT 
                sj.id,
                sj.name,
                sj.description,
                sj.startDate,
                l.id AS locationId,
                l.address AS location,
                l.city
            FROM Student_Job sj
            JOIN Location l ON sj.Location_id = l.id
            WHERE sj.id = ?
        `;
        return await this.db.executeQuery(sql, [jobId]);
    }

    async add(job) {
        const sql = `
            INSERT INTO Student_Job (name, description, startDate, Location_id)
            VALUES (?, ?, ?, ?)
        `;
        return await this.db.executeQuery(sql, [
            job.name,
            job.description,
            job.startDate,
            job.Location_id
        ]);
    }

    async delete(jobId) {
        return await this.db.executeQuery(
            "DELETE FROM Student_Job WHERE id = ?",
            [jobId]
        );
    }

    async applyToJob(userId, jobId, applicationDate) {
        const sql = `
            INSERT INTO Job_Application (User_id, Student_Job_id, applicationDate)
            VALUES (?, ?, ?)
        `;
        return await this.db.executeQuery(sql, [userId, jobId, applicationDate]);
    }

    async unapplyFromJob(userId, jobId) {
        const sql = `
            DELETE FROM Job_Application
            WHERE User_id = ? AND Student_Job_id = ?
        `;
        return await this.db.executeQuery(sql, [userId, jobId]);
    }

    async getApplicationsByUser(userId) {
        const sql = `
            SELECT ja.Student_Job_id, ja.applicationDate, sj.name, sj.description
            FROM Job_Application ja
            JOIN Student_Job sj ON ja.Student_Job_id = sj.id
            WHERE ja.User_id = ?
            ORDER BY ja.applicationDate DESC
        `;
        return await this.db.executeQuery(sql, [userId]);
    }
}

module.exports = JobDAO;
