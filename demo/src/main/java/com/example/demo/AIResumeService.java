package com.example.demo;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

@Service
public class AIResumeService {

    // 🔴 YOUR NEW API KEY
    private String apiKey = "AIzaSyDBD2trXKF9T4e_hT_NHcbzXZhzp60gPUQ"; 

    // ---------------------------------------------------------
    // METHOD 1: RESUME ANALYZER
    // ---------------------------------------------------------
    public String analyzeResume(String resumeText) {
        // ✅ FIXED: Using 'gemini-1.5-flash' (The correct official name)
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        // 1. Create Request Body
        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> contentPart = new HashMap<>();
        List<Map<String, Object>> parts = new ArrayList<>();
        Map<String, Object> textPart = new HashMap<>();

        // 🧠 SCORING PROMPT
        String prompt = "You are a critical HR Resume Evaluator. Analyze the resume text below.\n" +
                        "SCORING CRITERIA (0-100):\n" +
                        "1. Impact (0-40 pts): Does the candidate use numbers, % growth, or specific achievements?\n" +
                        "2. Keywords (0-30 pts): Does the resume have strong industry-specific technical keywords?\n" +
                        "3. Structure (0-30 pts): Is it professional and clear?\n\n" +
                        "INSTRUCTIONS:\n" +
                        "- Be strict. Average resumes get 65-75. Good ones get 80+. Only perfect resumes get 90+.\n" +
                        "- Do NOT give everyone the same score. Base it on CONTENT depth.\n" +
                        "OUTPUT FORMAT:\n" +
                        "ATS Score: [Calculated Number]/100\n" +
                        "## Missing Keywords\n" +
                        "- [Keyword 1]\n" +
                        "- [Keyword 2]\n" +
                        "## Improvements\n" +
                        "- [Specific Advice 1]\n" +
                        "- [Specific Advice 2]\n\n" +
                        "Resume Text:\n" + resumeText;

        textPart.put("text", prompt);
        parts.add(textPart);
        contentPart.put("parts", parts);
        contents.add(contentPart);
        requestBody.put("contents", contents);

        // B. Temperature 0.4
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.4); 
        generationConfig.put("maxOutputTokens", 500);
        requestBody.put("generationConfig", generationConfig);

        // 2. Send to Google
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            Map body = response.getBody();
            List<Map> candidates = (List<Map>) body.get("candidates");
            Map content = (Map) candidates.get(0).get("content");
            List<Map> partsResponse = (List<Map>) content.get("parts");
            return (String) partsResponse.get(0).get("text");
        } catch (Exception e) {
            return "Error talking to AI: " + e.getMessage();
        }
    }

    // ---------------------------------------------------------
    // METHOD 2: MOCK INTERVIEW CHAT
    // ---------------------------------------------------------
    public String getInterviewResponse(String userMessage) {
        // ✅ FIXED: Using 'gemini-1.5-flash'
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        String prompt = "You are a professional Technical Interviewer. The candidate says: \"" + userMessage + "\". " +
                        "Reply with a short, constructive response or a follow-up technical question. " +
                        "Do not include markdown formatting like ** or ##. Keep it conversational.";

        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> contentPart = new HashMap<>();
        List<Map<String, Object>> parts = new ArrayList<>();
        Map<String, Object> textPart = new HashMap<>();
        
        textPart.put("text", prompt);
        parts.add(textPart);
        contentPart.put("parts", parts);
        contents.add(contentPart);
        requestBody.put("contents", contents);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            Map body = response.getBody();
            List<Map> candidates = (List<Map>) body.get("candidates");
            Map content = (Map) candidates.get(0).get("content");
            List<Map> partsResponse = (List<Map>) content.get("parts");
            return (String) partsResponse.get(0).get("text");
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    // ---------------------------------------------------------
    // METHOD 3: GENERATE QUESTIONS
    // ---------------------------------------------------------
    public String generateQuestions(String jobRole) {
        // ✅ FIXED: Using 'gemini-1.5-flash'
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        String prompt = "List 5 likely technical interview questions for a '" + jobRole + "' role. " +
                        "Include 2 basic, 2 intermediate, and 1 advanced question. " +
                        "Output strictly as a numbered list.";

        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> contentPart = new HashMap<>();
        List<Map<String, Object>> parts = new ArrayList<>();
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);
        parts.add(textPart);
        contentPart.put("parts", parts);
        contents.add(contentPart);
        requestBody.put("contents", contents);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            Map body = response.getBody();
            List<Map> candidates = (List<Map>) body.get("candidates");
            Map content = (Map) candidates.get(0).get("content");
            List<Map> partsResponse = (List<Map>) content.get("parts");
            return (String) partsResponse.get(0).get("text");
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}