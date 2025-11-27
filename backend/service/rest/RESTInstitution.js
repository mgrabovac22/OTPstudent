const InstitutionDAO = require("../dao/institutionDAO.js");

class RESTInstitution {
    constructor() {
        this.institutionDAO = new InstitutionDAO();
    }

    async getAllInstitutions(req, res) {
        res.type("application/json");

        const institutions = await this.institutionDAO.getAll();

        res.status(200).json(institutions);
    }
}

module.exports = RESTInstitution;