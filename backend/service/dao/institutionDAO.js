let DB = require("../db/database.js");

class InstitutionDAO {
    constructor() {
        this.db = new DB();
    }

    async getAll() {
        return await this.db.executeQuery("SELECT * FROM Higher_Education_Body", []);
    }
}

module.exports = InstitutionDAO;