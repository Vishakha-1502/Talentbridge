package com.talentbridge.backend.userProfile.service;

import com.talentbridge.backend.userProfile.dto.ParsedResumeDTO;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LocalResumeNlpEngine {

    // Comprehensive catalog of skills to detect
    private static final List<String> SKILL_CATALOG = Arrays.asList(
        // Languages
        "Java", "Python", "JavaScript", "TypeScript", "C++", "C#", "C", "PHP", "Ruby", "Swift",
        "Kotlin", "Go", "Golang", "Rust", "Dart", "SQL", "HTML", "CSS", "Bash", "Shell", "R",
        // Frontend
        "React", "Next.js", "Angular", "Vue", "Vue.js", "Svelte", "Redux", "Tailwind CSS", "Bootstrap",
        "jQuery", "Material UI", "HTML5", "CSS3", "Vite", "Webpack", "Sass", "LESS",
        // Backend & Frameworks
        "Spring Boot", "Spring", "Node.js", "Express", "Express.js", "Django", "Flask", "FastAPI",
        ".NET Core", "ASP.NET", ".NET", "Laravel", "Ruby on Rails", "NestJS", "REST API", "Restful API",
        "GraphQL", "Microservices", "Hibernate", "JPA",
        // Databases
        "MySQL", "PostgreSQL", "MongoDB", "Redis", "Oracle", "SQLite", "Cassandra", "DynamoDB",
        "Firebase", "Elasticsearch", "MariaDB",
        // Cloud & DevOps
        "AWS", "Azure", "GCP", "Google Cloud", "Docker", "Kubernetes", "Jenkins", "CI/CD", "Git",
        "GitHub", "GitLab", "Terraform", "Ansible", "Linux", "Unix", "Nginx", "Apache",
        // AI / ML / Data
        "Machine Learning", "Deep Learning", "Artificial Intelligence", "NLP", "Natural Language Processing",
        "Data Science", "Computer Vision", "Pandas", "NumPy", "Scikit-Learn", "TensorFlow", "PyTorch",
        "Keras", "OpenCV", "Tableau", "Power BI", "Data Analysis",
        // Architecture & Practices
        "Agile", "Scrum", "JIRA", "Unit Testing", "JUnit", "Jest", "Selenium", "Cypress",
        "Kafka", "RabbitMQ", "OAuth", "JWT", "System Design", "OOP", "Object Oriented Programming"
    );

    // Common Job Titles
    private static final List<String> COMMON_TITLES = Arrays.asList(
        "Full Stack Developer", "Full Stack Engineer", "Frontend Developer", "Frontend Engineer",
        "Backend Developer", "Backend Engineer", "Software Engineer", "Software Developer",
        "Senior Software Engineer", "Junior Software Engineer", "DevOps Engineer", "Cloud Engineer",
        "Data Scientist", "Machine Learning Engineer", "AI Engineer", "Data Analyst",
        "Mobile Developer", "Android Developer", "iOS Developer", "QA Engineer",
        "Quality Assurance Engineer", "Web Developer", "System Administrator", "Solutions Architect"
    );

    // Common Locations
    private static final List<String> COMMON_LOCATIONS = Arrays.asList(
        "Mumbai", "Bangalore", "Bengaluru", "Pune", "Delhi", "New Delhi", "Hyderabad",
        "Chennai", "Kolkata", "Noida", "Gurgaon", "Gurugram", "Ahmedabad", "Jaipur",
        "San Francisco", "New York", "Seattle", "Austin", "London", "Toronto", "Berlin",
        "Singapore", "Remote", "India", "USA", "United States", "UK"
    );

    public ParsedResumeDTO parse(String rawText) {
        ParsedResumeDTO dto = new ParsedResumeDTO();
        dto.setExtractionMethod("Local NLP Engine");

        if (rawText == null || rawText.trim().isEmpty()) {
            return dto;
        }

        String normalizedText = rawText.replace("\r\n", "\n").replace("\r", "\n");

        // 1. Email extraction
        String email = extractEmail(normalizedText);
        dto.setEmail(email);

        // 2. Phone extraction
        String phone = extractPhone(normalizedText);
        dto.setPhoneNumber(phone);

        // 3. Name extraction
        String[] name = extractName(normalizedText, email);
        dto.setFirstName(name[0]);
        dto.setLastName(name[1]);

        // 4. Social Links extraction
        dto.setLinkedinUrl(extractLinkedin(normalizedText));
        dto.setGithubUrl(extractGithub(normalizedText));
        dto.setPortfolioUrl(extractPortfolio(normalizedText, dto.getLinkedinUrl(), dto.getGithubUrl()));

        // 5. Skills extraction
        List<String> skills = extractSkills(normalizedText);
        dto.setSkills(skills);

        // 6. Experience level & Title
        String experienceLevel = extractExperienceLevel(normalizedText);
        dto.setExperienceLevel(experienceLevel);

        String jobTitle = extractJobTitle(normalizedText);
        dto.setJobTitle(jobTitle);

        // 7. Headline
        dto.setHeadline(generateHeadline(jobTitle, skills));

        // 8. Location
        dto.setLocation(extractLocation(normalizedText));

        // 9. Current Company & Job Description
        dto.setCurrentCompany(extractCompany(normalizedText));
        dto.setJobDescription(extractSummary(normalizedText));

        dto.setIndustry("Information Technology");

        return dto;
    }

    private String extractEmail(String text) {
        Pattern pattern = Pattern.compile("(?i)\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    private String extractPhone(String text) {
        // Match +91 xxxxxxxxxx, (xxx) xxx-xxxx, xxx-xxx-xxxx, etc.
        Pattern pattern = Pattern.compile("(?:\\+?\\d{1,3}[-.\\s]?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}|(?:\\+?91[-.\\s]?)?[6-9]\\d{9}");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group().trim();
        }
        return "";
    }

    private String[] extractName(String text, String email) {
        String[] lines = text.split("\n");
        Set<String> blacklistedWords = new HashSet<>(Arrays.asList(
            "resume", "curriculum", "vitae", "cv", "profile", "contact", "email",
            "phone", "address", "education", "experience", "skills", "projects", "summary"
        ));

        for (int i = 0; i < Math.min(lines.length, 8); i++) {
            String line = lines[i].trim();
            if (line.isEmpty() || line.length() > 50 || line.contains("@") || line.contains("http")) {
                continue;
            }

            String lower = line.toLowerCase();
            boolean containsBlacklist = false;
            for (String bw : blacklistedWords) {
                if (lower.contains(bw)) {
                    containsBlacklist = true;
                    break;
                }
            }
            if (containsBlacklist) continue;

            // Check if line looks like a person's name (2 to 4 capitalized alphabetic words)
            String[] words = line.split("\\s+");
            if (words.length >= 2 && words.length <= 4) {
                boolean allAlpha = true;
                for (String w : words) {
                    if (!w.matches("^[a-zA-Z\\.'-]+$")) {
                        allAlpha = false;
                        break;
                    }
                }
                if (allAlpha) {
                    String firstName = words[0];
                    StringBuilder lastName = new StringBuilder();
                    for (int j = 1; j < words.length; j++) {
                        if (lastName.length() > 0) lastName.append(" ");
                        lastName.append(words[j]);
                    }
                    return new String[]{firstName, lastName.toString()};
                }
            }
        }

        // Fallback: extract name from email username if available
        if (email != null && email.contains("@")) {
            String prefix = email.substring(0, email.indexOf("@")).replaceAll("[0-9_]+", " ").trim();
            String[] parts = prefix.split("[.\\s]+");
            if (parts.length >= 2) {
                return new String[]{capitalize(parts[0]), capitalize(parts[1])};
            } else if (parts.length == 1 && !parts[0].isEmpty()) {
                return new String[]{capitalize(parts[0]), ""};
            }
        }

        return new String[]{"", ""};
    }

    private String extractLinkedin(String text) {
        Pattern pattern = Pattern.compile("(?i)(https?:\\/\\/)?(www\\.)?linkedin\\.com\\/(?:in|pub)\\/[a-zA-Z0-9_\\-\\.]+");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String match = matcher.group();
            return match.startsWith("http") ? match : "https://" + match;
        }
        return "";
    }

    private String extractGithub(String text) {
        Pattern pattern = Pattern.compile("(?i)(https?:\\/\\/)?(www\\.)?github\\.com\\/[a-zA-Z0-9_\\-\\.]+");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String match = matcher.group();
            return match.startsWith("http") ? match : "https://" + match;
        }
        return "";
    }

    private String extractPortfolio(String text, String linkedin, String github) {
        Pattern pattern = Pattern.compile("(?i)https?:\\/\\/[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,}(?:\\/[^\\s]*)?");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String url = matcher.group();
            String lower = url.toLowerCase();
            if (!lower.contains("linkedin") && !lower.contains("github") && !lower.contains("google") && !lower.contains("w3schools") && !lower.contains("medium")) {
                return url;
            }
        }
        return "";
    }

    private List<String> extractSkills(String text) {
        Set<String> matchedSkills = new LinkedHashSet<>();
        String lowerText = text.toLowerCase();

        for (String skill : SKILL_CATALOG) {
            String patternStr;
            if (skill.equals("C++")) {
                patternStr = "(?i)(?:\\b|[^a-zA-Z0-9])c\\+\\+(?:\\b|[^a-zA-Z0-9])";
            } else if (skill.equals("C#")) {
                patternStr = "(?i)(?:\\b|[^a-zA-Z0-9])c#(?:\\b|[^a-zA-Z0-9])";
            } else if (skill.equals(".NET") || skill.equals(".NET Core")) {
                patternStr = "(?i)\\.net(?:\\s+core)?";
            } else if (skill.equalsIgnoreCase("C")) {
                patternStr = "(?i)(?:\\b|\\s)c(?:\\b|,|\\s|\\/)";
            } else if (skill.equalsIgnoreCase("R")) {
                patternStr = "(?i)(?:\\b|\\s)r(?:\\s+programming|\\s+language|\\b|,|\\/)";
            } else {
                patternStr = "(?i)\\b" + Pattern.quote(skill.toLowerCase()) + "\\b";
            }

            Pattern pattern = Pattern.compile(patternStr);
            if (pattern.matcher(lowerText).find()) {
                // Normalize names
                if (skill.equalsIgnoreCase("Golang")) matchedSkills.add("Go");
                else if (skill.equalsIgnoreCase("Vue.js")) matchedSkills.add("Vue");
                else if (skill.equalsIgnoreCase("Express.js")) matchedSkills.add("Express");
                else if (skill.equalsIgnoreCase("Restful API")) matchedSkills.add("REST API");
                else matchedSkills.add(skill);
            }
        }
        return new ArrayList<>(matchedSkills);
    }

    private String extractExperienceLevel(String text) {
        String lower = text.toLowerCase();
        Pattern yearPattern = Pattern.compile("(\\d{1,2})\\+?\\s*(?:years?|yrs?)(?:\\s+of)?\\s+experience");
        Matcher matcher = yearPattern.matcher(lower);
        if (matcher.find()) {
            try {
                int years = Integer.parseInt(matcher.group(1));
                if (years < 1) return "Fresher";
                if (years <= 3) return "1-3 years";
                if (years <= 5) return "3-5 years";
                return "Senior (5+ years)";
            } catch (Exception ignored) {}
        }

        if (lower.contains("fresher") || lower.contains("entry level") || lower.contains("recent graduate")) {
            return "Fresher";
        }
        if (lower.contains("senior") || lower.contains("lead engineer") || lower.contains("tech lead")) {
            return "Senior (5+ years)";
        }
        if (lower.contains("intern") || lower.contains("internship")) {
            return "Fresher";
        }
        return "1-3 years";
    }

    private String extractJobTitle(String text) {
        String lower = text.toLowerCase();
        for (String title : COMMON_TITLES) {
            if (lower.contains(title.toLowerCase())) {
                return title;
            }
        }
        // Fallback default
        return "Software Engineer";
    }

    private String generateHeadline(String title, List<String> skills) {
        if (skills.isEmpty()) {
            return title;
        }
        List<String> topSkills = skills.subList(0, Math.min(skills.size(), 3));
        return title + " | " + String.join(" | ", topSkills);
    }

    private String extractLocation(String text) {
        String lower = text.toLowerCase();
        for (String loc : COMMON_LOCATIONS) {
            Pattern p = Pattern.compile("(?i)\\b" + Pattern.quote(loc) + "\\b");
            if (p.matcher(lower).find()) {
                if (loc.equalsIgnoreCase("bengaluru")) return "Bangalore, India";
                if (loc.equalsIgnoreCase("mumbai") || loc.equalsIgnoreCase("pune") || loc.equalsIgnoreCase("delhi") || loc.equalsIgnoreCase("hyderabad") || loc.equalsIgnoreCase("chennai")) {
                    return loc + ", India";
                }
                return loc;
            }
        }
        return "Remote";
    }

    private String extractCompany(String text) {
        // Pattern 1: Title at Company (e.g. "Full Stack Developer at Tech Innovations Pvt Ltd")
        Pattern pattern1 = Pattern.compile("(?i)(?:engineer|developer|lead|analyst|manager|specialist|consultant)\\s+(?:at|@)\\s+([A-Za-z0-9\\s&.,'-]+?)(?:\\n|\\r|,|\\||\\-|$)");
        Matcher matcher1 = pattern1.matcher(text);
        if (matcher1.find()) {
            String company = matcher1.group(1).trim();
            if (company.length() >= 2 && company.length() < 50) return company;
        }

        // Pattern 2: Explicit prefix (worked at / currently at / company:)
        Pattern pattern2 = Pattern.compile("(?i)(?:worked at|currently at|company|employer):?\\s*([A-Za-z0-9\\s&.,'-]+?)(?:\\n|\\r|,|$)");
        Matcher matcher2 = pattern2.matcher(text);
        if (matcher2.find()) {
            String company = matcher2.group(1).trim();
            if (company.length() >= 2 && company.length() < 50) return company;
        }
        return "";
    }

    private String extractSummary(String text) {
        Pattern pattern = Pattern.compile("(?i)(?:summary|professional summary|about me|profile summary|career objective)[\\s\\n:]+([\\s\\S]+?)(?=\\n\\s*(?:technical skills|skills|experience|work experience|education|projects|certifications)|$)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String summary = matcher.group(1).trim().replaceAll("\\s+", " ");
            if (summary.length() > 20) {
                return summary.length() > 500 ? summary.substring(0, 500) + "..." : summary;
            }
        }
        return "";
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return "";
        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
    }
}
