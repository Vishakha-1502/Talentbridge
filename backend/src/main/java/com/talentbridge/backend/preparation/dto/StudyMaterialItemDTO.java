package com.talentbridge.backend.preparation.dto;

public class StudyMaterialItemDTO {
    private String title;
    private String type; // e.g. "Cheatsheet", "Documentation", "Video Course", "Practice Problems", "GitHub Repo", "Book"
    private String url;
    private String description;
    private String tag; // e.g. "Free", "Essential", "Recommended", "Official"

    public StudyMaterialItemDTO() {}

    public StudyMaterialItemDTO(String title, String type, String url, String description, String tag) {
        this.title = title;
        this.type = type;
        this.url = url;
        this.description = description;
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
