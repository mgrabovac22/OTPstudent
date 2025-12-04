const JobDAO = require("../dao/jobsDAO.js");

class RESTjobs {
    constructor() {
        this.jobDAO = new JobDAO();
    }

    async listJobs(req, res) {
        res.type("application/json");

        try {
            const jobs = await this.jobDAO.getAllJobs();
            res.status(200).json(jobs);
        } catch (err) {
            console.error("Error fetching jobs:", err);
            res.status(500).json({ error: "Internal server error" });
        }
    }

    async getJobDetails(req, res) {
        res.type("application/json");

        try {
            const id = parseInt(req.params.id, 10);
            if (isNaN(id)) {
                return res.status(400).json({ error: "Invalid job ID" });
            }

            const rows = await this.jobDAO.getJobDetailsById(id);

            if (!rows || rows.length === 0) {
                return res.status(404).json({ error: "Job not found" });
            }

            res.status(200).json(rows[0]);
        } catch (err) {
            console.error("Error fetching job:", err);
            res.status(500).json({ error: "Internal server error" });
        }
    }
}

module.exports = RESTjobs;
