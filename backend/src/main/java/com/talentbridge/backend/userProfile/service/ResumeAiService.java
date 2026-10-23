package com.talentbridge.backend.userProfile.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentbridge.backend.userProfile.dto.ParsedResumeDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ResumeAiService {

    @Value("${groq.api.key}")
    private String apiKey;

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL = "llama-3.1-8b-instant";
    private static final int MAX_TOKENS = 1200;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean isApiKeyAvailable() {
        return apiKey != null && !apiKey.trim().isEmpty() 
            && !apiKey.contains("placeholder") 
            && !apiKey.contains("dummy") 
            && !apiKey.equals("gsk_placeholder_key");
    }

    public ParsedResumeDTO parseWithAi(String resumeText) throws Exception {
        if (!isApiKeyAvailable()) {
            throw new IllegalStateException("Groq API key not configured or is a placeholder");
        }

        // Limit resume text size to avoid exceeding token limits
        String truncatedText = resumeText.length() > 6000 ? resumeText.substring(0, 6000) : resumeText;

        String prompt = "You are an expert ATS and resume analyzer. Analyze the following candidate resume text and extract candidate profile details.\n" +
            "Respond ONLY with valid JSON having exactly these keys:\n" +
            "{\n" +
            "  \"firstName\": \"string\",\n" +
            "  \"lastName\": \"string\",\n" +
            "  \"email\": \"string\",\n" +
            "  \"phoneNumber\": \"string\",\n" +
            "  \"headline\": \"string (e.g. Software Engineer | React | Java)\",\n" +
            "  \"currentCompany\": \"string\",\n" +
            "  \"experienceLevel\": \"string (e.g. Fresher, 1-3 years, 3-5 years, Senior)\",\n" +
            "  \"jobTitle\": \"string\",\n" +
            "  \"jobDescription\": \"string (brief career summary or current role details)\",\n" +
            "  \"skills\": [\"skill1\", \"skill2\"],\n" +
            "  \"industry\": \"Information Technology\",\n" +
            "  \"location\": \"string\",\n" +
            "  \"linkedinUrl\": \"string\",\n" +
            "  \"portfolioUrl\": \"string\",\n" +
            "  \"githubUrl\": \"string\"\n" +
            "}\n" +
            "Do NOT include markdown formatting or commentary.\n\n" +
            "Resume Text:\n" + truncatedText;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL);
        requestBody.put("max_tokens", MAX_TOKENS);
        requestBody.put("temperature", 0.1);
        requestBody.put("response_format", Map.of("type", "json_object"));

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", prompt));
        requestBody.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey.trim());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
            GROQ_API_URL,
            HttpMethod.POST,
            request,
            String.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            String content = extractContent(response.getBody());
            ParsedResumeDTO dto = objectMapper.readValue(content, ParsedResumeDTO.class);
            dto.setExtractionMethod("AI (GroqCloud)");
            return dto;
        } else {
            throw new RuntimeException("Non-OK response from GroqCloud: " + response.getStatusCode());
        }
    }

    private String extractContent(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode choices = root.get("choices");
        if (choices != null && choices.isArray() && choices.size() > 0) {
            JsonNode message = choices.get(0).get("message");
            if (message != null && message.has("content")) {
                return message.get("content").asText();
            }
        }
        throw new RuntimeException("Invalid response format from GroqCloud API");
    }
}
