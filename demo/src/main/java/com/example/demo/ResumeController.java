package com.example.demo;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Resume;
import com.example.demo.repository.ResumeRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = "*")
public class ResumeController {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private AIResumeService aiResumeService;

    // 1. UPLOAD & ANALYZE
    @PostMapping("/upload")
    public Map<String, String> uploadResume(@RequestParam("file") MultipartFile file) throws IOException {
        // A. Extract Text
        String content = "";
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            content = pdfStripper.getText(document);
        }

        // B. Ask AI
        String analysis = aiResumeService.analyzeResume(content);

        // C. Extract Score using Regex (Finds "ATS Score: 85")
        int score = 0;
        Pattern pattern = Pattern.compile("ATS Score:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(analysis);
        if (matcher.find()) {
            score = Integer.parseInt(matcher.group(1));
        }

        // D. Save to Database
        Resume resume = new Resume();
        resume.setContent(content);
        resume.setAnalysis(analysis); // Saving the AI feedback
        resume.setAtsScore(score);    // Saving the Score
        resumeRepository.save(resume);

        // E. Respond
        Map<String, String> response = new HashMap<>();
        response.put("message", "Analyzed & Saved!");
        response.put("analysis", analysis);
        return response;
    }

    // 2. GET HISTORY (For Dashboard)
    @GetMapping("/history")
    public List<Resume> getResumeHistory() {
        return resumeRepository.findAll();
    }
}