package com.talentbridge.backend.preparation.dto;

import java.util.List;

public class CompanyStudyStrategyDTO {
    private String id;
    private String companyName;
    private String category; // "Tier-1 Product / FAANG", "IT Services & Consulting", "FinTech & Enterprise", "Fast-Growing Tech"
    private String logoUrl;
    private String brandColor;
    private String packageCTC;
    private String location;
    private String difficultyLevel; // "High", "Medium-High", "Competitive"
    private String overview;
    private String hiringPhilosophy;
    private List<String> keyFocusAreas;
    private List<RoundInfoDTO> rounds;
    private List<InterviewQuestionItemDTO> frequentQuestions;
    private List<String> studyTimeline; // Week-by-week strategy
    private List<StudyMaterialItemDTO> curatedMaterials;

    public CompanyStudyStrategyDTO() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getBrandColor() {
        return brandColor;
    }

    public void setBrandColor(String brandColor) {
        this.brandColor = brandColor;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getHiringPhilosophy() {
        return hiringPhilosophy;
    }

    public void setHiringPhilosophy(String hiringPhilosophy) {
        this.hiringPhilosophy = hiringPhilosophy;
    }

    public List<String> getKeyFocusAreas() {
        return keyFocusAreas;
    }

    public void setKeyFocusAreas(List<String> keyFocusAreas) {
        this.keyFocusAreas = keyFocusAreas;
    }

    public List<RoundInfoDTO> getRounds() {
        return rounds;
    }

    public void setRounds(List<RoundInfoDTO> rounds) {
        this.rounds = rounds;
    }

    public List<InterviewQuestionItemDTO> getFrequentQuestions() {
        return frequentQuestions;
    }

    public void setFrequentQuestions(List<InterviewQuestionItemDTO> frequentQuestions) {
        this.frequentQuestions = frequentQuestions;
    }

    public List<String> getStudyTimeline() {
        return studyTimeline;
    }

    public void setStudyTimeline(List<String> studyTimeline) {
        this.studyTimeline = studyTimeline;
    }

    public List<StudyMaterialItemDTO> getCuratedMaterials() {
        return curatedMaterials;
    }

    public void setCuratedMaterials(List<StudyMaterialItemDTO> curatedMaterials) {
        this.curatedMaterials = curatedMaterials;
    }

    public String getPackageCTC() {
        return packageCTC;
    }

    public void setPackageCTC(String packageCTC) {
        this.packageCTC = packageCTC;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
