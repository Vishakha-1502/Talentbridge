package com.talentbridge.backend.jobs.model;

import com.talentbridge.backend.recruiter.model.Recruiter;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(length = 5000)
    private String description;
    private String location;
    private String job_type; 
    private Double salary;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> skills;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    @ManyToOne
    @JoinColumn(name = "recruiter_id", nullable = true)
    private Recruiter recruiter;

    // --- External Multi-Platform Integration Fields ---
    @Column(name = "company_name")
    private String companyName;

    @Column(name = "company_logo")
    private String companyLogo;

    @Column(name = "platform")
    private String platform; // "TALENT_BRIDGE", "LINKEDIN", "NAUKRI", "INDEED", "GLASSDOOR", "ARBEITNOW"

    @Column(name = "apply_url", length = 1500)
    private String applyUrl;

    @Column(name = "is_external")
    private Boolean isExternal = false;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "posted_at")
    private LocalDateTime postedAt;

    @PrePersist
    protected void onCreate() {
        if (created_at == null) {
            created_at = LocalDateTime.now();
        }
        if (updated_at == null) {
            updated_at = LocalDateTime.now();
        }
        if (postedAt == null) {
            postedAt = created_at;
        }
        if (platform == null) {
            platform = (recruiter != null) ? "TALENT_BRIDGE" : "LINKEDIN";
        }
        if (isExternal == null) {
            isExternal = (recruiter == null);
        }
        extractSkills();
    }

    @PreUpdate
    protected void onUpdate() {
        updated_at = LocalDateTime.now();
        extractSkills();
    }

    private void extractSkills() {
        if (description == null || description.isEmpty()) {
            return;
        }

        // Common tech skills to look for
        String[] commonSkills = {
            "Java", "Python", "JavaScript", "React", "Node.js", "NodeJS", "Spring Boot", "Spring",
            "SQL", "MySQL", "PostgreSQL", "MongoDB", "AWS", "Docker", "Kubernetes", "Git",
            "HTML", "CSS", "TypeScript", "Angular", "Vue", "Django", "Flask",
            "C++", "C#", ".NET", "Rest API", "GraphQL", "Redis", "Linux",
            "Machine Learning", "AI", "Data Science", "DevOps", "Agile", "Scrum", "Healthcare",
            "Microservices", "Next.js", "Tailwind"
        };

        if (this.skills == null) {
            this.skills = new ArrayList<>();
        } else if (this.skills.isEmpty()) {
            // only extract if not already provided
        } else {
            return;
        }

        String lowerDesc = description.toLowerCase();

        for (String skill : commonSkills) {
            if (lowerDesc.contains(skill.toLowerCase())) {
                String normalizedSkill = skill.equalsIgnoreCase("NodeJS") ? "Node.js" : skill;
                if (!this.skills.contains(normalizedSkill)) {
                    this.skills.add(normalizedSkill);
                }
            }
        }
    }

    // --- Getters & Setters ---
    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getJob_type() {
        return job_type;
    }

    public void setJob_type(String job_type) {
        this.job_type = job_type;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public Recruiter getRecruiter() {
        return recruiter;
    }

    public void setRecruiter(Recruiter recruiter) {
        this.recruiter = recruiter;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getApplyUrl() {
        return applyUrl;
    }

    public void setApplyUrl(String applyUrl) {
        this.applyUrl = applyUrl;
    }

    public Boolean getIsExternal() {
        return isExternal;
    }

    public void setIsExternal(Boolean isExternal) {
        this.isExternal = isExternal;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public LocalDateTime getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(LocalDateTime postedAt) {
        this.postedAt = postedAt;
    }
}
