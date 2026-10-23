package com.talentbridge.backend.preparation.dto;

import java.util.List;

public class InterviewQuestionItemDTO {
    private String question;
    private String difficulty; // "Easy", "Medium", "Hard"
    private String topic;
    private String frequency; // "Very High", "High", "Common"
    private String solutionOutline;
    private String practiceUrl;
    private List<String> keyPoints;

    public InterviewQuestionItemDTO() {}

    public InterviewQuestionItemDTO(String question, String difficulty, String topic, String frequency, String solutionOutline, String practiceUrl, List<String> keyPoints) {
        this.question = question;
        this.difficulty = difficulty;
        this.topic = topic;
        this.frequency = frequency;
        this.solutionOutline = solutionOutline;
        this.practiceUrl = practiceUrl;
        this.keyPoints = keyPoints;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getSolutionOutline() {
        return solutionOutline;
    }

    public void setSolutionOutline(String solutionOutline) {
        this.solutionOutline = solutionOutline;
    }

    public String getPracticeUrl() {
        return practiceUrl;
    }

    public void setPracticeUrl(String practiceUrl) {
        this.practiceUrl = practiceUrl;
    }

    public List<String> getKeyPoints() {
        return keyPoints;
    }

    public void setKeyPoints(List<String> keyPoints) {
        this.keyPoints = keyPoints;
    }
}
