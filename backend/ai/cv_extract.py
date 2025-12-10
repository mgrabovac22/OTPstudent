from dotenv import load_dotenv
import os
import sys
import json
from os.path import join
from langchain_community.document_loaders import PyPDFLoader

env_path = join("resources", ".env")

load_dotenv(env_path)

def load_cv_text(path):
    if path.lower().endswith(".pdf"):
        loader = PyPDFLoader(path)
        docs = loader.load()
        return "\n".join(d.page_content for d in docs)
    with open(path, "r", encoding="utf-8") as f:
        return f.read()

def main():
    raw = sys.stdin.read()
    data = json.loads(raw)
    cv_path = data.get("cv_path")
    if not cv_path:
        sys.stdout.write(json.dumps({"error": "cv_path required"}))
        sys.stdout.flush()
        return
    cv_text = load_cv_text(cv_path)
    out = {"cv_text": cv_text}
    sys.stdout.write(json.dumps(out))
    sys.stdout.flush()

if __name__ == "__main__":
    main()