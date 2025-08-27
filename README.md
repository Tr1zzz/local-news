# 📰 Local News — AI-powered Geo-Personalized News Aggregator  

**Live Demo:** [http://13.61.185.141](http://13.61.185.141)  

---

## 🌍 Overview  
**Local News** is a full-stack platform that ingests, classifies, and serves news articles, distinguishing between **Local** and **Global** relevance.  
Using **OpenAI gpt-4o-mini**, the system maps articles to the correct U.S. city with confidence scoring and fallbacks.  

This project was built solo as a demonstration of **clean architecture**, **AI integration**, and **production deployment**.  

---

## ✨ Features  
- 🔎 **AI-powered classification**  
  - Distinguishes Local vs Global articles  
  - Resolves city/state using OpenAI pipeline  
- 🏙 **City mapping**  
  - Links articles to a normalized database of U.S. cities (CSV import with population)  
- 🔄 **Ingestion pipeline**  
  - Fetches RSS feeds  
  - Deduplicates entries  
  - Stores raw and classified news in PostgreSQL  
- ⚙️ **Backend services**  
  - Built with **Spring Boot 3** and **JPA/Hibernate**  
  - Clean architecture (Domain, Application, Infrastructure)  
- 💻 **Frontend UI**  
  - Responsive **React + TypeScript (Vite)** app  
  - City search with autocomplete  
  - Local / Global / All filters  
  - Pagination for browsing news  
- 🚀 **Production deployment**  
  - Hosted on **AWS EC2**  
  - **Docker Compose** for multi-service orchestration  
  - **Nginx** reverse proxy  
  - Health checks via **Spring Actuator**  

---

## 🛠 Tech Stack  
**Backend**  
- Java 21  
- Spring Boot 3  
- JPA / Hibernate  
- PostgreSQL 15  

**AI Integration**  
- OpenAI gpt-4o-mini  

**Frontend**  
- React  
- TypeScript  
- Vite  
- Tailwind CSS  

**Deployment**  
- Docker Compose  
- Nginx  
- AWS EC2  

---

## ⚡️ Getting Started  

### Clone the repository  
```bash
git clone <your-repo-link>
cd local-news
```
### Backend (Spring Boot + PostgreSQL)
```bash
./mvnw spring-boot:run
```

### Frontend (React + Vite)
```bash
cd frontend
npm install
npm run dev
```

### Access the app
Frontend: http://localhost:5173
Backend: http://localhost:8080
