const JobDAO = require("../dao/jobsDAO.js");

class RESTjobs {
    constructor() {
        this.jobDAO = new JobDAO();
        const UserDAO = require("../dao/userDAO.js");
        this.userDAO = new UserDAO();
    }

    async applyToJob(req, res) {
        res.type("application/json");
        try {
            const email = req.user.email;
            const userRows = await this.userDAO.getUserByEmail(email);
            if (!userRows || !userRows[0]) {
                return res.status(404).json({ error: "User not found" });
            }

            const userId = userRows[0].id;
            const { jobId, Student_Job_id } = req.body;
            const resolvedJobId = jobId || Student_Job_id;
            const applicationDate = new Date().toISOString().slice(0, 10);

            if (!resolvedJobId) {
                return res.status(400).json({ error: "Missing jobId (or Student_Job_id)" });
            }

            await this.jobDAO.applyToJob(userId, resolvedJobId, applicationDate);
            return res.status(201).json({ success: "Job application submitted" });
        } catch (err) {
            console.error("Error applying to job:", err);

            if (err.code === "ER_DUP_ENTRY") {
                return res.status(409).json({
                    error: "Već ste prijavljeni na ovaj posao."
                });
            }

            return res.status(500).json({ error: "Internal server error" });
        }
    }

    async unapplyFromJob(req, res) {
        res.type("application/json");
        try {
            const email = req.user.email;
            const userRows = await this.userDAO.getUserByEmail(email);

            if (!userRows || !userRows[0]) {
                return res.status(404).json({ error: "User not found" });
            }

            const userId = userRows[0].id;

            const { jobId, Student_Job_id } = req.body;
            const resolvedJobId = jobId || Student_Job_id;

            if (!resolvedJobId) {
                return res
                    .status(400)
                    .json({ error: "Missing jobId (or Student_Job_id)" });
            }

            const result = await this.jobDAO.unapplyFromJob(userId, resolvedJobId);

            if (result.affectedRows === 0) {
                return res.status(404).json({ error: "Application not found" });
            }

            return res.status(200).json({
                success: "Prijava na posao je uspješno otkazana."
            });  
        } catch (err) {
            console.error("Error unappplying from job:", err);
            return res.status(500).json({ error: "Internal server error" });
        }
    }

    async getUserApplications(req, res) {
        res.type("application/json");
        try {
            const email = req.user.email;
            const userRows = await this.userDAO.getUserByEmail(email);
            if (!userRows || !userRows[0]) {
                return res.status(404).json({ error: "User not found" });
            }
            const userId = userRows[0].id;
            const applications = await this.jobDAO.getApplicationsByUser(userId);
            res.status(200).json(applications);
        } catch (err) {
            console.error("Error fetching job applications:", err);
            res.status(500).json({ error: "Internal server error" });
        }
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
