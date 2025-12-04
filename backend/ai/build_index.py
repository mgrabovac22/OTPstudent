import os
from dotenv import load_dotenv
from langchain_community.document_loaders import PyPDFLoader, TextLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_openai import OpenAIEmbeddings
from langchain_chroma import Chroma

env_path = os.path.join("resources", ".env")

load_dotenv(env_path)

API_KEY = os.environ.get("OPENAI_KEY")

os.environ["OPENAI_API_KEY"] = API_KEY

KNOWLEDGE_BASE_DIR = os.environ.get("KNOWLEDGE_BASE_DIR")
VECTOR_STORE_DIR = os.environ.get("VECTOR_STORE_DIR")
EMBEDDING_MODEL = os.environ.get("EMBEDDING_MODEL")

def load_docs():
    docs = []
    for root, _, files in os.walk(KNOWLEDGE_BASE_DIR):
        for name in files:
            path = os.path.join(root, name)
            if name.lower().endswith(".pdf"):
                loader = PyPDFLoader(path)
                docs.extend(loader.load())
            elif name.lower().endswith(".txt"):
                loader = TextLoader(path, encoding="utf-8")
                docs.extend(loader.load())
                
    return docs

def main():
    docs = load_docs()
    splitter = RecursiveCharacterTextSplitter(chunk_size=1000, chunk_overlap=200)
    chunks = splitter.split_documents(docs)
    embeddings = OpenAIEmbeddings(model=EMBEDDING_MODEL)
    Chroma.from_documents(
        documents=chunks,
        embedding=embeddings,
        persist_directory=VECTOR_STORE_DIR
    )

if __name__ == "__main__":
    main()