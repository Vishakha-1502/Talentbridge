package com.talentbridge.backend.preparation.dto;

public class CustomPlanRequestDTO {
    private String company;
    private String role;
    private Integer durationWeeks;
    private String experienceLevel;

    public CustomPlanRequestDTO() {}

    public CustomPlanRequestDTO(String company, String role, Integer durationWeeks, String experienceLevel) {
        this.company = company;
        this.role = role;
        this.durationWeeks = durationWeeks;
        this.experienceLevel = experienceLevel;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getDurationWeeks() {
        return durationWeeks;
    }

    public void setDurationWeeks(Integer durationWeeks) {
        this.durationWeeks = durationWeeks;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }
}
