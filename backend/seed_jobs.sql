-- ==============================================================================
-- TalentBridge Sample Data Seed Script: Recruiter Job Postings
-- ==============================================================================
-- Fields included:
-- 1. job_title
-- 2. description (job description)
-- 3. job_type (Full-time, Part-time, Internship, Contract, Remote)
-- 4. salary (Annual)
-- 5. location
--
-- Note: Skills are also populated in `job_skills` matching the entity skill extractor.
-- ==============================================================================

USE `talentbridge`;

-- Ensure a recruiter exists with ID 1
INSERT INTO `recruiters` (`recruiter_id`, `company_name`, `company_description`, `industry`, `location`, `established_year`, `is_verified`, `created_at`, `updated_at`, `user_id`)
SELECT 1, 'TechNova Solutions', 'Leading provider of enterprise cloud and software services.', 'Information Technology', 'Bangalore, India', 2018, 1, NOW(), NOW(), 3
WHERE NOT EXISTS (SELECT 1 FROM `recruiters` WHERE `recruiter_id` = 1);

-- Insert Sample Job Postings
-- 1. Full-Stack Developer
INSERT INTO `job` (`job_id`, `job_title`, `description`, `job_type`, `salary`, `location`, `recruiter_id`, `created_at`, `updated_at`)
VALUES (
    101,
    'Full Stack Java Developer',
    'We are seeking an experienced Full Stack Developer proficient in Java, Spring Boot, React, and MySQL. Responsibilities include building scalable REST APIs, managing database queries, and integrating frontend components with Git and Docker for CI/CD workflows.',
    'Full-time',
    1200000,
    'Bangalore, India',
    1,
    NOW(),
    NOW()
)
ON DUPLICATE KEY UPDATE
    `job_title` = VALUES(`job_title`),
    `description` = VALUES(`description`),
    `job_type` = VALUES(`job_type`),
    `salary` = VALUES(`salary`),
    `location` = VALUES(`location`),
    `updated_at` = NOW();

-- 2. Frontend Engineer
INSERT INTO `job` (`job_id`, `job_title`, `description`, `job_type`, `salary`, `location`, `recruiter_id`, `created_at`, `updated_at`)
VALUES (
    102,
    'Frontend Engineer (React / TypeScript)',
    'Looking for a passionate Frontend Developer skilled in React, TypeScript, JavaScript, HTML, and CSS. You will build highly responsive web applications, work with modern UI libraries, and collaborate with backend teams consuming Rest API endpoints.',
    'Full-time',
    950000,
    'Pune, India',
    1,
    NOW(),
    NOW()
)
ON DUPLICATE KEY UPDATE
    `job_title` = VALUES(`job_title`),
    `description` = VALUES(`description`),
    `job_type` = VALUES(`job_type`),
    `salary` = VALUES(`salary`),
    `location` = VALUES(`location`),
    `updated_at` = NOW();

-- 3. Python & AI / Data Science Specialist
INSERT INTO `job` (`job_id`, `job_title`, `description`, `job_type`, `salary`, `location`, `recruiter_id`, `created_at`, `updated_at`)
VALUES (
    103,
    'Python AI & Machine Learning Engineer',
    'Exciting opportunity for an AI and Machine Learning engineer. Hands-on experience with Python, Machine Learning, AI, Data Science, Flask, and PostgreSQL required. Experience with cloud deployments on AWS and containerization using Docker is a plus.',
    'Remote',
    1500000,
    'Remote',
    1,
    NOW(),
    NOW()
)
ON DUPLICATE KEY UPDATE
    `job_title` = VALUES(`job_title`),
    `description` = VALUES(`description`),
    `job_type` = VALUES(`job_type`),
    `salary` = VALUES(`salary`),
    `location` = VALUES(`location`),
    `updated_at` = NOW();

-- 4. Cloud DevOps Engineer
INSERT INTO `job` (`job_id`, `job_title`, `description`, `job_type`, `salary`, `location`, `recruiter_id`, `created_at`, `updated_at`)
VALUES (
    104,
    'Cloud DevOps Engineer',
    'Join our infrastructure team to drive automation and scalability. Must have strong skills in DevOps, AWS, Docker, Kubernetes, Linux, and Git. Experience with Agile and Scrum methodologies is highly desirable.',
    'Contract',
    1100000,
    'Hyderabad, India',
    1,
    NOW(),
    NOW()
)
ON DUPLICATE KEY UPDATE
    `job_title` = VALUES(`job_title`),
    `description` = VALUES(`description`),
    `job_type` = VALUES(`job_type`),
    `salary` = VALUES(`salary`),
    `location` = VALUES(`location`),
    `updated_at` = NOW();

-- 5. Backend Node.js Developer
INSERT INTO `job` (`job_id`, `job_title`, `description`, `job_type`, `salary`, `location`, `recruiter_id`, `created_at`, `updated_at`)
VALUES (
    105,
    'Backend Node.js Developer',
    'Seeking a Backend Developer with expertise in Node.js, JavaScript, MongoDB, Redis, and Rest API development. Experience building microservices and working with Git is required.',
    'Part-time',
    600000,
    'Mumbai, India',
    1,
    NOW(),
    NOW()
)
ON DUPLICATE KEY UPDATE
    `job_title` = VALUES(`job_title`),
    `description` = VALUES(`description`),
    `job_type` = VALUES(`job_type`),
    `salary` = VALUES(`salary`),
    `location` = VALUES(`location`),
    `updated_at` = NOW();

-- 6. Software Engineering Intern
INSERT INTO `job` (`job_id`, `job_title`, `description`, `job_type`, `salary`, `location`, `recruiter_id`, `created_at`, `updated_at`)
VALUES (
    106,
    'Software Engineer Intern',
    'Great internship opportunity for fresh graduates! You will learn and work with Java, SQL, MySQL, HTML, CSS, and Git. You will contribute to real-world agile sprint projects.',
    'Internship',
    350000,
    'Pune, India',
    1,
    NOW(),
    NOW()
)
ON DUPLICATE KEY UPDATE
    `job_title` = VALUES(`job_title`),
    `description` = VALUES(`description`),
    `job_type` = VALUES(`job_type`),
    `salary` = VALUES(`salary`),
    `location` = VALUES(`location`),
    `updated_at` = NOW();

-- Seed Job Skills for Matching and Search Engine
DELETE FROM `job_skills` WHERE `job_job_id` IN (101, 102, 103, 104, 105, 106);

-- Skills for Job 101 (Full Stack Java Developer)
INSERT INTO `job_skills` (`job_job_id`, `skills`) VALUES
(101, 'Java'),
(101, 'Spring Boot'),
(101, 'React'),
(101, 'MySQL'),
(101, 'Rest API'),
(101, 'Git'),
(101, 'Docker');

-- Skills for Job 102 (Frontend Engineer)
INSERT INTO `job_skills` (`job_job_id`, `skills`) VALUES
(102, 'React'),
(102, 'TypeScript'),
(102, 'JavaScript'),
(102, 'HTML'),
(102, 'CSS'),
(102, 'Rest API');

-- Skills for Job 103 (Python AI / ML)
INSERT INTO `job_skills` (`job_job_id`, `skills`) VALUES
(103, 'Python'),
(103, 'Machine Learning'),
(103, 'AI'),
(103, 'Data Science'),
(103, 'Flask'),
(103, 'PostgreSQL'),
(103, 'AWS'),
(103, 'Docker');

-- Skills for Job 104 (Cloud DevOps)
INSERT INTO `job_skills` (`job_job_id`, `skills`) VALUES
(104, 'DevOps'),
(104, 'AWS'),
(104, 'Docker'),
(104, 'Kubernetes'),
(104, 'Linux'),
(104, 'Git'),
(104, 'Agile'),
(104, 'Scrum');

-- Skills for Job 105 (Backend Node.js)
INSERT INTO `job_skills` (`job_job_id`, `skills`) VALUES
(105, 'Node.js'),
(105, 'JavaScript'),
(105, 'MongoDB'),
(105, 'Redis'),
(105, 'Rest API'),
(105, 'Git');

-- Skills for Job 106 (Software Engineer Intern)
INSERT INTO `job_skills` (`job_job_id`, `skills`) VALUES
(106, 'Java'),
(106, 'SQL'),
(106, 'MySQL'),
(106, 'HTML'),
(106, 'CSS'),
(106, 'Git'),
(106, 'Agile');
