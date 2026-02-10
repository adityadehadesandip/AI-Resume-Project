package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*") // Allow React to access this
public class ChatController {

    @Autowired
    private AIResumeService aiResumeService;

    @PostMapping
    public Map<String, String> chatWithAI(@RequestBody Map<String, String> payload) {
        // 1. Get the user's message from React
        String userMessage = payload.get("message");
        
        // 2. Send it to the AI Service (Gemini)
        String aiResponse = aiResumeService.getInterviewResponse(userMessage);

        // 3. Send the AI's reply back to React
        Map<String, String> response = new HashMap<>();
        response.put("reply", aiResponse);
        
        return response;
    }
    
 // 🆕 Endpoint for Question Bank
    @PostMapping("/questions")
    public Map<String, String> getQuestions(@RequestBody Map<String, String> payload) {
        String jobRole = payload.get("role");
        String questions = aiResumeService.generateQuestions(jobRole);
        
        Map<String, String> response = new HashMap<>();
        response.put("questions", questions);
        return response;
    }
}