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
                l.address AS location
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
                l.address AS location
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
}

module.exports = JobDAO;
