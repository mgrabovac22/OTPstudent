const { spawn } = require("child_process")
const path = require("path")
const UserDAO = require("../dao/userDAO.js")

class RESTchatbot {
    constructor() {
        this.pythonBin = process.env.PYTHON_BIN
        this.backendRoot = path.join(__dirname, "..", "..")
        this.userDAO = new UserDAO()
        this.cvCache = new Map()
        if (!this.pythonBin) {
            throw new Error("PYTHON_BIN env var is not set")
        }
    }

    callPython(script, payload) {
        return new Promise((resolve, reject) => {
            const child = spawn(this.pythonBin, [script], {
                cwd: this.backendRoot
            })

            let out = ""
            let err = ""

            child.stdout.on("data", chunk => {
                out += chunk.toString()
            })

            child.stderr.on("data", chunk => {
                err += chunk.toString()
            })

            child.on("close", code => {
                if (code !== 0) {
                    return reject(new Error(err || `${script} exit code ${code}`))
                }
                try {
                    const json = JSON.parse(out)
                    resolve(json)
                } catch (e) {
                    reject(e)
                }
            })

            child.stdin.write(JSON.stringify(payload))
            child.stdin.end()
        })
    }

    async getOrLoadCvText(userKey, cvPath) {
        if (this.cvCache.has(userKey)) {
            return this.cvCache.get(userKey)
        }
        const result = await this.callPython("ai/cv_extract.py", { cv_path: cvPath })
        const cvText = result.cv_text
        this.cvCache.set(userKey, cvText)
        return cvText
    }

    async callRagChat(payload) {
        return this.callPython("ai/rag_chat.py", payload)
    }

    async post(req, res) {
        res.type("application/json")

        const { message, mode, history, email } = req.body

        if (!message) {
            return res.status(400).json({ error: "Message is required" })
        }

        let finalMode = mode || "general"
        let cvText = null

        const lower = message.toLowerCase()
        const mentionsCv =
            lower.includes("moj cv") ||
            lower.includes("moj životopis") ||
            lower.includes("moj zivotopis") ||
            lower.includes("my cv")

        if (mentionsCv) {
            finalMode = "cv"

            if (!email) {
                return res.status(400).json({ error: "Email is required for CV questions" })
            }

            const rows = await this.userDAO.getUserByEmail(email)

            const user = Array.isArray(rows) ? rows[0] : rows

            if (!user || !user.cvPath) {
                return res.status(400).json({ error: "User CV not uploaded" })
            }

            let relativeCvPath = user.cvPath

            if (relativeCvPath.startsWith("/")) {
                relativeCvPath = relativeCvPath.slice(1)
            }

            const cvPath = path.join(this.backendRoot, relativeCvPath)

            const userKey = user.id || email
            cvText = await this.getOrLoadCvText(userKey, cvPath)

        }

        const payload = {
            mode: finalMode,
            message,
            history: history || [],
            cv_text: cvText
        }

        try {
            const result = await this.callRagChat(payload)
            res.status(200).json(result)
        } catch (err) {
            console.error("Error in RESTchatbot.post:", err)
            res.status(500).json({ error: "Internal server error" })
        }
    }
}

module.exports = RESTchatbot