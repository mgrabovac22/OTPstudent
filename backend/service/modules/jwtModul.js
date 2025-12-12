const jwt = require("jsonwebtoken");

function createAccessToken(korisnik) {
    return jwt.sign({ email: korisnik.email }, process.env.JWT_SECRET, { expiresIn: '15s' });
}

function createRefreshToken(korisnik) {
    return jwt.sign({ email: korisnik.email }, process.env.JWT_REFRESH_SECRET, { expiresIn: '7d' });
}

function checkToken(zahtjev, tajniKljucJWT) {
    if (zahtjev.headers.authorization != null) {
        let token = zahtjev.headers.authorization.split(" ")[1] ?? "";
        
        let podaci = jwt.verify(token, tajniKljucJWT);
        return podaci;
    }
    return false;
}

module.exports = { createAccessToken, createRefreshToken, checkToken };