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

    // 🔴 YOUR API KEY
    private String apiKey = "AIzaSyAuglG7FK15MUFH4XiD5SjG_MvZWNcNjdo"; 

    // ---------------------------------------------------------
    // METHOD 1: RESUME ANALYZER (ATS Score + Improvements)
    // ---------------------------------------------------------
    public String analyzeResume(String resumeText) {
        // We use 'gemini-flash-latest'
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=" + apiKey;

        // 1. Create the Request
        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> contentPart = new HashMap<>();
        List<Map<String, Object>> parts = new ArrayList<>();
        Map<String, Object> textPart = new HashMap<>();

        // 🧠 PROMPT: Asks for ATS Score + Keywords
        String prompt = "You are an expert ATS (Applicant Tracking System) scanner. Analyze the resume text below.\n" +
                        "1. Give an ATS Score out of 100 based on keyword matching, formatting, and impact.\n" +
                        "2. List 3-4 Missing Keywords that would help this resume.\n" +
                        "3. List 3 specific Improvements.\n" +
                        "Strict Output Format:\n" +
                        "ATS Score: [Number]/100\n" +
                        "## Missing Keywords\n" +
                        "- [Keyword]\n" +
                        "## Improvements\n" +
                        "- [Improvement]\n\n" +
                        "Resume Text:\n" + resumeText;

        textPart.put("text", prompt);
        parts.add(textPart);
        contentPart.put("parts", parts);
        contents.add(contentPart);
        requestBody.put("contents", contents);

        // 2. Send to Google
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            
            // 3. Extract Answer
            Map body = response.getBody();
            List<Map> candidates = (List<Map>) body.get("candidates");
            Map content = (Map) candidates.get(0).get("content");
            List<Map> partsResponse = (List<Map>) content.get("parts");
            String textResponse = (String) partsResponse.get(0).get("text");
            
            return textResponse;

        } catch (Exception e) {
            return "Error talking to AI: " + e.getMessage();
        }
    }

    // ---------------------------------------------------------
    // METHOD 2: MOCK INTERVIEW CHAT (New Feature!)
    // ---------------------------------------------------------
    public String getInterviewResponse(String userMessage) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=" + apiKey;

        // 🧠 PROMPT: Set the persona to "Interviewer"
        String prompt = "You are a professional Technical Interviewer. The candidate says: \"" + userMessage + "\". " +
                        "Reply with a short, constructive response or a follow-up technical question. " +
                        "Do not include markdown formatting like ** or ##. Keep it conversational.";

        // 1. Create Request
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

        // 2. Send to Google
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            
            // 3. Extract Answer
            Map body = response.getBody();
            List<Map> candidates = (List<Map>) body.get("candidates");
            Map content = (Map) candidates.get(0).get("content");
            List<Map> partsResponse = (List<Map>) content.get("parts");
            return (String) partsResponse.get(0).get("text");

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
 // ❓ NEW METHOD: Generate Interview Questions
    public String generateQuestions(String jobRole) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=" + apiKey;

        String prompt = "List 5 likely technical interview questions for a '" + jobRole + "' role. " +
                        "Include 2 basic, 2 intermediate, and 1 advanced question. " +
                        "Output strictly as a numbered list.";

        // (Boilerplate code to talk to Google)
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