const { spawn } = require("child_process")
const path = require("path")

const backendRoot = path.join(__dirname, "..", "..")
const pythonBin = process.env.PYTHON_BIN

function extractCvText(cvPath) {
  return new Promise((resolve, reject) => {
    const child = spawn(pythonBin, ["ai/cv_extract.py"], {
      cwd: backendRoot
    })

    let out = ""
    let err = ""

    child.stdout.on("data", chunk => { out += chunk.toString() })
    child.stderr.on("data", chunk => { err += chunk.toString() })

    child.on("close", code => {
      if (code !== 0) return reject(new Error(err || `cv_extract exit ${code}`))
      try {
        const json = JSON.parse(out)
        if (json.error) return reject(new Error(json.error))
        resolve(json.cv_text)
      } catch (e) {
        reject(e)
      }
    })

    child.stdin.write(JSON.stringify({ cv_path: cvPath }))
    child.stdin.end()
  })
}

module.exports = { extractCvText }