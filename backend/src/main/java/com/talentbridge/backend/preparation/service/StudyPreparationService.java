package com.talentbridge.backend.preparation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentbridge.backend.ai.service.GroqCloudService;
import com.talentbridge.backend.preparation.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudyPreparationService {

    private static final Logger logger = LoggerFactory.getLogger(StudyPreparationService.class);

    @Autowired(required = false)
    private GroqCloudService groqCloudService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, CompanyStudyStrategyDTO> companyStrategies = new LinkedHashMap<>();
    private final Map<String, RoleStudyStrategyDTO> roleStrategies = new LinkedHashMap<>();

    public StudyPreparationService() {
        initCompanyData();
        initRoleData();
    }

    public List<CompanyStudyStrategyDTO> getAllCompanies() {
        return new ArrayList<>(companyStrategies.values());
    }

    public CompanyStudyStrategyDTO getCompanyStrategy(String companyName) {
        if (companyName == null || companyName.isBlank()) {
            return companyStrategies.get("google");
        }
        String key = normalizeKey(companyName);
        for (Map.Entry<String, CompanyStudyStrategyDTO> entry : companyStrategies.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(key) || 
                entry.getValue().getCompanyName().equalsIgnoreCase(companyName) ||
                companyName.toLowerCase().contains(entry.getKey()) ||
                entry.getKey().contains(companyName.toLowerCase())) {
                return entry.getValue();
            }
        }
        // If not found in pre-curated catalog, generate dynamic tailored strategy
        return generateDynamicCompanyStrategy(companyName);
    }

    public List<RoleStudyStrategyDTO> getAllRoles() {
        return new ArrayList<>(roleStrategies.values());
    }

    public RoleStudyStrategyDTO getRoleStrategy(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            return roleStrategies.get("software-engineer");
        }
        String key = normalizeKey(roleName);
        for (Map.Entry<String, RoleStudyStrategyDTO> entry : roleStrategies.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(key) || 
                entry.getValue().getRoleTitle().equalsIgnoreCase(roleName) ||
                roleName.toLowerCase().contains(entry.getKey()) ||
                entry.getKey().contains(roleName.toLowerCase())) {
                return entry.getValue();
            }
        }
        return generateDynamicRoleStrategy(roleName);
    }

    public CustomStudyPlanDTO generateCustomPlan(CustomPlanRequestDTO request) {
        String company = request.getCompany() != null && !request.getCompany().isBlank() ? request.getCompany() : "Target Company";
        String role = request.getRole() != null && !request.getRole().isBlank() ? request.getRole() : "Software Engineer";
        int weeks = request.getDurationWeeks() != null && request.getDurationWeeks() > 0 ? request.getDurationWeeks() : 4;
        String level = request.getExperienceLevel() != null && !request.getExperienceLevel().isBlank() ? request.getExperienceLevel() : "Intermediate";

        return createStructuredCustomPlan(company, role, weeks, level);
    }

    private CustomStudyPlanDTO createStructuredCustomPlan(String company, String role, int weeks, String level) {
        CustomStudyPlanDTO plan = new CustomStudyPlanDTO();
        plan.setTargetCompany(company);
        plan.setTargetRole(role);
        plan.setDurationWeeks(weeks);
        plan.setPlanSummary(String.format("Tailored %d-week intensive preparation strategy for %s targeting %s at %s. Designed for %s level candidates.",
                weeks, role, company, level, level));

        List<String> prioritySkills = new ArrayList<>();
        if (role.toLowerCase().contains("front")) {
            prioritySkills.addAll(List.of("JavaScript/TypeScript (ES6+ Deep Dive)", "React Core & Custom Hooks", "State Management & Performance Optimization", "CSS Architecture & Web Vitals", "Frontend System Design"));
        } else if (role.toLowerCase().contains("back")) {
            prioritySkills.addAll(List.of("Java / Spring Boot or Node.js Core", "Relational Database Indexing & Query Tuning", "RESTful & gRPC API Architecture", "Redis Caching & Message Queues", "Scalable System Design & Microservices"));
        } else if (role.toLowerCase().contains("data") || role.toLowerCase().contains("ml") || role.toLowerCase().contains("ai")) {
            prioritySkills.addAll(List.of("Python, NumPy & Pandas Data Manipulation", "Statistical Foundations & Feature Engineering", "Supervised/Unsupervised ML Algorithms", "Deep Learning & Transformer Architectures", "Model Deployment & Vector Databases"));
        } else {
            prioritySkills.addAll(List.of("Data Structures & Algorithms (Trees, Graphs, DP)", "Object-Oriented Design & Clean Architecture", "System Design & Low-Level Design (LLD)", "Database Schema Design & Query Optimization", "Behavioral & STAR Scenario Responses"));
        }
        plan.setPrioritySkillsToLearn(prioritySkills);

        List<Map<String, Object>> weeklyPlan = new ArrayList<>();
        for (int i = 1; i <= weeks; i++) {
            Map<String, Object> week = new LinkedHashMap<>();
            week.put("weekNumber", i);
            if (i == 1) {
                week.put("theme", "Foundation & Diagnostic Assessment");
                week.put("focusTopics", List.of("Core CS Fundamentals", "Basic Data Structures (Arrays, Strings, HashMaps)", company + " Hiring Format Overview"));
                week.put("dailyTasks", List.of(
                    "Day 1-2: Review core language syntax, Big-O analysis, and solve 5 easy warmups",
                    "Day 3-4: Implement Two-Pointers, Sliding Window, and Prefix Sum techniques",
                    "Day 5-6: Deep dive into company interview patterns and past candidate experiences",
                    "Day 7: Weekly timed diagnostic test (2 coding questions in 60 minutes)"
                ));
                week.put("mockTestGoal", "Solve 2 medium problems without IDE autocompletion in under 60 minutes");
            } else if (i == weeks) {
                week.put("theme", "Full Mocks, System Design & Behavioral Polish");
                week.put("focusTopics", List.of("Full-Length Mock Interviews", "System Design / Architecture Presentation", "STAR Method Behavioral Stories for " + company));
                week.put("dailyTasks", List.of(
                    "Day 1-2: 2 Full-length live coding mocks with a peer or timer",
                    "Day 3-4: Refine 5 core behavioral stories tailored to " + company + "'s culture and values",
                    "Day 5: Revise cheatsheets, complex edge cases, and high-frequency company problems",
                    "Day 6-7: Rest, mental recharge, and final resume walkthrough"
                ));
                week.put("mockTestGoal", "Pass 2 mock interview rounds with clear communication and time-management");
            } else if (i == 2) {
                week.put("theme", "Non-Linear Structures & Algorithmic Patterns");
                week.put("focusTopics", List.of("Binary Trees, BST & Tree Traversals", "Heaps / Priority Queues", "Linked Lists & Fast/Slow Pointers"));
                week.put("dailyTasks", List.of(
                    "Day 1-2: BFS and DFS tree traversals (Level order, Zigzag, Path Sum)",
                    "Day 3-4: Top K Elements with Min/Max Heap and K-Way Merge",
                    "Day 5-6: Fast & Slow Pointer patterns for Linked List cycle and midpoint",
                    "Day 7: Timed assessment on Tree and Heap problems"
                ));
                week.put("mockTestGoal", "Complete 4 Tree/Heap medium problems within 90 minutes");
            } else {
                week.put("theme", "Advanced Graphs, Dynamic Programming & System Design");
                week.put("focusTopics", List.of("Graph BFS/DFS & Topological Sort", "Dynamic Programming (Knapsack & Subsequences)", "Low-Level / High-Level System Design"));
                week.put("dailyTasks", List.of(
                    "Day 1-2: Graph algorithms (Cycle detection, Connected Components, Dijkstra)",
                    "Day 3-4: 1D and 2D DP memoization vs tabulation",
                    "Day 5-6: Architecting scalable systems (Load Balancers, Sharding, Caching)",
                    "Day 7: 90-minute timed OA mock covering DP and Graphs"
                ));
                week.put("mockTestGoal", "Solve 1 Medium + 1 Hard problem within 75 minutes");
            }
            weeklyPlan.add(week);
        }
        plan.setWeeklyPlan(weeklyPlan);

        List<InterviewQuestionItemDTO> questions = new ArrayList<>();
        questions.add(new InterviewQuestionItemDTO(
            "LRU Cache Implementation", "Medium", "Data Structures", "Very High",
            "Use a combination of HashMap and Doubly Linked List for O(1) get and put operations.",
            "https://leetcode.com/problems/lru-cache/",
            List.of("O(1) time complexity requirement", "Update node position to head upon access", "Evict tail node upon reaching capacity")
        ));
        questions.add(new InterviewQuestionItemDTO(
            "Design a Rate Limiter", "Medium", "System Design", "High",
            "Evaluate Token Bucket, Leaky Bucket, and Sliding Window Counter algorithms stored in Redis.",
            "https://github.com/donnemartin/system-design-primer",
            List.of("Discuss distributed concurrency", "Redis Lua scripts for atomicity", "Handling 429 Too Many Requests response")
        ));
        questions.add(new InterviewQuestionItemDTO(
            "Tell me about a time you resolved a major production incident", "Behavioral", "Culture & Leadership", "Very High",
            "Structure using STAR (Situation, Task, Action, Result). Highlight root cause analysis and future prevention.",
            null,
            List.of("State the business impact succinctly", "Describe technical debugging steps taken", "Detail post-mortem and preventive automation")
        ));
        plan.setCriticalInterviewQuestions(questions);

        List<StudyMaterialItemDTO> materials = new ArrayList<>();
        materials.add(new StudyMaterialItemDTO("NeetCode 150 Core Practice Roadmap", "Practice Platform", "https://neetcode.io/practice", "Structured coding curriculum covering all key interview patterns", "Free"));
        materials.add(new StudyMaterialItemDTO("System Design Primer by Donne Martin", "GitHub Repo", "https://github.com/donnemartin/system-design-primer", "The most comprehensive open-source system design guide", "Essential"));
        materials.add(new StudyMaterialItemDTO("Tech Interview Handbook", "Cheatsheet", "https://techinterviewhandbook.org/", "Curated algorithm cheatsheets, resume templates, and behavioral guides", "Free"));
        plan.setRecommendedMaterials(materials);

        plan.setProTips(List.of(
            "Always think out loud: Interviewers at " + company + " evaluate your thought process as much as working code.",
            "Test your solution with edge cases before saying you are done (e.g. empty inputs, duplicates, boundaries).",
            "Prepare 3 concrete STAR stories showcasing leadership, dealing with ambiguity, and technical ownership.",
            "Ask clarifying questions about scale, inputs, and constraints in the first 3 minutes."
        ));

        return plan;
    }

    private CompanyStudyStrategyDTO generateDynamicCompanyStrategy(String companyName) {
        CompanyStudyStrategyDTO dto = new CompanyStudyStrategyDTO();
        String id = normalizeKey(companyName);
        dto.setId(id);
        dto.setCompanyName(companyName);
        dto.setCategory("Local & Campus Recruiter");
        dto.setPackageCTC("Market Standard");
        dto.setLocation("Pan India / Remote");
        dto.setDifficultyLevel("Medium-High");
        dto.setBrandColor("from-blue-600 to-indigo-600");
        dto.setOverview(companyName + " hires through a structured technical evaluation process focusing on problem-solving, architectural design, and cultural alignment.");
        dto.setHiringPhilosophy("Values engineers who write clean, maintainable code, communicate their thought process proactively, and demonstrate ownership.");
        dto.setKeyFocusAreas(List.of("Data Structures & Algorithms", "Clean Modular Code", "System Architecture", "Role-Specific Technologies", "Behavioral Alignment"));

        List<RoundInfoDTO> rounds = new ArrayList<>();
        rounds.add(new RoundInfoDTO(1, "Online Assessment (OA)", "60-90 mins on HackerRank/CodeSignal", "Algorithmic problem-solving and basic core CS concepts", List.of("Focus on passing all hidden edge cases", "Maintain clean time and space complexity")));
        rounds.add(new RoundInfoDTO(2, "Technical Problem Solving (1:1)", "45-60 mins live coding", "Data structures, problem decomposition, and coding speed", List.of("Explain approach before writing code", "Write clean, idiomatic variable and function names")));
        rounds.add(new RoundInfoDTO(3, "System Design / Practical Architecture", "45-60 mins architectural whiteboard", "Scalability, API design, database schemas, and tradeoffs", List.of("Clarify scale and functional requirements first", "Discuss bottlenecks and caching layers")));
        rounds.add(new RoundInfoDTO(4, "Managerial & Culture Fit", "45 mins behavioral interview", "Collaboration, handling conflict, career aspirations, and ownership", List.of("Use the STAR framework for all answers", "Research " + companyName + "'s mission and recent products")));
        dto.setRounds(rounds);

        List<InterviewQuestionItemDTO> questions = new ArrayList<>();
        questions.add(new InterviewQuestionItemDTO("Two Sum & Subarray Sum Equals K", "Easy-Medium", "Arrays & HashMaps", "Very High", "Use a HashMap to store complements or running prefix sums in O(N) time.", "https://leetcode.com/problems/subarray-sum-equals-k/", List.of("HashMap lookup in O(1)", "Single pass traversal")));
        questions.add(new InterviewQuestionItemDTO("Binary Tree Level Order Traversal", "Medium", "Trees & BFS", "High", "Use a Queue to traverse level by level, recording node counts at each tier.", "https://leetcode.com/problems/binary-tree-level-order-traversal/", List.of("Queue FIFO order", "Level boundary tracking")));
        questions.add(new InterviewQuestionItemDTO("Design a URL Shortener (TinyURL)", "Medium", "System Design", "High", "Base62 encoding, unique ID generation with Snowflake, distributed caching with Redis.", "https://github.com/donnemartin/system-design-primer", List.of("Read-heavy architecture", "Cache eviction with LRU")));
        dto.setFrequentQuestions(questions);

        dto.setStudyTimeline(List.of(
            "Week 1: Core DSA refresh (Arrays, Strings, HashMaps, Two-Pointers)",
            "Week 2: Non-linear structures (Trees, Graphs, BFS/DFS, Heaps)",
            "Week 3: High-frequency " + companyName + " tagged questions & System Design fundamentals",
            "Week 4: Timed mock interviews & STAR behavioral response preparation"
        ));

        List<StudyMaterialItemDTO> materials = new ArrayList<>();
        materials.add(new StudyMaterialItemDTO("NeetCode 150 Curated Roadmap", "Practice Platform", "https://neetcode.io/practice", "Structured coding curriculum with video explanations", "Free"));
        materials.add(new StudyMaterialItemDTO("System Design Primer", "GitHub Repo", "https://github.com/donnemartin/system-design-primer", "In-depth guide to scaling web applications and distributed systems", "Essential"));
        materials.add(new StudyMaterialItemDTO(companyName + " Interview Experience Archives", "Documentation", "https://www.geeksforgeeks.org/", "Real candidate interview experiences and questions", "Free"));
        dto.setCuratedMaterials(materials);

        return dto;
    }

    private RoleStudyStrategyDTO generateDynamicRoleStrategy(String roleName) {
        RoleStudyStrategyDTO dto = new RoleStudyStrategyDTO();
        String id = normalizeKey(roleName);
        dto.setId(id);
        dto.setRoleTitle(roleName);
        dto.setCategory("Engineering");
        dto.setIcon("Briefcase");
        dto.setExperienceLevel("Fresher to Experienced");
        dto.setOverview("Comprehensive study strategy, core competency roadmap, and curated material for " + roleName + " roles.");
        dto.setCoreCompetencies(List.of("Core Programming Language", "Data Structures & Algorithms", "System Architecture & API Design", "Testing & Debugging", "Version Control & CI/CD"));
        dto.setCoreTopicsToMaster(List.of("Core Syntax & Concurrency", "Database Management & Indexing", "Clean Code & Design Patterns", "REST/GraphQL Communication", "Cloud & Deployment Basics"));

        List<Map<String, Object>> roadmap = new ArrayList<>();
        roadmap.add(Map.of("stage", "Phase 1", "title", "Core Foundations", "description", "Master the primary programming language and core computer science fundamentals.", "topics", List.of("Language Syntax", "Data Structures", "Time Complexity")));
        roadmap.add(Map.of("stage", "Phase 2", "title", "Frameworks & Architecture", "description", "Build production-grade applications using modern frameworks and best practices.", "topics", List.of("Framework Mastery", "API Design", "Database Modeling")));
        roadmap.add(Map.of("stage", "Phase 3", "title", "Performance & Scalability", "description", "Optimize code execution, handle concurrency, and architect resilient systems.", "topics", List.of("Caching", "Profiling", "Scalable Architecture")));
        roadmap.add(Map.of("stage", "Phase 4", "title", "Interview Readiness", "description", "Timed mock interviews, portfolio project walkthroughs, and behavioral alignment.", "topics", List.of("System Design", "Live Coding", "STAR Stories")));
        dto.setLearningRoadmap(roadmap);

        List<RoundInfoDTO> rounds = new ArrayList<>();
        rounds.add(new RoundInfoDTO(1, "Technical Screening", "45-60 min Live Coding / Quiz", "Evaluation of core language knowledge and algorithm fundamentals", List.of("Practice explaining code out loud", "Keep solutions modular and well-commented")));
        rounds.add(new RoundInfoDTO(2, "In-Depth System / Domain Design", "60 min Technical Deep Dive", "Architecture, database selection, failure recovery, and trade-offs", List.of("Ask clarifying questions first", "State assumptions explicitly")));
        dto.setStandardInterviewRounds(rounds);

        List<InterviewQuestionItemDTO> questions = new ArrayList<>();
        questions.add(new InterviewQuestionItemDTO("Explain the execution lifecycle of a request in your primary framework", "Medium", "Architecture", "High", "Trace the lifecycle from client request, routing, middleware, controllers, service layer to database.", null, List.of("Middleware pipeline", "Dependency injection", "Error handling")));
        questions.add(new InterviewQuestionItemDTO("How do you handle database connection pooling and query bottlenecks?", "Medium", "Databases", "High", "Explain connection pool sizing (e.g. HikariCP), query execution plans, and composite indexes.", null, List.of("Index scans vs table scans", "Connection leak prevention")));
        dto.setTopInterviewQuestions(questions);

        List<StudyMaterialItemDTO> materials = new ArrayList<>();
        materials.add(new StudyMaterialItemDTO("Roadmap.sh Developer Guides", "Documentation", "https://roadmap.sh/", "Step-by-step interactive roadmaps for developers", "Free"));
        materials.add(new StudyMaterialItemDTO("NeetCode Practice", "Practice Platform", "https://neetcode.io/", "Coding interview preparation roadmap and video solutions", "Free"));
        dto.setCuratedMaterials(materials);

        return dto;
    }

    private void initCompanyData() {
        initTier1Companies();
        initITServiceCompanies();
        initLocalCampusCompanies();
    }

    private void initTier1Companies() {
        // 1. Google
        CompanyStudyStrategyDTO google = new CompanyStudyStrategyDTO();
        google.setId("google");
        google.setCompanyName("Google");
        google.setCategory("Tier-1 Product / FAANG");
        google.setPackageCTC("₹25.0 - 55.0 LPA");
        google.setLocation("Bangalore / Hyderabad / Pune");
        google.setBrandColor("from-red-500 via-amber-500 to-blue-500");
        google.setDifficultyLevel("High (Bar Raiser)");
        google.setOverview("Google evaluates candidates through rigorous algorithmic problem solving, clean code execution, large-scale system design, and cultural alignment (Googleyness).");
        google.setHiringPhilosophy("Values strong computer science fundamentals, creative problem formulation, writing bug-free modular code under time constraints, and navigating ambiguity.");
        google.setKeyFocusAreas(List.of("Graph Traversal & Dijkstra", "Dynamic Programming & Memoization", "Binary Trees & Trie Structures", "Distributed System Design", "Googleyness & Leadership"));

        List<RoundInfoDTO> gRounds = new ArrayList<>();
        gRounds.add(new RoundInfoDTO(1, "Online Assessment (OA) / Recruiter Screen", "90 mins (2 questions)", "Assesses fundamental algorithmic fluency on Google's internal platform.", List.of("Edge case coverage is critical", "Aim for optimal O(N) or O(N log N) solutions")));
        gRounds.add(new RoundInfoDTO(2, "Technical Phone Screen", "45 mins live coding on Google Docs", "Write real compilable code without syntax highlighting or autocompletion.", List.of("Speak your thought process continuously", "Write clean variable names and helper functions")));
        gRounds.add(new RoundInfoDTO(3, "Onsite Round 1 & 2: Data Structures & Algorithms", "45 mins each", "In-depth graph, tree, and dynamic programming challenges with follow-up constraints.", List.of("Proactively suggest test cases", "Analyze both time and space complexity with precision")));
        gRounds.add(new RoundInfoDTO(4, "Onsite Round 3: Large-Scale System Design", "45 mins", "Architecting planetary-scale systems (e.g. YouTube, Google Drive, Search Indexer).", List.of("Start with functional vs non-functional requirements", "Address data partitioning, consensus, and fault tolerance")));
        gRounds.add(new RoundInfoDTO(5, "Googleyness & Behavioral", "45 mins with Senior Engineering Manager", "Intellectual humility, doing the right thing for the user, collaboration, and handling ambiguity.", List.of("Never speak negatively about past teams", "Emphasize collaborative problem resolution using the STAR method")));
        google.setRounds(gRounds);

        List<InterviewQuestionItemDTO> gQuestions = new ArrayList<>();
        gQuestions.add(new InterviewQuestionItemDTO("Word Ladder II (Shortest Transformation Sequences)", "Hard", "Graphs & BFS", "Very High", "Use BFS to find shortest path lengths and backtrack via DFS to reconstruct all paths.", "https://leetcode.com/problems/word-ladder-ii/", List.of("Bi-directional BFS optimization", "Graph adjacency map")));
        gQuestions.add(new InterviewQuestionItemDTO("Median of Two Sorted Arrays", "Hard", "Binary Search", "Very High", "Binary search on the smaller array partition to divide combined elements equally in O(log(min(N,M))) time.", "https://leetcode.com/problems/median-of-two-sorted-arrays/", List.of("O(log(min(N,M))) time limit", "Partition boundary handling")));
        gQuestions.add(new InterviewQuestionItemDTO("Design Google Docs Real-Time Collaboration", "Hard", "System Design", "High", "Operational Transformation (OT) or CRDTs for concurrent text editing across distributed nodes.", "https://github.com/donnemartin/system-design-primer", List.of("CRDT vs OT tradeoffs", "WebSocket gateway architecture", "Snapshotting & version history")));
        google.setFrequentQuestions(gQuestions);

        google.setStudyTimeline(List.of(
            "Week 1: Graphs & Traversals (BFS, DFS, Topological Sort, Union-Find)",
            "Week 2: Advanced Dynamic Programming (2D grids, knapsack, interval DP)",
            "Week 3: Google-specific tagged LeetCode top 50 & Scalable System Design",
            "Week 4: Mock interviews on Google Docs (no IDE) & Googleyness behavioral scenarios"
        ));

        List<StudyMaterialItemDTO> gMaterials = new ArrayList<>();
        gMaterials.add(new StudyMaterialItemDTO("Google Tagged LeetCode 100", "Practice Platform", "https://leetcode.com/problem-list/google/", "Most frequently asked Google coding problems curated by difficulty", "Essential"));
        gMaterials.add(new StudyMaterialItemDTO("Google Engineering Practices Documentation", "Documentation", "https://google.github.io/eng-practices/", "Official Google guide on code reviews and software craftsmanship", "Official"));
        gMaterials.add(new StudyMaterialItemDTO("Designing Data-Intensive Applications (DDIA)", "Book", "https://dataintensive.net/", "The gold standard textbook for large-scale distributed system design", "Recommended"));
        google.setCuratedMaterials(gMaterials);
        companyStrategies.put("google", google);

        // 2. Amazon
        CompanyStudyStrategyDTO amazon = new CompanyStudyStrategyDTO();
        amazon.setId("amazon");
        amazon.setCompanyName("Amazon");
        amazon.setCategory("Tier-1 Product / FAANG");
        amazon.setPackageCTC("₹28.0 - 48.0 LPA");
        amazon.setLocation("Bangalore / Hyderabad / Chennai / Pune");
        amazon.setBrandColor("from-amber-500 to-orange-600");
        amazon.setDifficultyLevel("High");
        amazon.setOverview("Amazon's interview process is renowned for its dual focus on technical competency and its 16 Leadership Principles (LPs). Every technical round dedicates 15-20 minutes strictly to behavioral LP questions.");
        amazon.setHiringPhilosophy("Believes in hiring candidates that raise the bar. Demands Customer Obsession, Ownership, Bias for Action, and Deep Dive.");
        amazon.setKeyFocusAreas(List.of("16 Leadership Principles (STAR Stories)", "Object-Oriented Design (LLD)", "BFS/DFS & Priority Queues", "Microservices & Distributed Caching", "Bar Raiser Round"));

        List<RoundInfoDTO> aRounds = new ArrayList<>();
        aRounds.add(new RoundInfoDTO(1, "Amazon Online Assessment (OA)", "90 mins (2 Coding + Work Simulation)", "Debugging, algorithmic challenges, and Amazon work simulation scenarios.", List.of("Work Simulation questions must align with Amazon Leadership Principles", "Pass all test cases including large inputs")));
        aRounds.add(new RoundInfoDTO(2, "Technical Screening", "60 mins (20 min LP + 40 min Coding)", "A live coding session combined with two detailed STAR behavioral scenarios.", List.of("Have 2 distinct STAR stories for Customer Obsession and Ownership", "Explain space/time tradeoffs clearly")));
        aRounds.add(new RoundInfoDTO(3, "The Onsite 'Loop' (4-5 Rounds)", "5-6 hours total", "Includes DSA, Low-Level Object Oriented Design, System Design, and the famous Bar Raiser round.", List.of("The Bar Raiser has veto power; demonstrate deep humility and high standards", "Write clean extensible object models with SOLID principles")));
        amazon.setRounds(aRounds);

        List<InterviewQuestionItemDTO> aQuestions = new ArrayList<>();
        aQuestions.add(new InterviewQuestionItemDTO("Course Schedule II (Topological Sort)", "Medium", "Graphs & Kahn's Algorithm", "Very High", "Calculate in-degrees of courses and process queue for valid topological order.", "https://leetcode.com/problems/course-schedule-ii/", List.of("Detecting cycles in directed graphs", "Queue-based BFS traversal")));
        aQuestions.add(new InterviewQuestionItemDTO("Design Amazon Locker System (LLD / OOD)", "Medium", "Object-Oriented Design", "Very High", "Design classes for Locker, Package, LockerSize, CodeGenerator, and NotificationService.", null, List.of("SOLID principles application", "State design pattern for locker occupancy", "Extensibility for refrigerated lockers")));
        aQuestions.add(new InterviewQuestionItemDTO("Tell me about a time you had a disagreement with your team lead or manager", "Behavioral", "Have Backbone; Disagree and Commit", "Very High", "Demonstrate how you presented data-backed counter arguments respectfully and committed wholeheartedly to the team decision.", null, List.of("Focus on data over emotion", "Show total commitment post-decision")));
        amazon.setFrequentQuestions(aQuestions);

        amazon.setStudyTimeline(List.of(
            "Week 1: Prepare 10-12 STAR stories mapped to all 16 Leadership Principles",
            "Week 2: BFS/DFS, Top K Elements with Priority Queues, and Sliding Window",
            "Week 3: Low-Level Design (LLD) practicing Parking Lot, Elevator, and Amazon Locker",
            "Week 4: High-Level System Design (Design Amazon Video/Cart) & Mock Bar Raiser"
        ));

        List<StudyMaterialItemDTO> aMaterials = new ArrayList<>();
        aMaterials.add(new StudyMaterialItemDTO("Amazon 16 Leadership Principles Deep Dive", "Documentation", "https://www.aboutamazon.com/about-us/leadership-principles", "Official breakdown of Amazon's guiding principles with examples", "Essential"));
        aMaterials.add(new StudyMaterialItemDTO("Amazon Top 50 LeetCode Curated List", "Practice Platform", "https://leetcode.com/company/amazon/", "High-frequency coding problems reported in Amazon interviews", "Free"));
        amazon.setCuratedMaterials(aMaterials);
        companyStrategies.put("amazon", amazon);

        // 3. Microsoft
        CompanyStudyStrategyDTO msft = new CompanyStudyStrategyDTO();
        msft.setId("microsoft");
        msft.setCompanyName("Microsoft");
        msft.setCategory("Tier-1 Product / FAANG");
        msft.setPackageCTC("₹24.0 - 50.0 LPA");
        msft.setLocation("Hyderabad / Bangalore / Noida");
        msft.setBrandColor("from-blue-500 to-cyan-600");
        msft.setDifficultyLevel("Medium-High");
        msft.setOverview("Microsoft focuses heavily on practical, maintainable code, tree traversals, string manipulation, modular object-oriented thinking, and Azure/cloud readiness.");
        msft.setHiringPhilosophy("Values a 'Growth Mindset'—learning from mistakes, curiosity, cross-team empathy, and strong fundamental data structure craftsmanship.");
        msft.setKeyFocusAreas(List.of("Binary Trees & Binary Search Trees", "Linked Lists & Strings", "Modular Architecture & Clean Code", "Azure & Distributed Systems", "Growth Mindset & AA Round"));

        List<RoundInfoDTO> mRounds = new ArrayList<>();
        mRounds.add(new RoundInfoDTO(1, "Codility Online Assessment", "60-90 mins (3 questions)", "Codility platform testing edge cases, time complexity, and memory utilization.", List.of("Codility runs harsh corner-case tests; check integer overflow and null bounds")));
        mRounds.add(new RoundInfoDTO(2, "Technical Rounds 1 & 2", "45 mins each", "DSA problem solving with emphasis on code maintainability and modularity.", List.of("Write production-quality code with comments and unit test ideas")));
        mRounds.add(new RoundInfoDTO(3, "System Design & Architecture", "60 mins", "Design reliable web-scale architectures (e.g. Microsoft Teams chat, OneDrive sync).", List.of("Focus on consistency vs availability tradeoffs", "Show practical database indexing and message queueing")));
        mRounds.add(new RoundInfoDTO(4, "As-Appropriate (AA) Round", "60 mins with Partner / Director", "Senior leader evaluation on architectural breadth, technical passion, and Growth Mindset.", List.of("Show enthusiasm for continuous learning and technological impact")));
        msft.setRounds(mRounds);

        List<InterviewQuestionItemDTO> mQuestions = new ArrayList<>();
        mQuestions.add(new InterviewQuestionItemDTO("Lowest Common Ancestor of a Binary Tree", "Medium", "Trees & Recursion", "Very High", "Traverse recursively: if current root matches either target or both subtrees return non-null, current is LCA.", "https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree/", List.of("Base cases handling", "Recursive post-order traversal")));
        mQuestions.add(new InterviewQuestionItemDTO("Reverse Nodes in k-Group", "Hard", "Linked Lists", "High", "Count nodes, reverse k nodes iteratively, and recurse for remaining groups.", "https://leetcode.com/problems/reverse-nodes-in-k-group/", List.of("Pointer manipulation without extra space", "Handling remainder group preservation")));
        msft.setFrequentQuestions(mQuestions);

        msft.setStudyTimeline(List.of(
            "Week 1: Master Binary Trees, BSTs, and Recursion edge cases",
            "Week 2: Linked List in-place manipulation, Two Pointers, and String parsing",
            "Week 3: Microsoft top tagged LeetCode problems & High-level cloud architecture",
            "Week 4: Low-level clean code exercises & Growth Mindset behavioral preparation"
        ));

        List<StudyMaterialItemDTO> mMaterials = new ArrayList<>();
        mMaterials.add(new StudyMaterialItemDTO("NeetCode Microsoft Top 75", "Practice Platform", "https://neetcode.io/", "Filtered list of high-frequency Microsoft coding questions", "Free"));
        msft.setCuratedMaterials(mMaterials);
        companyStrategies.put("microsoft", msft);
    }

    private void initITServiceCompanies() {
        // 4. Capgemini
        CompanyStudyStrategyDTO capgemini = new CompanyStudyStrategyDTO();
        capgemini.setId("capgemini");
        capgemini.setCompanyName("Capgemini");
        capgemini.setCategory("IT Services & Global Consulting");
        capgemini.setPackageCTC("₹4.25 - 7.50 LPA (Analyst / Senior Analyst)");
        capgemini.setLocation("Mumbai / Pune / Bangalore / Hyderabad");
        capgemini.setBrandColor("from-blue-600 via-sky-600 to-indigo-700");
        capgemini.setDifficultyLevel("Competitive (Exceller Drive)");
        capgemini.setOverview("Capgemini recruits freshers and graduates across India primarily through the Capgemini Exceller on-campus and off-campus drives. Emphasizes pseudocode comprehension, game-based cognitive speed, English communication, and fundamental programming.");
        capgemini.setHiringPhilosophy("Looks for adaptable engineers with strong logical reasoning, swift mental agility (tested through gamified assessments), solid OOP basics, and effective verbal communication.");
        capgemini.setKeyFocusAreas(List.of("Pseudocode Analysis & Bitwise Operations", "English Communication & Speaking Assessment", "Game-Based Cognitive Agility", "Core Coding (Java/Python/C++)", "Data Structures & DBMS"));

        List<RoundInfoDTO> cRounds = new ArrayList<>();
        cRounds.add(new RoundInfoDTO(1, "Technical Assessment (Pseudocode Round)", "30 mins (30 Questions)", "Traces loops, bitwise XOR/AND shifts, recursion, conditional statements, and data structures.", List.of("Practice bitwise operations and operator precedence", "Dry-run recursive functions on paper with call stacks")));
        cRounds.add(new RoundInfoDTO(2, "English Communication Test", "30 mins (MCQs + Audio Spoken)", "Grammar, sentence completion, reading comprehension, pronunciation, and spoken fluency.", List.of("Speak clearly into the microphone without long pauses", "Focus on subject-verb agreement and preposition rules")));
        cRounds.add(new RoundInfoDTO(3, "Game-Based Cognitive Assessment", "4 Gamified Challenges", "Tests cognitive speed, working memory, spatial orientation, inductive reasoning, and multitasking.", List.of("Play practice cognitive games (Grid challenge, Motion challenge)", "Stay calm and prioritize accuracy over reckless speed")));
        cRounds.add(new RoundInfoDTO(4, "Coding Assessment (For Senior Analyst / Differential Band)", "45 mins (2 Problems)", "Hands-on coding challenges on arrays, strings, matrix manipulations, and basic DP.", List.of("Pass all sample and hidden test cases with boundary handling")));
        cRounds.add(new RoundInfoDTO(5, "Technical & HR Interview", "25-35 mins", "Evaluates final-year academic project, OOP concepts, SQL queries, resume technologies, and flexibility.", List.of("Be ready to explain project architecture and database schemas", "Demonstrate readiness for flexible project shifts and locations")));
        capgemini.setRounds(cRounds);

        List<InterviewQuestionItemDTO> cQuestions = new ArrayList<>();
        cQuestions.add(new InterviewQuestionItemDTO("Bitwise XOR and Shift Evaluation in Nested Loops", "Easy-Medium", "Pseudocode & Bitwise", "Very High", "Evaluate expressions like ((a ^ b) << 2) | (c >> 1) with operator precedence and bitwise properties.", null, List.of("XOR with same number is 0", "Left shift multiplies by 2^k", "Right shift divides by 2^k")));
        cQuestions.add(new InterviewQuestionItemDTO("Reverse Words in a Sentence while Preserving Spaces", "Easy", "String Manipulation", "Very High", "Split by space or reverse in-place using two-pointer swapping for words followed by space preservation.", null, List.of("In-place two pointer approach", "Handling multiple consecutive spaces")));
        cQuestions.add(new InterviewQuestionItemDTO("Difference Between TRUNCATE, DELETE, and DROP in SQL", "Core CS", "DBMS / SQL", "Very High", "DELETE is DML (logged, row-by-row, rollback possible), TRUNCATE is DDL (deallocates pages, resets identity), DROP removes entire table definition.", null, List.of("Rollback ability", "High-water mark reset", "DML vs DDL classification")));
        cQuestions.add(new InterviewQuestionItemDTO("Explain Method Overloading vs Method Overriding in Java/C++", "Core CS", "OOP Principles", "Very High", "Overloading is compile-time polymorphism (same name, different signature in same class); Overriding is runtime polymorphism (same signature in subclass).", null, List.of("Compile-time vs runtime binding", "@Override annotation safety", "Cannot override static or final methods")));
        capgemini.setFrequentQuestions(cQuestions);

        capgemini.setStudyTimeline(List.of(
            "Week 1: Pseudocode analysis drills, bitwise operators, loop tracing, and recursion dry-runs",
            "Week 2: English communication prep (reading comprehension, sentence correction) & gamified cognitive tests",
            "Week 3: Core CS Subject revision: OOP 4 pillars, DBMS & SQL Joins, basic Data Structures (Arrays, Strings, LinkedLists)",
            "Week 4: Final Year Project deep-dive, Capgemini mock test simulations, and behavioral HR questions"
        ));

        List<StudyMaterialItemDTO> cMaterials = new ArrayList<>();
        cMaterials.add(new StudyMaterialItemDTO("Capgemini Exceller Syllabus & Pattern", "Documentation", "https://www.geeksforgeeks.org/capgemini-recruitment-process/", "Complete breakdown of Capgemini test syllabus and round-by-round criteria", "Free"));
        cMaterials.add(new StudyMaterialItemDTO("Sanfoundry Pseudocode Tracing Questions", "Practice Platform", "https://www.sanfoundry.com/", "Extensive bank of pseudocode and algorithm tracing MCQs", "Free"));
        cMaterials.add(new StudyMaterialItemDTO("Top 50 Capgemini Interview Questions", "Cheatsheet", "https://www.javatpoint.com/capgemini-interview-questions", "Frequently asked technical and HR interview questions", "Essential"));
        capgemini.setCuratedMaterials(cMaterials);
        companyStrategies.put("capgemini", capgemini);

        // 5. TCS
        CompanyStudyStrategyDTO tcs = new CompanyStudyStrategyDTO();
        tcs.setId("tcs");
        tcs.setCompanyName("Tata Consultancy Services (TCS)");
        tcs.setCategory("IT Services & Global Consulting");
        tcs.setPackageCTC("₹3.60 - 9.00 LPA (Ninja / Digital / Prime)");
        tcs.setLocation("Pan India / Mumbai / Pune");
        tcs.setBrandColor("from-indigo-700 to-purple-800");
        tcs.setDifficultyLevel("Competitive (High Volume)");
        tcs.setOverview("TCS hires primarily through the National Qualifier Test (TCS NQT) for Ninja, Digital, and Prime roles. Emphasizes quantitative aptitude, verbal ability, fundamental coding, and core CS academics.");
        tcs.setHiringPhilosophy("Values academic consistency, analytical reasoning, strong foundational knowledge of OOP and SQL, and readiness to adapt to client project technologies.");
        tcs.setKeyFocusAreas(List.of("TCS NQT Cognitive Aptitude", "Basic to Intermediate Coding (C++/Java/Python)", "Object-Oriented Programming (OOP)", "Database Management & SQL Queries", "Software Engineering Life Cycle (SDLC)"));

        List<RoundInfoDTO> tRounds = new ArrayList<>();
        tRounds.add(new RoundInfoDTO(1, "TCS NQT Foundation Section", "75 mins (Numerical, Reasoning, Verbal)", "Covers percentages, probability, syllogisms, reading comprehension, and error spotting.", List.of("Time management is key; practice 30 mock aptitude tests", "No negative marking in recent patterns")));
        tRounds.add(new RoundInfoDTO(2, "TCS NQT Advanced Section (For Digital / Prime)", "90 mins (Advanced Aptitude + 2 Coding Problems)", "Advanced quantitative reasoning and 2 coding questions on arrays, strings, and matrix manipulation.", List.of("Solve both coding questions for Digital/Prime band qualification", "Handle negative numbers and edge bounds")));
        tRounds.add(new RoundInfoDTO(3, "Technical Interview (TR)", "30-45 mins", "Questions on candidate's final year project, OOP concepts, DBMS, and basic coding on a notepad.", List.of("Explain every table and API in your project clearly", "Be ready to write SQL queries with JOINs and GROUP BY")));
        tRounds.add(new RoundInfoDTO(4, "Managerial & HR Round (MR/HR)", "20-30 mins", "Checks flexibility for relocation, shifts, communication skills, and commitment.", List.of("Express willingness to learn new technologies and relocate", "Maintain a polite, confident, professional posture")));
        tcs.setRounds(tRounds);

        List<InterviewQuestionItemDTO> tQuestions = new ArrayList<>();
        tQuestions.add(new InterviewQuestionItemDTO("Find the N-th term in a mixed Prime/Fibonacci series", "Easy-Medium", "Logic & Math", "Very High", "Segregate odd and even indices to alternate between Fibonacci sequence and prime numbers.", null, List.of("Modulo operator for odd/even indices", "Sieve of Eratosthenes for prime numbers")));
        tQuestions.add(new InterviewQuestionItemDTO("Find the 2nd Highest Salary in an Employee Table", "Easy", "SQL / DBMS", "Very High", "SELECT MAX(salary) FROM Employee WHERE salary < (SELECT MAX(salary) FROM Employee); or using DENSE_RANK().", null, List.of("Handling duplicate salaries with DISTINCT", "DENSE_RANK() vs LIMIT/OFFSET")));
        tQuestions.add(new InterviewQuestionItemDTO("Explain the 4 Pillars of OOP with Real-World Examples", "Core CS", "OOP Concepts", "Very High", "Encapsulation, Abstraction, Inheritance, and Polymorphism with code snippets.", null, List.of("Method overloading vs overriding", "Interfaces vs abstract classes")));
        tcs.setFrequentQuestions(tQuestions);

        tcs.setStudyTimeline(List.of(
            "Week 1: Quantitative Aptitude (Time & Work, Speed & Distance, Percentages, Profit/Loss)",
            "Week 2: Logical Reasoning, Verbal Ability & String/Array coding problems",
            "Week 3: Core CS Subject revision: DBMS, SQL Joins, OOP 4 pillars, OS process vs thread",
            "Week 4: Final Year Project deep-dive & TCS NQT mock exam simulations"
        ));

        List<StudyMaterialItemDTO> tMaterials = new ArrayList<>();
        tMaterials.add(new StudyMaterialItemDTO("GeeksforGeeks TCS NQT Preparation Portal", "Documentation", "https://www.geeksforgeeks.org/tcs-nqt-preparation/", "Complete syllabus, previous year questions, and mock tests", "Free"));
        tMaterials.add(new StudyMaterialItemDTO("IndiaBIX Aptitude & Reasoning Tests", "Practice Platform", "https://www.indiabix.com/", "Free chapter-wise aptitude practice with step-by-step explanations", "Free"));
        tMaterials.add(new StudyMaterialItemDTO("SQL 50 LeetCode Study Plan", "Practice Platform", "https://leetcode.com/studyplan/top-sql-50/", "Essential SQL queries frequently tested in technical interviews", "Free"));
        tcs.setCuratedMaterials(tMaterials);
        companyStrategies.put("tcs", tcs);

        // 6. Infosys
        CompanyStudyStrategyDTO infosys = new CompanyStudyStrategyDTO();
        infosys.setId("infosys");
        infosys.setCompanyName("Infosys");
        infosys.setCategory("IT Services & Global Consulting");
        infosys.setPackageCTC("₹3.60 - 9.50 LPA (SE / DSE / Specialist)");
        infosys.setLocation("Pan India / Bangalore / Pune");
        infosys.setBrandColor("from-blue-600 to-indigo-700");
        infosys.setDifficultyLevel("Competitive");
        infosys.setOverview("Infosys hires through InfyTQ, HackWithInfy, and campus drives for System Engineer, Specialist Programmer (SP), and Digital Specialist Engineer (DSE) roles.");
        infosys.setHiringPhilosophy("Values mathematical and algorithmic thinking, clean modular coding in Java/Python, and sound understanding of database normalization.");
        infosys.setKeyFocusAreas(List.of("Pseudocode Analysis", "Mathematical & Puzzle Solving", "Java / Python Object-Oriented Programming", "Data Structures & Dynamic Programming", "Database Normalization & Queries"));

        List<RoundInfoDTO> infRounds = new ArrayList<>();
        infRounds.add(new RoundInfoDTO(1, "Infosys Online Qualifier", "100 mins (Reasoning, Math, Pseudocode, Verbal)", "Includes complex pseudocode tracing and mathematical analysis.", List.of("Practice bitwise operations and loop counters in pseudocode", "Pay attention to operator precedence")));
        infRounds.add(new RoundInfoDTO(2, "Hands-on Coding Assessment", "3 Coding Questions (Easy, Medium, Hard)", "Tests DP, string manipulation, and graph traversal for Specialist roles.", List.of("Solving 1 question qualifies for SE; 2+ qualifies for DSE/SP")));
        infRounds.add(new RoundInfoDTO(3, "Technical & HR Combined Interview", "30-40 mins", "Covers resume projects, coding puzzles, Java collection framework, and HR readiness.", List.of("Know your resume projects thoroughly", "Be confident in explaining Java/Python memory concepts")));
        infosys.setRounds(infRounds);

        List<InterviewQuestionItemDTO> infQuestions = new ArrayList<>();
        infQuestions.add(new InterviewQuestionItemDTO("Longest Increasing Subsequence (LIS)", "Medium", "Dynamic Programming", "Very High", "Calculate DP array where dp[i] is max length of increasing subsequence ending at index i in O(N log N) or O(N^2).", "https://leetcode.com/problems/longest-increasing-subsequence/", List.of("Patience sorting with binary search", "DP state definition")));
        infQuestions.add(new InterviewQuestionItemDTO("Difference between ArrayList and LinkedList in Java", "Core CS", "Java Collections", "Very High", "Contiguous array vs doubly-linked nodes, memory overhead, random access vs insertion speed.", null, List.of("O(1) vs O(N) access complexity", "Resizing mechanism in ArrayList")));
        infosys.setFrequentQuestions(infQuestions);

        infosys.setStudyTimeline(List.of(
            "Week 1: Pseudocode tracing & Mathematical Aptitude",
            "Week 2: Java/Python Collections, OOP Principles & String Manipulation",
            "Week 3: Recursion, Dynamic Programming & SQL Complex Queries",
            "Week 4: Mock interviews and Infosys sample paper solving"
        ));

        List<StudyMaterialItemDTO> infMaterials = new ArrayList<>();
        infMaterials.add(new StudyMaterialItemDTO("InfyTQ Practice Question Bank", "Practice Platform", "https://infytq.onwingspan.com/", "Official Infosys learning and certification portal", "Official"));
        infMaterials.add(new StudyMaterialItemDTO("GeeksforGeeks Infosys Archives", "Documentation", "https://www.geeksforgeeks.org/infosys-interview-questions/", "Real Infosys interview experiences and question patterns", "Free"));
        infosys.setCuratedMaterials(infMaterials);
        companyStrategies.put("infosys", infosys);

        // 7. Hexaware Technologies
        CompanyStudyStrategyDTO hexaware = new CompanyStudyStrategyDTO();
        hexaware.setId("hexaware");
        hexaware.setCompanyName("Hexaware Technologies");
        hexaware.setCategory("IT Services & Global Consulting");
        hexaware.setPackageCTC("₹3.00 - 4.00 LPA (Graduate Trainee / Testing)");
        hexaware.setLocation("Mumbai / Pune / Chennai");
        hexaware.setBrandColor("from-rose-600 via-pink-600 to-indigo-800");
        hexaware.setDifficultyLevel("Moderate (Campus Drive)");
        hexaware.setOverview("Hexaware is a fast-growing digital transformation and automation IT enterprise with over 34,000+ employees globally. Actively hires freshers from campus for Graduate Trainee, Testing, and Cloud Engineer roles.");
        hexaware.setHiringPhilosophy("Values strong cognitive aptitude, fundamentals of Software Testing Life Cycle (STLC), basic programming in Java or Python, and effective team communication.");
        hexaware.setKeyFocusAreas(List.of("Cognitive Aptitude & Logic", "Basic Programming in Java/Python", "Manual & Automation Testing Basics", "SQL Queries & Database Fundamentals", "Communication & Adaptability"));

        List<RoundInfoDTO> hRounds = new ArrayList<>();
        hRounds.add(new RoundInfoDTO(1, "Online Assessment (OA)", "90 mins (Aptitude, Domain MCQs & 2 Coding Problems)", "Covers numerical ability, logical deduction, verbal, plus 2 hands-on coding questions.", List.of("Speed is important in aptitude; solve easy questions first", "Ensure coding solution compiles with zero errors")));
        hRounds.add(new RoundInfoDTO(2, "Technical Interview", "30-40 mins", "Deep dive into your project, basic testing methodologies (Selenium, unit tests), OOP concepts, and SQL.", List.of("Be ready to write basic SQL queries and explain differences between test levels", "Demonstrate willingness to work in quality engineering or cloud")));
        hRounds.add(new RoundInfoDTO(3, "HR Interview", "15-20 mins", "Evaluation of communication, location preference, rotational shift flexibility, and career goals.", List.of("Maintain professional positive body language", "State clear commitment to long-term growth")));
        hexaware.setRounds(hRounds);

        List<InterviewQuestionItemDTO> hQuestions = new ArrayList<>();
        hQuestions.add(new InterviewQuestionItemDTO("Check if a String is a Palindrome without extra memory", "Easy", "String Manipulation", "Very High", "Use two pointers moving from front and back comparing characters.", null, List.of("Two-pointer technique", "Case-insensitive handling")));
        hQuestions.add(new InterviewQuestionItemDTO("Difference between Verification and Validation in Software Testing", "Testing Core", "STLC Concepts", "High", "Verification checks whether software conforms to specifications (Are we building the product right?); Validation checks if it fulfills customer needs (Are we building the right product?).", null, List.of("Static vs dynamic testing", "Review/inspections vs test execution")));
        hQuestions.add(new InterviewQuestionItemDTO("Write an SQL Query to find duplicate records in a table", "Easy", "SQL / DBMS", "Very High", "SELECT column_name, COUNT(*) FROM table_name GROUP BY column_name HAVING COUNT(*) > 1;", null, List.of("GROUP BY column", "HAVING filter on aggregate count")));
        hexaware.setFrequentQuestions(hQuestions);

        hexaware.setStudyTimeline(List.of(
            "Week 1: Quantitative aptitude, logical deduction, and verbal reasoning drills",
            "Week 2: Java/Python core basics, string manipulation, and array problems",
            "Week 3: STLC concepts (Test Cases, Bug Life Cycle, Manual Testing) & SQL Joins",
            "Week 4: Project review, notepad coding practice, and HR mock interviews"
        ));

        List<StudyMaterialItemDTO> hMaterials = new ArrayList<>();
        hMaterials.add(new StudyMaterialItemDTO("IndiaBIX Software Testing & Aptitude Portal", "Practice Platform", "https://www.indiabix.com/", "Complete practice bank for campus aptitude and technical MCQs", "Free"));
        hMaterials.add(new StudyMaterialItemDTO("Guru99 Software Testing Tutorial", "Documentation", "https://www.guru99.com/software-testing.html", "Clear explanations of STLC, manual testing, and automation concepts", "Free"));
        hexaware.setCuratedMaterials(hMaterials);
        companyStrategies.put("hexaware", hexaware);
    }

    private void initLocalCampusCompanies() {
        // 8. Wissen Technology
        CompanyStudyStrategyDTO wissen = new CompanyStudyStrategyDTO();
        wissen.setId("wissen-technology");
        wissen.setCompanyName("Wissen Technology");
        wissen.setCategory("Local & Campus Recruiter");
        wissen.setPackageCTC("₹9.50 LPA (8.0L Fixed + Retention)");
        wissen.setLocation("Mumbai / Bangalore (Hybrid)");
        wissen.setBrandColor("from-blue-800 to-indigo-950");
        wissen.setDifficultyLevel("High (FinTech Standards)");
        wissen.setOverview("Wissen Technology is an elite high-end technology consulting firm delivering mission-critical trading engines, algorithmic risk platforms, and digital architectures for Fortune 500 banks (Morgan Stanley, Goldman Sachs, Fidelity, Rakuten).");
        wissen.setHiringPhilosophy("Demands genuine computer science mastery: deep knowledge of Core Java concurrency, multithreading, memory management, algorithmic data structures (Trees, Graphs, DP), and low-level object-oriented design.");
        wissen.setKeyFocusAreas(List.of("Core Java Deep Dive & Concurrency", "Data Structures & Algorithms (Trees, DP, Graphs)", "Low-Level Design (LLD / Clean Architecture)", "Relational Databases & Performance Tuning", "Multithreading & Race Condition Prevention"));

        List<RoundInfoDTO> wRounds = new ArrayList<>();
        wRounds.add(new RoundInfoDTO(1, "Online Assessment (OA)", "90 mins (Coding + Core Java MCQs)", "Rigorous problem solving on HackerRank with strict time/memory complexity constraints.", List.of("Focus on optimal O(N) or O(N log N) solutions", "Avoid unnecessary memory allocations in loops")));
        wRounds.add(new RoundInfoDTO(2, "Technical Interview 1 (DSA + Core Java Deep Dive)", "60 mins Live Coding", "Deep probing into Java memory model, concurrent collections, garbage collection, and custom data structure implementations.", List.of("Be prepared to implement LinkedList or Tree algorithms without IDE", "Explain how HashMap handles hash collisions internally")));
        wRounds.add(new RoundInfoDTO(3, "Technical Interview 2 (System & Low-Level Design)", "60 mins Architectural Discussion", "Designing thread-safe in-memory caching systems, distributed order books, or producer-consumer pipelines.", List.of("Use SOLID principles and demonstrate knowledge of ReentrantLock / Semaphore", "Explain database index tree traversal and lock escalation")));
        wRounds.add(new RoundInfoDTO(4, "Management & Leadership Interview", "30-45 mins", "Evaluates technical curiosity, problem ownership, adaptability to complex financial domain logic, and communication.", List.of("Show passion for high-performance software engineering", "Demonstrate depth in your final-year project architecture")));
        wissen.setRounds(wRounds);

        List<InterviewQuestionItemDTO> wQuestions = new ArrayList<>();
        wQuestions.add(new InterviewQuestionItemDTO("Design and Implement a Thread-Safe LRU Cache in Java", "Medium-Hard", "Concurrency & Data Structures", "Very High", "Combine ConcurrentHashMap with a custom Doubly Linked List protected by ReentrantReadWriteLock or atomic references.", "https://leetcode.com/problems/lru-cache/", List.of("O(1) read and write time", "Thread-safety without full table locking", "Eviction policy under concurrent access")));
        wQuestions.add(new InterviewQuestionItemDTO("Explain Internal Working of HashMap in Java 8 (Treeification)", "Core Java", "Collections Internals", "Very High", "Array of Node buckets. When bucket length exceeds TREEIFY_THRESHOLD (8) and array capacity >= 64, converts linked list to Red-Black Tree for O(log N) worst-case lookup.", null, List.of("Hash code calculation and index masking", "equals() and hashCode() contract", "Rehashing at load factor 0.75")));
        wQuestions.add(new InterviewQuestionItemDTO("What happens when a deadlock occurs, and how do you prevent it in Java?", "Core Java", "Multithreading", "Very High", "Occurs when threads hold locks while waiting for locks held by each other. Prevent by strict lock ordering, tryLock() with timeout, and avoiding nested synchronized blocks.", null, List.of("Deadlock 4 conditions (Coffman)", "Thread dump analysis using jstack", "Lock ordering protocol")));
        wissen.setFrequentQuestions(wQuestions);

        wissen.setStudyTimeline(List.of(
            "Week 1: Core Java Collections internals (HashMap, ConcurrentHashMap, ArrayList) & JVM memory model",
            "Week 2: Multithreading & Concurrency (ExecutorService, synchronized, volatile, Locks, CountDownLatch)",
            "Week 3: LeetCode Medium/Hard on Trees, Graphs, Sliding Window, and Dynamic Programming",
            "Week 4: Low-Level Design (LLD) practice (Parking Lot, LRU Cache, Rate Limiter) & Mock Interviews"
        ));

        List<StudyMaterialItemDTO> wMaterials = new ArrayList<>();
        wMaterials.add(new StudyMaterialItemDTO("Java Concurrency in Practice Guide", "Book / Notes", "https://jcip.net/", "The authoritative reference for multi-threaded programming in Java", "Essential"));
        wMaterials.add(new StudyMaterialItemDTO("NeetCode 150 Core FinTech List", "Practice Platform", "https://neetcode.io/practice", "High-frequency coding patterns asked by financial technology consulting firms", "Free"));
        wMaterials.add(new StudyMaterialItemDTO("Baeldung Java Concurrency Tutorials", "Documentation", "https://www.baeldung.com/category/java/concurrency/", "Practical, code-first tutorials on modern Java concurrency APIs", "Free"));
        wissen.setCuratedMaterials(wMaterials);
        companyStrategies.put("wissen-technology", wissen);

        // 9. Sciative Solutions
        CompanyStudyStrategyDTO sciative = new CompanyStudyStrategyDTO();
        sciative.setId("sciative-solutions");
        sciative.setCompanyName("Sciative Solutions");
        sciative.setCategory("Local & Campus Recruiter");
        sciative.setPackageCTC("₹7.35 LPA (5.80L Fixed + Allowances)");
        sciative.setLocation("Navi Mumbai");
        sciative.setBrandColor("from-amber-500 via-orange-500 to-red-600");
        sciative.setDifficultyLevel("Medium-High (AI & Product SaaS)");
        sciative.setOverview("Sciative Solutions is India's leading AI-powered dynamic pricing SaaS product company that cuts across Retail, Travel, and Hospitality to optimize billions of price points daily.");
        sciative.setHiringPhilosophy("Looks for sharp Junior Software Development Engineers (Backend) who understand RESTful web services, database query performance, algorithmic logic, and asynchronous data processing.");
        sciative.setKeyFocusAreas(List.of("Backend Engineering (Python/Node/Java)", "REST API Architecture & Web Services", "Database Optimization & Indexing", "Data Structures (HashMaps, Arrays, Queues)", "Analytical & Pricing Problem Solving"));

        List<RoundInfoDTO> scRounds = new ArrayList<>();
        scRounds.add(new RoundInfoDTO(1, "Pre-Placement Talk & Behavioral Assessment", "45 mins", "Company overview, dynamic pricing product introduction, and initial mindset evaluation.", List.of("Take active notes during pre-placement talk", "Demonstrate genuine curiosity about AI pricing technology")));
        scRounds.add(new RoundInfoDTO(2, "Progressive Online Assessment (POA)", "60 mins", "Foundational computer science MCQs, logical thinking, and core coding questions.", List.of("Brush up on array manipulation and complexity analysis")));
        scRounds.add(new RoundInfoDTO(3, "Final Online Assessment", "60 mins", "In-depth coding problems testing edge cases, boundary conditions, and algorithmic efficiency.", List.of("Write clean, modular code with descriptive variable names")));
        scRounds.add(new RoundInfoDTO(4, "Manager Round Interview", "45 mins", "Technical deep dive into candidate projects, database schema design, indexing, and API lifecycle.", List.of("Explain how your backend handles concurrency and database queries", "Be ready to write SQL queries on live shared screen")));
        scRounds.add(new RoundInfoDTO(5, "Senior Management Interview", "30-45 mins", "Product intuition, alignment with high-speed startup culture, and problem-solving under ambiguity.", List.of("Show eagerness to learn dynamic pricing algorithms and high-throughput systems")));
        sciative.setRounds(scRounds);

        List<InterviewQuestionItemDTO> scQuestions = new ArrayList<>();
        scQuestions.add(new InterviewQuestionItemDTO("Design a Dynamic Discount Calculation Engine", "Medium", "Backend Architecture", "Very High", "Implement a pricing service that applies priority-based coupon codes, category discounts, and loyalty points while ensuring final price is never negative.", null, List.of("Strategy design pattern for discount rules", "Idempotency and transaction safety", "Validation of expiration dates and limits")));
        scQuestions.add(new InterviewQuestionItemDTO("Subarray Sum Equals K in O(N) Time", "Medium", "HashMaps & Prefix Sum", "Very High", "Use a HashMap to store prefix sums and check if (currentSum - K) has been seen before.", "https://leetcode.com/problems/subarray-sum-equals-k/", List.of("O(N) time and O(N) space", "Prefix sum array concept")));
        scQuestions.add(new InterviewQuestionItemDTO("How do you optimize slow SQL queries with Composite Indexes?", "Database", "SQL Performance", "High", "Analyze using EXPLAIN. Ensure the most selective column appears first in the composite index (Leftmost Prefix Rule).", null, List.of("Leftmost prefix rule", "Covering indexes avoiding table lookups")));
        sciative.setFrequentQuestions(scQuestions);

        sciative.setStudyTimeline(List.of(
            "Week 1: Core programming language syntax (Python/Java/Node) & HashMaps/Prefix-sums",
            "Week 2: RESTful API design, HTTP verbs, status codes, and database schema design",
            "Week 3: SQL indexing, JOIN optimization, and backend caching with Redis",
            "Week 4: Final year project architecture walkthrough & mock manager interviews"
        ));

        List<StudyMaterialItemDTO> scMaterials = new ArrayList<>();
        scMaterials.add(new StudyMaterialItemDTO("FastAPI / Spring Boot Backend Guide", "Documentation", "https://roadmap.sh/backend", "Visual roadmap for modern backend web development", "Free"));
        scMaterials.add(new StudyMaterialItemDTO("Database Indexing & Query Tuning", "Documentation", "https://use-the-index-luke.com/", "The definitive guide to relational database indexing for developers", "Essential"));
        sciative.setCuratedMaterials(scMaterials);
        companyStrategies.put("sciative-solutions", sciative);

        // 10. Recruit CRM
        CompanyStudyStrategyDTO recruitCrm = new CompanyStudyStrategyDTO();
        recruitCrm.setId("recruit-crm");
        recruitCrm.setCompanyName("Recruit CRM");
        recruitCrm.setCategory("Local & Campus Recruiter");
        recruitCrm.setPackageCTC("₹9.00 LPA (Remote Bootstrapped SaaS)");
        recruitCrm.setLocation("Remote / Mumbai");
        recruitCrm.setBrandColor("from-sky-500 via-blue-600 to-indigo-700");
        recruitCrm.setDifficultyLevel("Medium-High (SaaS Standards)");
        recruitCrm.setOverview("Recruit CRM is one of the world's most profitable bootstrapped remote-first SaaS companies, powering executive search and recruiting firms across 100+ countries with stellar customer ratings.");
        recruitCrm.setHiringPhilosophy("Seeks highly autonomous Trainee Software Engineers who take end-to-end ownership, communicate effectively in remote environments, write clean full-stack code, and learn quickly.");
        recruitCrm.setKeyFocusAreas(List.of("Full Stack / Backend Web Technologies", "Online MCQ & Aptitude Speed", "Practical Coding Assessment", "REST APIs & Webhooks", "Remote Team Communication & Ownership"));

        List<RoundInfoDTO> rcRounds = new ArrayList<>();
        rcRounds.add(new RoundInfoDTO(1, "Online MCQ Test", "45 mins", "Computer science fundamentals, web architecture, reasoning, and algorithmic logic.", List.of("Focus on accuracy and quick problem comprehension")));
        rcRounds.add(new RoundInfoDTO(2, "Coding Assessment", "60 mins (2 Problems)", "Practical programming challenges on data transformation, strings, and object mapping.", List.of("Write clean, readable code with comments", "Test for null inputs and empty lists")));
        rcRounds.add(new RoundInfoDTO(3, "Virtual Technical Interviews", "45-60 mins Live Coding & Architecture", "Code walkthrough, debugging edge cases, understanding of asynchronous events and webhooks.", List.of("Explain code clearly and show how you troubleshoot bugs", "Ask thoughtful questions about their SaaS product architecture")));
        rcRounds.add(new RoundInfoDTO(4, "Founder / Culture Fit Interview", "30 mins", "Assesses remote work discipline, self-motivation, high accountability, and cultural alignment.", List.of("Demonstrate why you thrive in autonomous, remote-first environments")));
        recruitCrm.setRounds(rcRounds);

        List<InterviewQuestionItemDTO> rcQuestions = new ArrayList<>();
        rcQuestions.add(new InterviewQuestionItemDTO("Design a Webhook Event Ingestion Pipeline", "Medium", "System Architecture", "Very High", "Accept incoming webhook payloads, validate signatures, enqueue into a queue (Redis/Kafka), and return immediate 200 OK.", null, List.of("Immediate 200 OK response to prevent sender timeouts", "Asynchronous background worker processing", "Deduplication with unique event IDs")));
        rcQuestions.add(new InterviewQuestionItemDTO("Group Anagrams in O(N * K) Time", "Medium", "HashMaps & Strings", "Very High", "Group strings that have identical character frequency counts or sorted representations.", "https://leetcode.com/problems/group-anagrams/", List.of("Character count tuple as HashMap key", "O(N * K) time complexity")));
        recruitCrm.setFrequentQuestions(rcQuestions);

        recruitCrm.setStudyTimeline(List.of(
            "Week 1: Modern JavaScript/TypeScript or Python web fundamentals & async operations",
            "Week 2: Data structures (Arrays, HashMaps, Strings) & LeetCode Easy/Medium practice",
            "Week 3: REST API creation, Webhook handling, and database relations",
            "Week 4: Mock technical interviews, remote work preparation, and portfolio demonstration"
        ));

        List<StudyMaterialItemDTO> rcMaterials = new ArrayList<>();
        rcMaterials.add(new StudyMaterialItemDTO("Full Stack Open Course", "Documentation", "https://fullstackopen.com/en/", "Modern web development with React, Node.js, and REST APIs", "Free"));
        rcMaterials.add(new StudyMaterialItemDTO("System Design for Web Developers", "Documentation", "https://github.com/donnemartin/system-design-primer", "Clear visual guide to web scaling and API design", "Free"));
        recruitCrm.setCuratedMaterials(rcMaterials);
        companyStrategies.put("recruit-crm", recruitCrm);

        // 11. Zeus Learning
        CompanyStudyStrategyDTO zeus = new CompanyStudyStrategyDTO();
        zeus.setId("zeus-learning");
        zeus.setCompanyName("Zeus Learning");
        zeus.setCategory("Local & Campus Recruiter");
        zeus.setPackageCTC("₹6.25 - 7.80 LPA");
        zeus.setLocation("Lower Parel, Mumbai");
        zeus.setBrandColor("from-teal-500 via-cyan-600 to-blue-700");
        zeus.setDifficultyLevel("High (Logical & Coding Rigor)");
        zeus.setOverview("Zeus Learning is a leading e-learning technology solutions provider developing enterprise LMS, simulation software, virtual science labs, and interactive rich-media applications for global publishers like McGraw-Hill, Pearson, and Harvard Business Publishing.");
        zeus.setHiringPhilosophy("Tests pure algorithmic logic, clean code craftsmanship, mathematical puzzle solving, and ability to construct complex web applications without relying on bloated libraries.");
        zeus.setKeyFocusAreas(List.of("Core JavaScript & Object-Oriented Programming", "Mathematical & Logic Puzzles", "Data Structures & Recursion", "Machine Test / Simulation Coding", "Code Cleanliness & DRY Principles"));

        List<RoundInfoDTO> zRounds = new ArrayList<>();
        zRounds.add(new RoundInfoDTO(1, "Online Aptitude & Technical Test", "60 mins", "Logical reasoning puzzles, mathematics, probability, and core programming output prediction.", List.of("Practice classic water jug, probability, and logic puzzles", "Trace recursive loops carefully")));
        zRounds.add(new RoundInfoDTO(2, "Technical Machine Test (Hands-on Coding)", "2-3 Hours", "Building an interactive game/simulation from scratch (e.g. Tic-Tac-Toe, Snake, Chess board validator, Canvas widget).", List.of("Focus on clean separation of logic from UI", "Handle all boundary conditions and game state resets gracefully")));
        zRounds.add(new RoundInfoDTO(3, "Personal Technical Interview", "45-60 mins", "Line-by-line review of your machine test code, deep dive into OOP concepts, data structures, and mathematical puzzles.", List.of("Be ready to refactor your machine test code on the spot", "Explain the Big-O complexity of every function written")));
        zeus.setRounds(zRounds);

        List<InterviewQuestionItemDTO> zQuestions = new ArrayList<>();
        zQuestions.add(new InterviewQuestionItemDTO("Implement 2D Tic-Tac-Toe with Arbitrary Board Size (N x N)", "Medium", "Machine Coding / Arrays", "Very High", "Track row sums, column sums, main diagonal, and anti-diagonal sums in O(1) time per move.", null, List.of("O(1) time complexity per move", "Generalization from 3x3 to NxN", "Clean object model separating Board, Player, and GameEngine")));
        zQuestions.add(new InterviewQuestionItemDTO("Deep Clone an Object Supporting Nested Objects and Arrays", "Medium", "JavaScript Internals", "Very High", "Recursively clone properties while maintaining prototype chain and handling circular references with a WeakMap.", null, List.of("WeakMap for cycle detection", "Handling Date, RegExp, and Array types")));
        zeus.setFrequentQuestions(zQuestions);

        zeus.setStudyTimeline(List.of(
            "Week 1: Mathematical logic puzzles, probability, and analytical aptitude drills",
            "Week 2: Core JavaScript/Java OOP principles, prototype chains, and custom class design",
            "Week 3: Machine coding practice: building small games/widgets (Tic-Tac-Toe, Calculator) from scratch",
            "Week 4: Code refactoring exercises, clean code architecture, and live interview simulation"
        ));

        List<StudyMaterialItemDTO> zMaterials = new ArrayList<>();
        zMaterials.add(new StudyMaterialItemDTO("Eloquent JavaScript Modern Guide", "Book", "https://eloquentjavascript.net/", "The classic textbook on JavaScript language fundamentals and game development", "Essential"));
        zMaterials.add(new StudyMaterialItemDTO("GeeksforGeeks Puzzles for Interviews", "Practice Platform", "https://www.geeksforgeeks.org/puzzles/", "Must-solve logic and mathematical puzzles for technical interviews", "Free"));
        zeus.setCuratedMaterials(zMaterials);
        companyStrategies.put("zeus-learning", zeus);

        // 12. Godrej Infotech
        CompanyStudyStrategyDTO godrej = new CompanyStudyStrategyDTO();
        godrej.setId("godrej-infotech");
        godrej.setCompanyName("Godrej Infotech");
        godrej.setCategory("Local & Campus Recruiter");
        godrej.setPackageCTC("₹4.00 LPA (Senior Executive)");
        godrej.setLocation("Vikhroli, Mumbai");
        godrej.setBrandColor("from-rose-700 to-red-800");
        godrej.setDifficultyLevel("Moderate (Box Test Pattern)");
        godrej.setOverview("Godrej Infotech Ltd, a key pillar of the 125-year-old Godrej Enterprises Group, delivers end-to-end digital enterprise solutions, ERP implementation (Microsoft Dynamics, Infor), cloud migration, and bespoke software.");
        godrej.setHiringPhilosophy("Values structured procedural thinking, strong written test performance ('Box Test'), enterprise database modeling, and domain problem solving.");
        godrej.setKeyFocusAreas(List.of("'Box Test' Written Programming & Aptitude", "Relational Databases & SQL Queries", "Object-Oriented Programming (Java / C#)", "Software Engineering & SDLC", "Technical Documentation & Communication"));

        List<RoundInfoDTO> gdRounds = new ArrayList<>();
        gdRounds.add(new RoundInfoDTO(1, "Box Test Part 1 (Written Assessment - 1 Hour)", "60 mins Written", "Logical reasoning, quantitative aptitude, and core computer science fundamentals.", List.of("Focus on accuracy on foundational CS and arithmetic questions")));
        gdRounds.add(new RoundInfoDTO(2, "Box Test Part 2 (Written Technical - 2 Hours)", "120 mins Written", "Detailed written programming logic, pseudocode, database schema design, and SQL queries on paper.", List.of("Write clean, readable handwritten code with proper indentation", "Double-check table relationships and foreign keys in database schemas")));
        gdRounds.add(new RoundInfoDTO(3, "Technical & HR Combined Interview", "30-40 mins", "Discussion based on Box Test answers, final year project, database design, and company loyalty.", List.of("Be ready to explain any mistakes from your written test", "Show strong enthusiasm for enterprise digital transformation and ERP systems")));
        godrej.setRounds(gdRounds);

        List<InterviewQuestionItemDTO> gdQuestions = new ArrayList<>();
        gdQuestions.add(new InterviewQuestionItemDTO("Design and Normalize an Order Management Schema to 3NF", "Database", "DBMS Normalization", "Very High", "Break down Customer, Orders, OrderItems, and Products ensuring atomic columns, no partial dependencies, and no transitive dependencies.", null, List.of("Primary and Foreign key constraints", "1NF, 2NF, 3NF criteria", "Referential integrity")));
        gdQuestions.add(new InterviewQuestionItemDTO("Difference between Interface and Abstract Class with Use Cases", "Core CS", "OOP Concepts", "Very High", "Interfaces define contracts (multiple inheritance supported); Abstract classes provide default implementations and share common state.", null, List.of("Multiple inheritance in interfaces", "Instance variables in abstract classes")));
        godrej.setFrequentQuestions(gdQuestions);

        godrej.setStudyTimeline(List.of(
            "Week 1: Quantitative aptitude, logical reasoning, and paper-based mock test solving",
            "Week 2: Database normalization (1NF to 3NF), ER diagram modeling, and complex SQL joins",
            "Week 3: OOP concepts (Java/C#) and handwriting code algorithms on paper",
            "Week 4: Final year project walkthrough and professional HR interview preparation"
        ));

        List<StudyMaterialItemDTO> gdMaterials = new ArrayList<>();
        gdMaterials.add(new StudyMaterialItemDTO("Database System Concepts by Korth Notes", "Documentation", "https://www.geeksforgeeks.org/dbms/", "Comprehensive database normalization and SQL tutorials", "Free"));
        godrej.setCuratedMaterials(gdMaterials);
        companyStrategies.put("godrej-infotech", godrej);

        // 13. Servosys Solutions
        CompanyStudyStrategyDTO servosys = new CompanyStudyStrategyDTO();
        servosys.setId("servosys-solutions");
        servosys.setCompanyName("Servosys Solutions");
        servosys.setCategory("Local & Campus Recruiter");
        servosys.setPackageCTC("₹5.00 LPA");
        servosys.setLocation("Mumbai");
        servosys.setBrandColor("from-red-600 via-slate-800 to-blue-900");
        servosys.setDifficultyLevel("Moderate-High");
        servosys.setOverview("Servosys Solutions is one of India's fastest-growing enterprise automation software providers (BPM, DMS, RPA, Imaging) powering core processes for premier Indian banks like ICICI Bank, Axis Bank, HDFC, and Fino Payments Bank.");
        servosys.setHiringPhilosophy("Values strong knowledge of backend enterprise software, multi-tiered database operations, transactional consistency in financial software, and core Java/Spring Boot.");
        servosys.setKeyFocusAreas(List.of("Core Java & Enterprise Architecture", "Relational Databases & ACID Transactions", "Aptitude & Logical Reasoning", "Software Design Patterns", "Banking Workflow Systems"));

        List<RoundInfoDTO> svRounds = new ArrayList<>();
        svRounds.add(new RoundInfoDTO(1, "Online Aptitude & Technical Test", "60 mins", "Aptitude, quantitative logic, Java collections, and basic SQL query questions.", List.of("Practice numerical speed drills and Java output prediction")));
        svRounds.add(new RoundInfoDTO(2, "Technical Interview 1", "40-50 mins", "Core Java deep dive, exception handling hierarchy, multithreading, and writing SQL queries.", List.of("Explain difference between Checked and Unchecked exceptions", "Write clean SQL queries with INNER and LEFT JOINs")));
        svRounds.add(new RoundInfoDTO(3, "Technical Interview 2 / Managerial", "30-40 mins", "Discussion on banking workflows, document management architectures, and candidate project scalability.", List.of("Demonstrate understanding of transactional consistency and ACID properties")));
        svRounds.add(new RoundInfoDTO(4, "HR Interview", "15-20 mins", "Terms discussion, relocation readiness, communication skills, and final offer details.", List.of("Maintain professional confidence and clear articulation")));
        servosys.setRounds(svRounds);

        List<InterviewQuestionItemDTO> svQuestions = new ArrayList<>();
        svQuestions.add(new InterviewQuestionItemDTO("How are Database Transactions Managed in Financial Applications?", "Backend / SQL", "ACID Transactions", "Very High", "Atomic commit/rollback, transaction isolation levels (Read Committed vs Repeatable Read), and pessimistic vs optimistic locking.", null, List.of("ACID properties", "Dirty reads vs phantom reads", "Spring @Transactional rollbackFor configuration")));
        svQuestions.add(new InterviewQuestionItemDTO("Explain Java Exception Hierarchy (Throwable, Error, Exception)", "Core Java", "Exception Handling", "Very High", "Throwable is parent of Error (unrecoverable JVM issues) and Exception (checked vs RuntimeException unchecked).", null, List.of("Checked vs Unchecked exceptions", "try-with-resources auto-closing", "Custom exception creation")));
        servosys.setFrequentQuestions(svQuestions);

        servosys.setStudyTimeline(List.of(
            "Week 1: Quantitative aptitude and core Java syntax revision",
            "Week 2: Java Collections (List, Set, Map) and Exception Handling in enterprise apps",
            "Week 3: Relational database queries, transaction management, and Spring Boot basics",
            "Week 4: Banking software workflow case studies and mock managerial interviews"
        ));

        List<StudyMaterialItemDTO> svMaterials = new ArrayList<>();
        svMaterials.add(new StudyMaterialItemDTO("Baeldung Core Java & Spring Tutorials", "Documentation", "https://www.baeldung.com/", "Best-in-class practical guides for modern Java and database development", "Essential"));
        servosys.setCuratedMaterials(svMaterials);
        companyStrategies.put("servosys-solutions", servosys);

        // 14. Stetig Consulting
        CompanyStudyStrategyDTO stetig = new CompanyStudyStrategyDTO();
        stetig.setId("stetig-consulting");
        stetig.setCompanyName("Stetig Consulting");
        stetig.setCategory("Local & Campus Recruiter");
        stetig.setPackageCTC("₹4.00 LPA");
        stetig.setLocation("Pune / Mumbai");
        stetig.setBrandColor("from-teal-600 to-emerald-700");
        stetig.setDifficultyLevel("Moderate (Salesforce Focus)");
        stetig.setOverview("Stetig Consulting is a Salesforce Gold Consulting partner delivering cloud-based digital transformation, mobile enterprise apps, and process consulting for over 200+ enterprise clients.");
        stetig.setHiringPhilosophy("Looks for candidates with strong OOP fundamentals, understanding of relational database relationships, and enthusiasm for cloud ecosystems (Salesforce Apex / LWC).");
        stetig.setKeyFocusAreas(List.of("Object-Oriented Programming (OOP)", "Database Relations & SOQL/SQL", "Web Technologies (JavaScript, HTML, CSS)", "Logical Problem Solving", "Cloud & CRM Concepts"));

        List<RoundInfoDTO> stRounds = new ArrayList<>();
        stRounds.add(new RoundInfoDTO(1, "Assessment Test", "60 mins", "Aptitude, logical deduction, and hands-on coding problem solving.", List.of("Practice string and array programming questions")));
        stRounds.add(new RoundInfoDTO(2, "Technical Interview 1", "30-45 mins", "Evaluation of OOP principles, Java/JavaScript syntax, database relationships, and algorithmic reasoning.", List.of("Explain inheritance and polymorphism with clean real-world examples")));
        stRounds.add(new RoundInfoDTO(3, "Technical Interview 2 & Management", "30 mins", "Discussion on cloud architectures, willingness to undergo Salesforce training/certification, and HR fit.", List.of("Show excitement about learning Salesforce Apex and Lightning Web Components")));
        stetig.setRounds(stRounds);

        List<InterviewQuestionItemDTO> stQuestions = new ArrayList<>();
        stQuestions.add(new InterviewQuestionItemDTO("Explain the Difference Between Relational Database Relationships (1:1, 1:N, M:N)", "Database", "Data Modeling", "Very High", "1:1 with unique foreign key, 1:N with standard foreign key in child, M:N resolved using junction/bridge table.", null, List.of("Foreign key constraints", "Junction table implementation", "Cascading deletes")));
        stetig.setFrequentQuestions(stQuestions);

        stetig.setStudyTimeline(List.of(
            "Week 1: Core OOP principles in Java/Python and basic algorithmic problem solving",
            "Week 2: Database normalization and entity relationship modeling (1:1, 1:N, M:N)",
            "Week 3: Web basics (HTML, CSS, JavaScript) & introduction to Salesforce cloud fundamentals",
            "Week 4: Project review and interview communication practice"
        ));

        List<StudyMaterialItemDTO> stMaterials = new ArrayList<>();
        stMaterials.add(new StudyMaterialItemDTO("Salesforce Trailhead Free Learning", "Documentation", "https://trailhead.salesforce.com/", "Official gamified learning platform for Salesforce CRM and cloud technologies", "Official"));
        stetig.setCuratedMaterials(stMaterials);
        companyStrategies.put("stetig-consulting", stetig);

        // 15. Fristine Infotech
        CompanyStudyStrategyDTO fristine = new CompanyStudyStrategyDTO();
        fristine.setId("fristine-infotech");
        fristine.setCompanyName("Fristine Infotech");
        fristine.setCategory("Local & Campus Recruiter");
        fristine.setPackageCTC("Competitive (Not Disclosed)");
        fristine.setLocation("Pune / Vapi / Ambasamudram");
        fristine.setBrandColor("from-sky-600 to-blue-800");
        fristine.setDifficultyLevel("Comprehensive (Task-Based)");
        fristine.setOverview("Fristine Infotech is a Premium Zoho Partner serving enterprise clients across the APAC region with digital transformation, cloud integrations, and business automation software.");
        fristine.setHiringPhilosophy("Evaluates candidates across analytical aptitude (strict 75% cutoff on 50 MCQs), group communication, practical technical product building (12-hour take-home task), and commercial problem solving.");
        fristine.setKeyFocusAreas(List.of("Aptitude & Quantitative Speed (75% cutoff)", "Group Discussion & Articulation", "Hands-on Technical Product Task (12 Hours)", "Business Automation & Workflow Logic", "Management Alignment"));

        List<RoundInfoDTO> frRounds = new ArrayList<>();
        frRounds.add(new RoundInfoDTO(1, "Aptitude Test", "60 mins (50 MCQs - 75% Cutoff, No Negative Marking)", "Speed and accuracy on arithmetic, logical deduction, and verbal comprehension.", List.of("Strict 75% passing cutoff: attempt all questions quickly")));
        frRounds.add(new RoundInfoDTO(2, "Group Discussion (GD)", "60 mins (10 candidates per group)", "Evaluates active listening, articulate expression, topical tech/business knowledge, and leadership.", List.of("Speak clearly, build constructively on others' points, and do not dominate or interrupt aggressively")));
        frRounds.add(new RoundInfoDTO(3, "Technical Product Test / Task", "12 Hours Take-Home", "Building a functional business automation workflow, mini-app, or data integration script.", List.of("Submit clean, working code with clear documentation on how to run it", "Ensure edge cases and validation are fully handled")));
        frRounds.add(new RoundInfoDTO(4, "Technical Interview", "30 mins", "Deep dive into your submitted task code, architectural decisions, and computer science basics.", List.of("Walk through your code line by line with total confidence")));
        frRounds.add(new RoundInfoDTO(5, "Final Management Interview", "30 mins", "Alignment with company culture, client-facing readiness, and long-term career aspirations.", List.of("Demonstrate business mindset and willingness to master cloud CRM platforms")));
        fristine.setRounds(frRounds);

        List<InterviewQuestionItemDTO> frQuestions = new ArrayList<>();
        frQuestions.add(new InterviewQuestionItemDTO("Design a Lead Management Workflow with Multi-Tier Escalations", "System Design", "Business Automation", "Very High", "Model statuses (New, Contacted, Qualified, Closed), trigger automated email notifications, and auto-escalate if unaddressed after 24 hours.", null, List.of("State machine modeling", "Scheduled background triggers", "Audit log tracking")));
        fristine.setFrequentQuestions(frQuestions);

        fristine.setStudyTimeline(List.of(
            "Week 1: High-speed aptitude drills to comfortably cross 75% score on 50 MCQs",
            "Week 2: Group discussion practice on current tech topics & effective verbal articulation",
            "Week 3: Practical mini-product building (REST APIs, webhooks, forms, and validation)",
            "Week 4: Submitted task walkthrough practice & management interview readiness"
        ));

        List<StudyMaterialItemDTO> frMaterials = new ArrayList<>();
        frMaterials.add(new StudyMaterialItemDTO("IndiaBIX Aptitude Drills", "Practice Platform", "https://www.indiabix.com/", "Practice 50-question mock tests to build speed for the 75% cutoff", "Free"));
        fristine.setCuratedMaterials(frMaterials);
        companyStrategies.put("fristine-infotech", fristine);
    }

    private void initRoleData() {
        // 1. Frontend Developer
        RoleStudyStrategyDTO fe = new RoleStudyStrategyDTO();
        fe.setId("frontend-developer");
        fe.setRoleTitle("Frontend Developer (React / Next.js)");
        fe.setCategory("Web Development");
        fe.setIcon("Layout");
        fe.setExperienceLevel("Fresher to Senior");
        fe.setOverview("Frontend engineers build high-performance, accessible, and responsive user interfaces using modern JavaScript/TypeScript, React, Next.js, and CSS frameworks.");
        fe.setCoreCompetencies(List.of("JavaScript (ES6+) & TypeScript", "React.js Core & Custom Hooks", "Next.js (App Router, SSR, SSG)", "State Management (Redux/Zustand)", "Performance & Core Web Vitals", "CSS Architecture & Tailwind"));
        fe.setCoreTopicsToMaster(List.of("Event Loop & Microtasks", "Closures & Prototypes", "React Reconciliation & Virtual DOM", "Debouncing & Throttling Polyfills", "Core Web Vitals (LCP, CLS, INP)", "Accessibility (WCAG & ARIA)"));

        List<Map<String, Object>> feRoadmap = new ArrayList<>();
        feRoadmap.add(Map.of("stage", "Phase 1", "title", "Advanced Modern JavaScript", "description", "Master asynchronous JavaScript, prototypes, scopes, closures, and DOM rendering engines.", "topics", List.of("Promises & async/await", "Event Delegation", "Currying & Closures", "Polyfill Implementations")));
        feRoadmap.add(Map.of("stage", "Phase 2", "title", "React Deep Dive", "description", "Understand component lifecycles, hook internals, memoization, and custom hook development.", "topics", List.of("useMemo, useCallback & React.memo", "Custom Hooks (useFetch, useDebounce)", "Context API vs Zustand", "Error Boundaries")));
        feRoadmap.add(Map.of("stage", "Phase 3", "title", "Next.js & Production Architecture", "description", "Architect full-featured web applications using Server Components, caching layers, and SEO best practices.", "topics", List.of("Next.js App Router", "Server vs Client Components", "Route Handlers & Server Actions", "Image & Font Optimization")));
        feRoadmap.add(Map.of("stage", "Phase 4", "title", "Testing & Performance Tuning", "description", "Write robust unit and integration tests and optimize runtime performance.", "topics", List.of("Jest & React Testing Library", "Lighthouse & Web Vitals Audit", "Code Splitting & Lazy Loading", "CI/CD & Vercel Deployment")));
        fe.setLearningRoadmap(feRoadmap);

        List<RoundInfoDTO> feRounds = new ArrayList<>();
        feRounds.add(new RoundInfoDTO(1, "Machine Coding / UI Component Round", "60-90 mins", "Build an interactive UI component (e.g. Autocomplete Search, Infinite Scroll, Star Rating) from scratch.", List.of("Handle loading, error, and empty states gracefully", "Implement debouncing and cleanup on unmount")));
        feRounds.add(new RoundInfoDTO(2, "JavaScript & React Deep Dive", "45-60 mins", "Theoretical and practical coding on closures, event loop, React rendering triggers, and state batching.", List.of("Explain why unnecessary re-renders occur and how to measure them using React Profiler")));
        feRounds.add(new RoundInfoDTO(3, "Frontend System Design", "60 mins", "Architecting large client-side applications (e.g. Design Twitter Web App, Real-Time Chat, Video Player).", List.of("Discuss state management, data normalization, offline sync, caching, and bundle size splitting")));
        fe.setStandardInterviewRounds(feRounds);

        List<InterviewQuestionItemDTO> feQuestions = new ArrayList<>();
        feQuestions.add(new InterviewQuestionItemDTO("Implement a custom useDebounce hook and explain its cleanup", "Medium", "React Hooks", "Very High", "Use setTimeout inside useEffect with dependency array and return clearTimeout in cleanup function.", null, List.of("Memory leak prevention", "Dependency tracking")));
        feQuestions.add(new InterviewQuestionItemDTO("Implement Promise.all polyfill from scratch", "Medium", "JavaScript Internals", "Very High", "Return a new Promise, track resolved count in an array, reject immediately on any single rejection.", null, List.of("Preserving original order of promises", "Early rejection on error")));
        feQuestions.add(new InterviewQuestionItemDTO("What is the difference between Server Components and Client Components in Next.js?", "Core Concept", "Next.js App Router", "High", "Server Components run only on the server, zero bundle size, direct DB access. Client components enable state and interactivity.", null, List.of("'use client' directive", "Hydration process and boundaries")));
        fe.setTopInterviewQuestions(feQuestions);

        List<Map<String, Object>> feProjects = new ArrayList<>();
        feProjects.add(Map.of("title", "High-Performance Analytics Dashboard", "description", "Interactive financial or SaaS dashboard with real-time charts, dark mode, and CSV export.", "techStack", "Next.js 15, Tailwind CSS, Recharts, Lucide Icons", "keyFeatures", List.of("Virtual scrolling for 10k rows", "Optimistic UI updates", "100 Lighthouse Performance score")));
        feProjects.add(Map.of("title", "Real-Time Collaborative Kanban Board", "description", "Trello-like drag-and-drop workspace with WebSockets synchronization across multiple browsers.", "techStack", "React, TypeScript, dnd-kit, Socket.io, Tailwind CSS", "keyFeatures", List.of("Drag-and-drop accessibility", "Conflict resolution", "Offline persistent storage")));
        fe.setPortfolioProjects(feProjects);

        List<StudyMaterialItemDTO> feMaterials = new ArrayList<>();
        feMaterials.add(new StudyMaterialItemDTO("Roadmap.sh Frontend Developer Guide", "Documentation", "https://roadmap.sh/frontend", "Step-by-step visual roadmap for frontend engineering", "Free"));
        feMaterials.add(new StudyMaterialItemDTO("GreatFrontEnd Machine Coding Practice", "Practice Platform", "https://www.greatfrontend.com/", "Curated UI component coding challenges asked at top tech companies", "Free"));
        feMaterials.add(new StudyMaterialItemDTO("JavaScript.info Modern Tutorial", "Documentation", "https://javascript.info/", "The most in-depth modern JavaScript guide from basics to advanced internals", "Essential"));
        fe.setCuratedMaterials(feMaterials);
        roleStrategies.put("frontend-developer", fe);

        // 2. Backend Developer
        RoleStudyStrategyDTO be = new RoleStudyStrategyDTO();
        be.setId("backend-developer");
        be.setRoleTitle("Backend Developer (Java / Spring Boot / Node.js)");
        be.setCategory("Backend Engineering");
        be.setIcon("Server");
        be.setExperienceLevel("Fresher to Senior");
        be.setOverview("Backend engineers design reliable server-side services, robust REST & gRPC APIs, database architectures, and distributed asynchronous workflows.");
        be.setCoreCompetencies(List.of("Java / Spring Boot or Node.js/Go", "Relational Databases (MySQL, PostgreSQL)", "NoSQL & In-Memory Stores (MongoDB, Redis)", "REST & GraphQL API Design", "Authentication & Security (JWT, OAuth2)", "Message Brokers (Kafka, RabbitMQ)"));
        be.setCoreTopicsToMaster(List.of("ACID Properties & Transaction Isolation", "B-Tree vs Hash Indexing & Query Tuning", "Spring Bean Lifecycle & IoC Container", "Concurrency, Multithreading & Thread Pools", "Caching Patterns (Cache-Aside, Write-Through)", "Idempotency & Distributed Rate Limiting"));

        List<Map<String, Object>> beRoadmap = new ArrayList<>();
        beRoadmap.add(Map.of("stage", "Phase 1", "title", "Core Language & Concurrency", "description", "Master OOP, memory models, multithreading, thread safety, and collection internals.", "topics", List.of("JVM Internals / Node Event Loop", "ExecutorService & ThreadPools", "Concurrent Collections", "Garbage Collection Tuning")));
        beRoadmap.add(Map.of("stage", "Phase 2", "title", "Framework & REST APIs", "description", "Build enterprise APIs with validation, security, dependency injection, and exception handling.", "topics", List.of("Spring Boot 3 / Express", "Spring Security & JWT", "Spring Data JPA & Hibernate", "Global Exception Handling")));
        beRoadmap.add(Map.of("stage", "Phase 3", "title", "Databases & Performance", "description", "Optimize query performance, indexing, schema normalization, and connection pooling.", "topics", List.of("MySQL Index Optimization", "HikariCP Tuning", "Redis Caching Strategies", "Transactions & Pessimistic/Optimistic Locking")));
        beRoadmap.add(Map.of("stage", "Phase 4", "title", "Microservices & Distributed Systems", "description", "Design decoupled architectures with event-driven messaging, circuit breakers, and Docker.", "topics", List.of("Apache Kafka / RabbitMQ", "Docker & Containerization", "API Gateway & Service Discovery", "Distributed Tracing & Metrics")));
        be.setLearningRoadmap(beRoadmap);

        List<RoundInfoDTO> beRounds = new ArrayList<>();
        beRounds.add(new RoundInfoDTO(1, "API & Low-Level Design (LLD)", "60 mins", "Design and implement a clean service with controllers, models, services, and repositories.", List.of("Follow SOLID principles and clean separation of concerns", "Handle validation and HTTP error codes properly")));
        beRounds.add(new RoundInfoDTO(2, "Concurrency & Database Deep Dive", "45-60 mins", "Discussion on transaction isolation levels, indexing, deadlock prevention, and connection leaks.", List.of("Explain EXPLAIN query plans", "Discuss pessimistic vs optimistic locking with version columns")));
        beRounds.add(new RoundInfoDTO(3, "High-Level System Design (HLD)", "60 mins", "Architecting resilient services (e.g. Payment Gateway, Notification Engine, URL Shortener).", List.of("Address idempotency, message deduplication, and retry mechanisms with exponential backoff")));
        be.setStandardInterviewRounds(beRounds);

        List<InterviewQuestionItemDTO> beQuestions = new ArrayList<>();
        beQuestions.add(new InterviewQuestionItemDTO("How does database indexing work internally and when should you avoid indexes?", "Medium", "Database Management", "Very High", "B-Tree indexes allow O(log N) lookups but add overhead on write/update operations. Avoid indexing low-cardinality columns.", null, List.of("Composite index column order (leftmost prefix)", "Index overhead on INSERT/UPDATE/DELETE")));
        beQuestions.add(new InterviewQuestionItemDTO("How do you guarantee idempotency in payment processing APIs?", "Medium-Hard", "System Architecture", "Very High", "Pass a unique Idempotency-Key header, store processed transaction IDs in Redis/DB with distributed locks.", null, List.of("Distributed lock with Redis Redlock", "Database unique constraint on transaction key")));
        be.setTopInterviewQuestions(beQuestions);

        List<StudyMaterialItemDTO> beMaterials = new ArrayList<>();
        beMaterials.add(new StudyMaterialItemDTO("Roadmap.sh Backend Developer Guide", "Documentation", "https://roadmap.sh/backend", "Comprehensive backend engineering learning roadmap", "Free"));
        beMaterials.add(new StudyMaterialItemDTO("Baeldung Spring Boot Deep Dive Tutorials", "Documentation", "https://www.baeldung.com/", "The premier learning resource for Spring Boot and modern Java", "Essential"));
        be.setCuratedMaterials(beMaterials);
        roleStrategies.put("backend-developer", be);

        // 3. Software Development Engineer (SDE / Core DSA)
        RoleStudyStrategyDTO sde = new RoleStudyStrategyDTO();
        sde.setId("software-engineer");
        sde.setRoleTitle("Software Development Engineer (SDE / DSA Focused)");
        sde.setCategory("Core Software Engineering");
        sde.setIcon("Code2");
        sde.setExperienceLevel("Fresher to Senior");
        sde.setOverview("SDE roles at product-based companies prioritize data structures, algorithmic complexity optimization, modular code design, and scalable architecture.");
        sde.setCoreCompetencies(List.of("Data Structures & Algorithms", "Time & Space Complexity Analysis", "Object-Oriented Design (SOLID)", "Low-Level & High-Level System Design", "Clean Code & Refactoring"));
        sde.setCoreTopicsToMaster(List.of("Two Pointers & Sliding Window", "Fast & Slow Pointers", "Trees, BST & Trie", "Graphs (Dijkstra, Topological Sort)", "Dynamic Programming (0/1 Knapsack, LCS)", "Heaps & Priority Queues"));

        List<Map<String, Object>> sdeRoadmap = new ArrayList<>();
        sdeRoadmap.add(Map.of("stage", "Phase 1", "title", "Linear Data Structures & Sorting", "description", "Master Arrays, Strings, LinkedLists, Stacks, and Queues with optimal pointer techniques.", "topics", List.of("Two Pointers", "Sliding Window", "Monotonic Stack", "Binary Search Patterns")));
        sdeRoadmap.add(Map.of("stage", "Phase 2", "title", "Trees, Graphs & Recursion", "description", "Implement all tree traversals, shortest path algorithms, cycle detection, and tries.", "topics", List.of("Tree DFS & BFS", "Trie Prefix Tree", "Dijkstra & Prim", "Union-Find Disjoint Set")));
        sdeRoadmap.add(Map.of("stage", "Phase 3", "title", "Dynamic Programming & Backtracking", "description", "Deconstruct recurring subproblems, memoization tables, and state transitions.", "topics", List.of("Subsequence & Substring DP", "Knapsack Variations", "Bitmask DP", "N-Queens & Sudoku Solver")));
        sdeRoadmap.add(Map.of("stage", "Phase 4", "title", "System Design & Interview Simulation", "description", "Design scalable distributed systems and practice timed live coding interviews.", "topics", List.of("NeetCode 150 Blind List", "Low-Level Design Patterns", "Scalability & Caching", "STAR Behavioral Stories")));
        sde.setLearningRoadmap(sdeRoadmap);

        List<RoundInfoDTO> sdeRounds = new ArrayList<>();
        sdeRounds.add(new RoundInfoDTO(1, "Coding Assessment (OA)", "70-90 mins", "2-3 algorithmic problems on platforms like HackerRank, CodeSignal, or Codility.", List.of("Write brute force first if stuck, then optimize", "Double check edge cases before submission")));
        sdeRounds.add(new RoundInfoDTO(2, "DSA Technical Rounds (2-3 Rounds)", "45-60 mins each", "Live whiteboarding or collaborative code editor with a senior engineer.", List.of("Never jump into code immediately", "Discuss space/time complexity and alternative approaches")));
        sde.setStandardInterviewRounds(sdeRounds);

        List<InterviewQuestionItemDTO> sdeQuestions = new ArrayList<>();
        sdeQuestions.add(new InterviewQuestionItemDTO("Trapping Rain Water", "Hard", "Two Pointers / Monotonic Stack", "Very High", "Use two pointers moving inward tracking maxLeft and maxRight to accumulate trapped water in O(N) time and O(1) space.", "https://leetcode.com/problems/trapping-rain-water/", List.of("O(1) space optimization", "Elevation boundaries")));
        sdeQuestions.add(new InterviewQuestionItemDTO("Merge K Sorted Lists", "Hard", "Heaps & Divide and Conquer", "Very High", "Insert the head of each list into a Min-Heap; pop min and insert next node in O(N log K) time.", "https://leetcode.com/problems/merge-k-sorted-lists/", List.of("PriorityQueue comparator", "O(N log K) time complexity")));
        sde.setTopInterviewQuestions(sdeQuestions);

        List<StudyMaterialItemDTO> sdeMaterials = new ArrayList<>();
        sdeMaterials.add(new StudyMaterialItemDTO("NeetCode 150 Roadmaps", "Practice Platform", "https://neetcode.io/practice", "Structured categorized coding list with video explanations", "Essential"));
        sdeMaterials.add(new StudyMaterialItemDTO("Striver's SDE Sheet", "Practice Platform", "https://takeuforward.org/interviews/strivers-sde-sheet-top-coding-interview-problems/", "Most recommended coding sheet for software engineering placements", "Free"));
        sde.setCuratedMaterials(sdeMaterials);
        roleStrategies.put("software-engineer", sde);

        // 4. Data Scientist & AI / ML Engineer
        RoleStudyStrategyDTO ds = new RoleStudyStrategyDTO();
        ds.setId("data-scientist-ai");
        ds.setRoleTitle("Data Scientist & AI / ML Engineer");
        ds.setCategory("Data & Artificial Intelligence");
        ds.setIcon("Brain");
        ds.setExperienceLevel("Fresher to Senior");
        ds.setOverview("Data Scientists and AI Engineers build predictive models, machine learning pipelines, and generative AI solutions using Python, PyTorch, Scikit-learn, and vector databases.");
        ds.setCoreCompetencies(List.of("Python, NumPy, Pandas, Scikit-learn", "Statistical Analysis & Probability", "Supervised & Unsupervised Machine Learning", "Deep Learning (PyTorch, TensorFlow)", "Generative AI, LLMs & RAG Architectures", "MLOps & Model Deployment"));
        ds.setCoreTopicsToMaster(List.of("Bias-Variance Tradeoff", "Regularization (L1/L2, Dropout)", "Gradient Descent & Optimizers (Adam, SGD)", "Transformer Architecture & Self-Attention", "Vector Search & Retrieval Augmented Generation (RAG)", "Evaluation Metrics (ROC-AUC, F1-Score, RMSE)"));

        List<Map<String, Object>> dsRoadmap = new ArrayList<>();
        dsRoadmap.add(Map.of("stage", "Phase 1", "title", "Mathematics & Python Foundations", "description", "Linear algebra, multivariable calculus, probability distributions, hypothesis testing, and vectorized computation.", "topics", List.of("Matrix Operations & Eigenvectors", "Bayes Theorem & Distributions", "NumPy & Pandas Manipulation", "Data Cleaning & Imputation")));
        dsRoadmap.add(Map.of("stage", "Phase 2", "title", "Classical Machine Learning", "description", "Feature engineering, regression, tree-based models, ensemble methods, and clustering.", "topics", List.of("Linear & Logistic Regression", "Random Forest & XGBoost", "K-Means & DBSCAN Clustering", "Hyperparameter Tuning with GridSearch")));
        dsRoadmap.add(Map.of("stage", "Phase 3", "title", "Deep Learning & Transformers", "description", "Neural network backpropagation, convolutional networks, and self-attention transformers.", "topics", List.of("PyTorch Neural Networks", "CNNs for Computer Vision", "Transformers & Hugging Face", "Transfer Learning & Fine-Tuning")));
        dsRoadmap.add(Map.of("stage", "Phase 4", "title", "Generative AI & MLOps", "description", "Build production LLM applications with LangChain, vector databases, and containerized serving APIs.", "topics", List.of("RAG Architecture & Embeddings", "Vector Databases (Pinecone, Chroma)", "FastAPI Model Serving", "Dockerizing ML Pipelines")));
        ds.setLearningRoadmap(dsRoadmap);

        List<RoundInfoDTO> dsRounds = new ArrayList<>();
        dsRounds.add(new RoundInfoDTO(1, "Coding & Data Manipulation Assessment", "60 mins", "Data analysis questions using Pandas, NumPy, and basic algorithmic problem solving.", List.of("Be comfortable manipulating dataframes and handling missing values")));
        dsRounds.add(new RoundInfoDTO(2, "Machine Learning Theory & Modeling Deep Dive", "60 mins", "Mathematical breakdown of algorithms, loss functions, overfitting mitigation, and metric selection.", List.of("Explain why you chose a specific metric (e.g. PR-AUC for imbalanced fraud data)")));
        dsRounds.add(new RoundInfoDTO(3, "ML System Design / GenAI Case Study", "60 mins", "Design end-to-end ML solutions (e.g. Recommendation Engine, RAG Document Search, Fraud Detector).", List.of("Cover data ingestion, feature store, model training, latency constraints, and drift monitoring")));
        ds.setStandardInterviewRounds(dsRounds);

        List<InterviewQuestionItemDTO> dsQuestions = new ArrayList<>();
        dsQuestions.add(new InterviewQuestionItemDTO("Explain the Self-Attention Mechanism in Transformers", "Medium-Hard", "Deep Learning & GenAI", "Very High", "Self-attention computes dot products of Query and Key vectors, scaled by sqrt(d_k), passed through softmax, and multiplied by Value vectors.", null, List.of("Q, K, V mathematical formulation", "Scaling factor reason (prevent vanishing gradients)", "Multi-head attention purpose")));
        dsQuestions.add(new InterviewQuestionItemDTO("How do you handle severely imbalanced datasets (e.g. 99.5% negative, 0.5% positive)?", "Medium", "Machine Learning Practice", "Very High", "Use SMOTE / downsampling, Focal Loss or class weighting, and evaluate with Precision-Recall AUC instead of Accuracy.", null, List.of("Why accuracy is misleading", "Cost-sensitive learning", "Threshold calibration")));
        ds.setTopInterviewQuestions(dsQuestions);

        List<StudyMaterialItemDTO> dsMaterials = new ArrayList<>();
        dsMaterials.add(new StudyMaterialItemDTO("Fast.ai Practical Deep Learning", "Video Course", "https://course.fast.ai/", "Renowned hands-on course teaching modern deep learning from top-down", "Free"));
        dsMaterials.add(new StudyMaterialItemDTO("Hugging Face NLP & LLM Course", "Documentation", "https://huggingface.co/learn", "Comprehensive official tutorial on fine-tuning transformers and deploying LLMs", "Free"));
        ds.setCuratedMaterials(dsMaterials);
        roleStrategies.put("data-scientist-ai", ds);
    }

    private String normalizeKey(String input) {
        if (input == null) return "";
        return input.trim().toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }
}
