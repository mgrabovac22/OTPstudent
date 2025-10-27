const jwt = require("jsonwebtoken");

function createToken(korisnik, tajniKljucJWT){
	let token = jwt.sign({ oib: korisnik.oib }, tajniKljucJWT, { expiresIn: `15s` });
  return token;
}

function checkToken(zahtjev, tajniKljucJWT) {
    if (zahtjev.headers.authorization != null) {
        let token = zahtjev.headers.authorization.split(" ")[1] ?? "";
        try {
            let podaci = jwt.verify(token, tajniKljucJWT);
            return podaci;
        } catch (e) {
            console.log(e)
            return false;
        }
    }
    return false;
}

module.exports = { createToken, checkToken };
