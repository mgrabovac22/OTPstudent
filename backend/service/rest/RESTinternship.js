const InternshipDAO = require("../dao/internshipDAO.js");
const UserDAO = require("../dao/userDAO.js");

class RESTinternship {
    constructor() {
        this.internshipDAO = new InternshipDAO();
        this.userDAO = new UserDAO();
    }

    async apply(req, res) {
        res.type("application/json");
        try {
            const email = req.user.email;
            const userRows = await this.userDAO.getUserByEmail(email);
            if (!userRows || !userRows[0]) {
                return res.status(404).json({ error: "User not found" });
            }
            const userId = userRows[0].id;

            const {
                studentExpectations,
                studentAdress,
                contactNumber,
                duration,
                expectedStartDate,
                expectedEndDate,
                expectedJobs = []
            } = req.body;

            if (!studentExpectations || !studentAdress || !contactNumber ||
                !duration || !expectedStartDate || !expectedEndDate) {
                return res.status(400).json({ error: "Missing required fields" });
            }

            const id = await this.internshipDAO.nextApplicationId();
            const application = {
                id,
                studentExpectations,
                studentAdress,
                contactNumber,
                dateOfApplication: new Date().toISOString().slice(0, 10),
                duration,
                expectedStartDate,
                expectedEndDate,
                userId
            };

            await this.internshipDAO.createApplication(application);
            await this.internshipDAO.addExpectedJobs(id, expectedJobs);

            res.status(201).json({ success: "Internship application created", id });
        } catch (err) {
            console.error("Error creating internship application:", err);
            res.status(500).json({ error: "Internal server error" });
        }
    }

    async listUserApplications(req, res) {
        res.type("application/json");
        try {
            const email = req.user.email;
            const userRows = await this.userDAO.getUserByEmail(email);
            if (!userRows || !userRows[0]) {
                return res.status(404).json({ error: "User not found" });
            }
            const userId = userRows[0].id;

            const apps = await this.internshipDAO.listApplicationsByUser(userId);
            res.status(200).json(apps);
        } catch (err) {
            console.error("Error listing applications:", err);
            res.status(500).json({ error: "Internal server error" });
        }
    }

    async getApplication(req, res) {
        res.type("application/json");
        try {
            const email = req.user.email;
            const userRows = await this.userDAO.getUserByEmail(email);
            if (!userRows || !userRows[0]) {
                return res.status(404).json({ error: "User not found" });
            }
            const userId = userRows[0].id;
            const applicationId = parseInt(req.params.id, 10);

            const app = await this.internshipDAO.getApplication(userId, applicationId);
            if (!app) return res.status(404).json({ error: "Application not found" });

            res.status(200).json(app);
        } catch (err) {
            console.error("Error getting application:", err);
            res.status(500).json({ error: "Internal server error" });
        }
    }

    async deleteApplication(req, res) {
        res.type("application/json");
        try {
            const email = req.user.email;
            const userRows = await this.userDAO.getUserByEmail(email);
            if (!userRows || !userRows[0]) {
                return res.status(404).json({ error: "User not found" });
            }
            const userId = userRows[0].id;
            const applicationId = parseInt(req.params.id, 10);

            await this.internshipDAO.deleteApplication(userId, applicationId);
            res.status(200).json({ success: "Application deleted" });
        } catch (err) {
            console.error("Error deleting application:", err);
            res.status(500).json({ error: "Internal server error" });
        }
    }

    async listJobs(req, res) {
        res.type("application/json");
        try {
            const jobs = await this.internshipDAO.listInternshipJobs();
            res.status(200).json(jobs);
        } catch (err) {
            console.error("Error listing internship jobs:", err);
            res.status(500).json({ error: "Internal server error" });
        }
    }
}

module.exports = RESTinternship;