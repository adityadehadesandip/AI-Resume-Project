import React, { useState, useEffect } from "react";

function App() {
  const [activeTab, setActiveTab] = useState("resume");

  // --- STATE VARIABLES ---
  const [file, setFile] = useState(null);
  const [resumeResult, setResumeResult] = useState(null);
  const [loadingResume, setLoadingResume] = useState(false);
  const [score, setScore] = useState(null);

  const [chatMessage, setChatMessage] = useState("");
  const [chatHistory, setChatHistory] = useState([
    { sender: "ai", text: "Hello! I am your AI Interviewer. What role are you applying for?" }
  ]);
  const [loadingChat, setLoadingChat] = useState(false);

  const [jobRole, setJobRole] = useState("");
  const [questions, setQuestions] = useState(null);
  const [loadingQuestions, setLoadingQuestions] = useState(false);

  const [history, setHistory] = useState([]);

  // --- LOGIC HANDLERS ---
  const handleFileChange = (e) => setFile(e.target.files[0]);

  const handleUpload = async () => {
    if (!file) return alert("Please select a PDF file first.");
    setLoadingResume(true);
    const formData = new FormData();
    formData.append("file", file);
    try {
      // ✅ FIXED: Changed "/analyze" to "/upload" to match your Java Controller
      const response = await fetch("https://ai-resume-project-whrn.onrender.com/api/resume/upload", { method: "POST", body: formData });
      
      if (response.ok) {
        const data = await response.json();
        setResumeResult(data);
        
        // Extract Score if present
        const match = data.analysis.match(/ATS Score:\s*(\d+)/);
        if (match) setScore(match[1]);
        
        // Refresh History
        fetchHistory();
      } else {
        alert("Server Error: " + response.statusText);
      }
    } catch (e) { alert("Backend not connected. Please wait for Render to wake up."); }
    setLoadingResume(false);
  };

  const sendMessage = async () => {
    if (!chatMessage) return;
    const userMsg = { sender: "user", text: chatMessage };
    setChatHistory([...chatHistory, userMsg]);
    setChatMessage("");
    setLoadingChat(true);
    try {
      // ✅ Chat Link
      const response = await fetch("https://ai-resume-project-whrn.onrender.com/api/chat", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ message: chatMessage }),
      });
      const data = await response.json();
      setChatHistory(prev => [...prev, { sender: "ai", text: data.reply }]);
    } catch (e) { setChatHistory(prev => [...prev, { sender: "ai", text: "❌ Connection Error." }]); }
    setLoadingChat(false);
  };

  const generateQuestions = async () => {
    if (!jobRole) return;
    setLoadingQuestions(true);
    try {
      // ✅ Questions Link
      const response = await fetch("https://ai-resume-project-whrn.onrender.com/api/chat/questions", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ role: jobRole }),
      });
      const data = await response.json();
      setQuestions(data.questions);
    } catch (e) { alert("Backend error"); }
    setLoadingQuestions(false);
  };

  const fetchHistory = async () => {
    try {
      // ✅ History Link
      const response = await fetch("https://ai-resume-project-whrn.onrender.com/api/resume/history");
      const data = await response.json();
      setHistory(data);
    } catch (error) { console.error("Error fetching history"); }
  };

  useEffect(() => {
    if (activeTab === "dashboard") fetchHistory();
  }, [activeTab]);

  const formatText = (text) => {
    if (!text) return null;
    if (text.includes("ATS Score:")) return null;
    return text.split("\n").map((line, i) => <p key={i} style={{ marginBottom: "8px" }}>{line.replace(/\*\*/g, "")}</p>);
  };

  // --- RENDER ---
  return (
    <div style={styles.appContainer}>
      
      {/* 🟦 TOP NAVBAR */}
      <nav style={styles.navbar}>
        <div style={styles.logo}>🚀 CareerAI</div>
        <div style={styles.navLinks}>
          <button onClick={() => setActiveTab("resume")} style={activeTab === "resume" ? styles.navBtnActive : styles.navBtn}>Resume Scan</button>
          <button onClick={() => setActiveTab("chat")} style={activeTab === "chat" ? styles.navBtnActive : styles.navBtn}>Mock Interview</button>
          <button onClick={() => setActiveTab("questions")} style={activeTab === "questions" ? styles.navBtnActive : styles.navBtn}>Study Mode</button>
          <button onClick={() => setActiveTab("dashboard")} style={activeTab === "dashboard" ? styles.navBtnActive : styles.navBtn}>History</button>
        </div>
      </nav>

      {/* ⬜ MAIN CONTENT AREA */}
      <div style={styles.mainContent}>
        
        {/* TAB 1: RESUME */}
        {activeTab === "resume" && (
          <div style={styles.card}>
            <h2 style={styles.header}>Resume AI Scanner</h2>
            <p style={styles.subHeader}>Upload your resume to get an ATS Score & Feedback</p>
            
            <div style={styles.uploadBox}>
              <input type="file" accept=".pdf" onChange={handleFileChange} style={styles.fileInput} />
              <button onClick={handleUpload} disabled={loadingResume} style={styles.primaryBtn}>
                {loadingResume ? "⚙️ Analyzing..." : "✨ Analyze Resume"}
              </button>
            </div>

            {score && (
              <div style={styles.scoreContainer}>
                <div style={{ ...styles.scoreCircle, background: score >= 75 ? "conic-gradient(#2ecc71 0% 100%)" : "conic-gradient(#f1c40f 0% 100%)" }}>
                  <span style={styles.scoreText}>{score}</span>
                </div>
                <p style={{ marginTop: "10px", fontWeight: "bold", color: "#555" }}>ATS Match Score</p>
              </div>
            )}

            {resumeResult && <div style={styles.resultBox}>{formatText(resumeResult.analysis)}</div>}
          </div>
        )}

        {/* TAB 2: CHAT */}
        {activeTab === "chat" && (
          <div style={{ ...styles.card, height: "600px", display: "flex", flexDirection: "column" }}>
            <h2 style={styles.header}>🤖 AI Interviewer</h2>
            <div style={styles.chatWindow}>
              {chatHistory.map((msg, index) => (
                <div key={index} style={msg.sender === "user" ? styles.userMsg : styles.aiMsg}>
                  {msg.text}
                </div>
              ))}
            </div>
            <div style={styles.chatInputArea}>
              <input 
                value={chatMessage} 
                onChange={(e) => setChatMessage(e.target.value)} 
                onKeyPress={(e) => e.key === 'Enter' && sendMessage()}
                placeholder="Type your answer..." 
                style={styles.inputField} 
              />
              <button onClick={sendMessage} disabled={loadingChat} style={styles.sendBtn}>Send</button>
            </div>
          </div>
        )}

        {/* TAB 3: QUESTIONS */}
        {activeTab === "questions" && (
          <div style={styles.card}>
            <h2 style={styles.header}>📚 Question Bank</h2>
            <div style={{ display: "flex", gap: "10px", marginBottom: "20px" }}>
              <input 
                value={jobRole} 
                onChange={(e) => setJobRole(e.target.value)} 
                placeholder="e.g. React Developer" 
                style={styles.inputField} 
              />
              <button onClick={generateQuestions} disabled={loadingQuestions} style={styles.primaryBtn}>
                {loadingQuestions ? "Generating..." : "Get Questions"}
              </button>
            </div>
            {questions && <div style={styles.resultBox}>{formatText(questions)}</div>}
          </div>
        )}

        {/* TAB 4: DASHBOARD */}
        {activeTab === "dashboard" && (
          <div style={styles.card}>
            <h2 style={styles.header}>📊 Your Progress</h2>
            {history.length === 0 ? <p>No history found.</p> : (
              <table style={styles.table}>
                <thead>
                  <tr style={{ background: "#f8f9fa" }}>
                    <th style={styles.th}>ID</th>
                    <th style={styles.th}>Score</th>
                    <th style={styles.th}>Feedback Snippet</th>
                  </tr>
                </thead>
                <tbody>
                  {history.map((item) => (
                    <tr key={item.id} style={{ borderBottom: "1px solid #eee" }}>
                      <td style={styles.td}>#{item.id}</td>
                      <td style={{ ...styles.td, fontWeight: "bold", color: item.atsScore >= 70 ? "green" : "orange" }}>
                        {item.atsScore}/100
                      </td>
                      <td style={styles.td}>{item.analysis ? item.analysis.substring(0, 60) + "..." : "No data"}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        )}

      </div>
    </div>
  );
}

// 🎨 STYLING
const styles = {
  appContainer: { fontFamily: "'Inter', sans-serif", backgroundColor: "#f0f2f5", minHeight: "100vh", paddingBottom: "50px" },
  navbar: { display: "flex", justifyContent: "space-between", alignItems: "center", padding: "15px 40px", background: "linear-gradient(90deg, #1e3c72 0%, #2a5298 100%)", color: "white", boxShadow: "0 4px 12px rgba(0,0,0,0.1)" },
  logo: { fontSize: "24px", fontWeight: "bold", letterSpacing: "1px" },
  navLinks: { display: "flex", gap: "20px" },
  navBtn: { background: "transparent", border: "none", color: "rgba(255,255,255,0.7)", fontSize: "16px", cursor: "pointer", padding: "8px 12px", transition: "0.3s" },
  navBtnActive: { background: "rgba(255,255,255,0.2)", borderRadius: "8px", border: "none", color: "white", fontSize: "16px", cursor: "pointer", padding: "8px 12px", fontWeight: "bold" },
  
  mainContent: { maxWidth: "900px", margin: "40px auto", padding: "0 20px" },
  card: { background: "white", borderRadius: "16px", padding: "40px", boxShadow: "0 10px 40px rgba(0,0,0,0.08)", transition: "0.3s" },
  header: { color: "#1e3c72", marginBottom: "10px", fontSize: "28px" },
  subHeader: { color: "#7f8c8d", marginBottom: "30px" },
  
  uploadBox: { border: "2px dashed #cbd5e0", padding: "40px", borderRadius: "12px", textAlign: "center", background: "#f8f9fa", marginBottom: "30px" },
  fileInput: { marginBottom: "20px" },
  primaryBtn: { background: "linear-gradient(90deg, #4A90E2 0%, #007AFF 100%)", color: "white", border: "none", padding: "12px 24px", fontSize: "16px", borderRadius: "8px", cursor: "pointer", boxShadow: "0 4px 12px rgba(74, 144, 226, 0.3)", transition: "transform 0.2s" },
  
  scoreContainer: { textAlign: "center", margin: "30px 0" },
  scoreCircle: { width: "120px", height: "120px", borderRadius: "50%", display: "flex", alignItems: "center", justifyContent: "center", margin: "0 auto", color: "white", fontSize: "36px", fontWeight: "bold", boxShadow: "0 10px 20px rgba(0,0,0,0.15)" },
  
  resultBox: { background: "#f8f9fa", padding: "20px", borderRadius: "12px", borderLeft: "5px solid #4A90E2", marginTop: "20px" },
  
  chatWindow: { flex: 1, overflowY: "auto", padding: "20px", background: "#f8f9fa", borderRadius: "12px", marginBottom: "20px", display: "flex", flexDirection: "column", gap: "10px" },
  userMsg: { alignSelf: "flex-end", background: "#007AFF", color: "white", padding: "12px 18px", borderRadius: "18px 18px 0 18px", maxWidth: "70%", boxShadow: "0 2px 5px rgba(0,0,0,0.1)" },
  aiMsg: { alignSelf: "flex-start", background: "white", color: "#333", padding: "12px 18px", borderRadius: "18px 18px 18px 0", maxWidth: "70%", border: "1px solid #e1e4e8", boxShadow: "0 2px 5px rgba(0,0,0,0.05)" },
  
  chatInputArea: { display: "flex", gap: "10px" },
  inputField: { flex: 1, padding: "12px", borderRadius: "8px", border: "1px solid #ced4da", fontSize: "16px", outline: "none" },
  sendBtn: { background: "#1e3c72", color: "white", border: "none", padding: "12px 24px", borderRadius: "8px", cursor: "pointer" },

  table: { width: "100%", borderCollapse: "collapse", marginTop: "20px" },
  th: { padding: "15px", textAlign: "left", borderBottom: "2px solid #ddd", color: "#555" },
  td: { padding: "15px", borderBottom: "1px solid #eee", color: "#333" }
};

export default App;