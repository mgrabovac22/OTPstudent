from dotenv import load_dotenv
import os
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
     Ako je dan opis posla, prilagodi savjet tomu:\n{job_description}
     '''),
    ("human",
     '''
     Povijest razgovora:\n{history}\n\n
     Pitanje korisnika o CV-u:\n{question}
     ''')
])

def main():
    history = "Korisnik: Bok, zanima me više o vašoj firmi.\nAsistent: Naravno, postavi pitanje."
    question = "Reci mi nešto o OTP banci"
    
    docs = retriever.invoke(question)
    data = "\n\n".join(d.page_content for d in docs)

    messages = general_prompt.format_messages(
        data=data,
        history=history,
        question=question
    )
    
    response = llm.invoke(messages)
    print(response.content)
    
if __name__ == "__main__":
    main()
