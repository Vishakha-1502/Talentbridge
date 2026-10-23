package com.talentbridge.backend.userProfile.service;

import com.talentbridge.backend.userProfile.dto.ParsedResumeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ResumeParsingService {

    @Autowired
    private ResumeTextExtractorService textExtractorService;

    @Autowired
    private ResumeAiService resumeAiService;

    @Autowired
    private LocalResumeNlpEngine localResumeNlpEngine;

    public ParsedResumeDTO parseResume(MultipartFile file) {
        String rawText;
        try {
            rawText = textExtractorService.extractText(file);
        } catch (Exception e) {
            System.err.println("ResumeParsingService: Failed to extract text from file: " + e.getMessage());
            throw new RuntimeException("Could not read uploaded file: " + e.getMessage(), e);
        }

        if (rawText == null || rawText.trim().isEmpty()) {
            throw new RuntimeException("Uploaded file contains no readable text");
        }

        // Try AI if API key is present
        if (resumeAiService.isApiKeyAvailable()) {
            try {
                System.out.println("ResumeParsingService: Attempting AI parsing via GroqCloud...");
                ParsedResumeDTO aiResult = resumeAiService.parseWithAi(rawText);
                if (aiResult != null && (aiResult.getEmail() != null || !aiResult.getSkills().isEmpty())) {
                    System.out.println("ResumeParsingService: Successfully parsed via AI engine.");
                    return aiResult;
                }
            } catch (Exception e) {
                System.err.println("ResumeParsingService: AI parsing failed or timed out: " + e.getMessage() + ". Falling back to Local NLP Engine.");
            }
        } else {
            System.out.println("ResumeParsingService: No external AI key configured. Using Local NLP Engine directly.");
        }

        // Fallback or Primary: Local NLP Engine
        System.out.println("ResumeParsingService: Parsing resume with Local NLP Engine...");
        ParsedResumeDTO localResult = localResumeNlpEngine.parse(rawText);
        System.out.println("ResumeParsingService: Local NLP Engine finished. Extracted " + localResult.getSkills().size() + " skills.");
        return localResult;
    }
}
