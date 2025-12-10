const { exit } = require("node:process");
const mysql2 = require("mysql2/promise");
const fs = require("node:fs");
const path = require("node:path");
require("dotenv").config({ path: path.resolve(__dirname, "../../resources/.env") });

async function refreshDatabase() {
    try {

        const connection = await mysql2.createConnection({
            host: process.env.DB_HOST,
            port: process.env.DB_PORT ? Number(process.env.DB_PORT) : 3306,
            user: process.env.DB_USER,
            password: process.env.DB_PASSWORD,
            ssl: process.env.DB_SSL === 'true' || process.env.DB_SSL === 'REQUIRED' ? { rejectUnauthorized: false } : undefined
        });

        await connection.query(`CREATE DATABASE IF NOT EXISTS \`${process.env.DB_NAME}\`;`);
        console.log("Database ensured.");

        const db = await mysql2.createConnection({
            host: process.env.DB_HOST,
            port: process.env.DB_PORT ? Number(process.env.DB_PORT) : 3306,
            user: process.env.DB_USER,
            password: process.env.DB_PASSWORD,
            database: process.env.DB_NAME,
            ssl: process.env.DB_SSL === 'true' || process.env.DB_SSL === 'REQUIRED' ? { rejectUnauthorized: false } : undefined
        });

        const sqlFile = path.resolve(__dirname, "./database.sql");
        const sqlData = fs.readFileSync(sqlFile, "utf-8");

        const statements = sqlData.split(";").map(s => s.trim()).filter(s => s.length);

        for (const stmt of statements) {
            await db.query(stmt);
        }

        console.log("Database has been refreshed.");
        await db.end();
        await connection.end();
        exit(0);

    } catch (err) {
        console.error("Error refreshing database:", err);
        exit(1);
    }
}

(async () => {
    await refreshDatabase();
})();
