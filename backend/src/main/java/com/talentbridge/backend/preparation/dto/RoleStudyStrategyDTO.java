package com.talentbridge.backend.preparation.dto;

import java.util.List;
import java.util.Map;

public class RoleStudyStrategyDTO {
    private String id;
    private String roleTitle;
    private String category;
    private String icon;
    private String experienceLevel;
    private String overview;
    private List<String> coreCompetencies;
    private List<Map<String, Object>> learningRoadmap;
    private List<String> coreTopicsToMaster;
    private List<RoundInfoDTO> standardInterviewRounds;
    private List<InterviewQuestionItemDTO> topInterviewQuestions;
    private List<Map<String, Object>> portfolioProjects;
    private List<StudyMaterialItemDTO> curatedMaterials;

    public RoleStudyStrategyDTO() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoleTitle() {
        return roleTitle;
    }

    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public List<String> getCoreCompetencies() {
        return coreCompetencies;
    }

    public void setCoreCompetencies(List<String> coreCompetencies) {
        this.coreCompetencies = coreCompetencies;
    }

    public List<Map<String, Object>> getLearningRoadmap() {
        return learningRoadmap;
    }

    public void setLearningRoadmap(List<Map<String, Object>> learningRoadmap) {
        this.learningRoadmap = learningRoadmap;
    }

    public List<String> getCoreTopicsToMaster() {
        return coreTopicsToMaster;
    }

    public void setCoreTopicsToMaster(List<String> coreTopicsToMaster) {
        this.coreTopicsToMaster = coreTopicsToMaster;
    }

    public List<RoundInfoDTO> getStandardInterviewRounds() {
        return standardInterviewRounds;
    }

    public void setStandardInterviewRounds(List<RoundInfoDTO> standardInterviewRounds) {
        this.standardInterviewRounds = standardInterviewRounds;
    }

    public List<InterviewQuestionItemDTO> getTopInterviewQuestions() {
        return topInterviewQuestions;
    }

    public void setTopInterviewQuestions(List<InterviewQuestionItemDTO> topInterviewQuestions) {
        this.topInterviewQuestions = topInterviewQuestions;
    }

    public List<Map<String, Object>> getPortfolioProjects() {
        return portfolioProjects;
    }

    public void setPortfolioProjects(List<Map<String, Object>> portfolioProjects) {
        this.portfolioProjects = portfolioProjects;
    }

    public List<StudyMaterialItemDTO> getCuratedMaterials() {
        return curatedMaterials;
    }

    public void setCuratedMaterials(List<StudyMaterialItemDTO> curatedMaterials) {
        this.curatedMaterials = curatedMaterials;
    }
}
