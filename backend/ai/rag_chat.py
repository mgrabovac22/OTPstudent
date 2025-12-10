from dotenv import load_dotenv
import os
import sys
import json
from os.path import join

from langchain_core.prompts import ChatPromptTemplate
from langchain_openai import ChatOpenAI, OpenAIEmbeddings
from langchain_chroma import Chroma

env_path = join("resources", ".env")

load_dotenv(env_path)

API_KEY = os.environ.get("OPENAI_KEY")

os.environ["OPENAI_API_KEY"] = API_KEY

VECTOR_STORE_DIR = os.environ.get("VECTOR_STORE_DIR")
EMBEDDING_MODEL = os.environ.get("EMBEDDING_MODEL", "text-embedding-3-small")

embeddings = OpenAIEmbeddings(model=EMBEDDING_MODEL)

vector_store = Chroma(
    embedding_function=embeddings,
    persist_directory=VECTOR_STORE_DIR
)

retriever = vector_store.as_retriever(search_kwargs={"k": 4})

llm = ChatOpenAI(model='gpt-4.1-mini')

general_prompt = ChatPromptTemplate.from_messages([
    ("system",
     '''
     Ti si korisni asistent čiji je zadatak odgovarati na pitanja korisnika.\n
     Za svoje odgovore smiješ koristiti isključivo dane podatke: {data}\n
     Ukoliko to nije moguće, nemoj izmišljati činjenice nego daj korisniku do
     znanja da nisi u mogućnosti dati odgovor na pitanje.
     Odgovaraj što sažetije.
     '''),
    ("human",
     '''
     Povijest razgovora: {history}\n
     Pitanje: {question}
     ''')
])

cv_prompt = ChatPromptTemplate.from_messages([
    ("system",
     '''
     Ti si stručnjak za analizu životopisa.\n
     Ovo je CV kandidata:\n{cv_text}\n\n
     Odgovaraj što sažetije.
     '''),
    ("human",
     '''
     Povijest razgovora:\n{history}\n\n
     Pitanje korisnika o CV-u:\n{question}
     ''')
])

def format_history(history):
    return "\n".join(f"{m['role']}: {m['content']}" for m in history)

def handle_general(message, history):
    history_str = format_history(history)
    docs = retriever.invoke(message)
    data = "\n\n".join(d.page_content for d in docs)
    msgs = general_prompt.format_messages(
        data=data,
        history=history_str,
        question=message
    )
    resp = llm.invoke(msgs)
    return resp.content

def handle_cv(message, history, cv_text):
    history_str = format_history(history)
    msgs = cv_prompt.format_messages(
        cv_text=cv_text or "",
        history=history_str,
        question=message
    )
    resp = llm.invoke(msgs)
    return resp.content

def main():
    raw = sys.stdin.read()
    data = json.loads(raw)
    mode = data.get("mode", "general")
    message = data["message"]
    history = data.get("history", [])
    if mode == "cv":
        cv_text = data.get("cv_text")
        answer = handle_cv(message, history, cv_text)
    else:
        answer = handle_general(message, history)
    out = {"answer": answer, "mode": mode}
    sys.stdout.write(json.dumps(out))
    sys.stdout.flush()
    
if __name__ == "__main__":
    main()
