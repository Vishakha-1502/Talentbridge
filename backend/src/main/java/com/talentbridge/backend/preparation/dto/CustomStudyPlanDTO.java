package com.talentbridge.backend.preparation.dto;

import java.util.List;
import java.util.Map;

public class CustomStudyPlanDTO {
    private String targetCompany;
    private String targetRole;
    private Integer durationWeeks;
    private String planSummary;
    private List<Map<String, Object>> weeklyPlan;
    private List<String> prioritySkillsToLearn;
    private List<InterviewQuestionItemDTO> criticalInterviewQuestions;
    private List<StudyMaterialItemDTO> recommendedMaterials;
    private List<String> proTips;

    public CustomStudyPlanDTO() {}

    public String getTargetCompany() {
        return targetCompany;
    }

    public void setTargetCompany(String targetCompany) {
        this.targetCompany = targetCompany;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public Integer getDurationWeeks() {
        return durationWeeks;
    }

    public void setDurationWeeks(Integer durationWeeks) {
        this.durationWeeks = durationWeeks;
    }

    public String getPlanSummary() {
        return planSummary;
    }

    public void setPlanSummary(String planSummary) {
        this.planSummary = planSummary;
    }

    public List<Map<String, Object>> getWeeklyPlan() {
        return weeklyPlan;
    }

    public void setWeeklyPlan(List<Map<String, Object>> weeklyPlan) {
        this.weeklyPlan = weeklyPlan;
    }

    public List<String> getPrioritySkillsToLearn() {
        return prioritySkillsToLearn;
    }

    public void setPrioritySkillsToLearn(List<String> prioritySkillsToLearn) {
        this.prioritySkillsToLearn = prioritySkillsToLearn;
    }

    public List<InterviewQuestionItemDTO> getCriticalInterviewQuestions() {
        return criticalInterviewQuestions;
    }

    public void setCriticalInterviewQuestions(List<InterviewQuestionItemDTO> criticalInterviewQuestions) {
        this.criticalInterviewQuestions = criticalInterviewQuestions;
    }

    public List<StudyMaterialItemDTO> getRecommendedMaterials() {
        return recommendedMaterials;
    }

    public void setRecommendedMaterials(List<StudyMaterialItemDTO> recommendedMaterials) {
        this.recommendedMaterials = recommendedMaterials;
    }

    public List<String> getProTips() {
        return proTips;
    }

    public void setProTips(List<String> proTips) {
        this.proTips = proTips;
    }
}
