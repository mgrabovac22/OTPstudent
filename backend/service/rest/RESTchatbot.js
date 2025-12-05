const { spawn } = require("child_process")
const path = require("path")

class RESTchatbot {
    constructor() {
        this.pythonBin = process.env.PYTHON_BIN
        this.backendRoot = path.join(__dirname, "..", "..")
    }

    callRagChat(payload) {
        return new Promise((resolve, reject) => {
            const child = spawn(this.pythonBin, ["ai/rag_chat.py"], {
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
                    return reject(new Error(err || `rag_chat exit code ${code}`))
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

    async post(req, res) {
        res.type("application/json")

        const { message, mode, history, cvText, jobDescription } = req.body

        if (!message) {
            return res.status(400).json({ error: "Message is required" })
        }

        const payload = {
            mode: mode || "general",
            message,
            history: history || [],
            cv_text: cvText || null,
            job_description: jobDescription || null
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
