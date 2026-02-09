# 🚀 CareerAI - AI-Powered Resume & Interview Platform

A full-stack AI platform that helps job seekers improve their resumes and prepare for technical interviews. Built with Java Spring Boot, React, and Google Gemini AI.

## 🌟 Features

### 1. 📄 AI Resume Scanner (ATS)
* **PDF Parsing:** Extracts text from uploaded PDF resumes.
* **ATS Scoring:** AI calculates a match score (0-100) based on industry standards.
* **Feedback:** Provides strengths, weaknesses, and missing keywords.

### 2. 💬 Mock Interview Chatbot
* **AI Persona:** Acts as a strict technical interviewer.
* **Interactive Chat:** Users can type answers and get real-time feedback.

### 3. 📚 Question Bank Generator
* **Role-Based:** Generates 5 interview questions (Basic to Advanced) for any job title (e.g., "React Developer", "Data Scientist").

### 4. 📊 User Dashboard
* **History Tracking:** Saves all past resume scans and scores to a MySQL database.
* **Progress Monitoring:** View how your resume score improves over time.

## 🛠️ Tech Stack
* **Frontend:** React.js, Glassmorphism UI, CSS Modules
* **Backend:** Java Spring Boot 3 (REST API)
* **Database:** MySQL (Hibernate/JPA)
* **AI Engine:** Google Gemini Flash API
* **Tools:** Apache PDFBox, Maven, Git

## 🚀 How to Run Locally

### Backend (Java)
1.  Update `application.properties` with your MySQL credentials.
2.  Add your API Key in `AIResumeService.java`.
3.  Run: `mvn spring-boot:run`

### Frontend (React)
1.  `cd frontend`
2.  `npm install`
3.  `npm start`

---
*Built by [Aditya Dehade]*