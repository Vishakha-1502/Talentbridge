-- Clear any previous external jobs if any to avoid duplicates
DELETE FROM talentbridge.job_skills WHERE job_job_id IN (SELECT job_id FROM talentbridge.job WHERE is_external = 1);
DELETE FROM talentbridge.job WHERE is_external = 1;

-- 1. LINKEDIN: Google - Software Engineer
INSERT INTO talentbridge.job (job_title, company_name, location, job_type, salary, description, platform, apply_url, is_external, external_id, created_at, updated_at, posted_at, recruiter_id)
VALUES (
    'Software Engineer - Cloud & Distributed Systems',
    'Google',
    'Bengaluru, Karnataka, India',
    'Full-time',
    2800000.0,
    'Google is looking for a Software Engineer to design, develop, test, and deploy large-scale software solutions. Requirements: BS/MS in Computer Science, 2+ years experience in Java, Python, C++, Go, distributed systems, microservices architecture, Docker, and Kubernetes. Strong knowledge of data structures, algorithms, and cloud computing.',
    'LINKEDIN',
    'https://www.linkedin.com/jobs/view/software-engineer-at-google-3891001',
    1,
    'ext-li-001',
    NOW() - INTERVAL 1 DAY,
    NOW() - INTERVAL 1 DAY,
    NOW() - INTERVAL 1 DAY,
    NULL
);
SET @job_id = LAST_INSERT_ID();
INSERT INTO talentbridge.job_skills (job_job_id, skills) VALUES (@job_id, 'Java'), (@job_id, 'Python'), (@job_id, 'Kubernetes'), (@job_id, 'Docker'), (@job_id, 'Microservices');

-- 2. LINKEDIN: Microsoft - Senior Backend Engineer
INSERT INTO talentbridge.job (job_title, company_name, location, job_type, salary, description, platform, apply_url, is_external, external_id, created_at, updated_at, posted_at, recruiter_id)
VALUES (
    'Senior Backend Engineer - Spring Boot & AWS',
    'Microsoft',
    'Hyderabad, Telangana, India',
    'Full-time',
    3200000.0,
    'Microsoft Azure team is hiring a Senior Backend Engineer. You will build highly scalable RESTful services and microservices on Azure. Required: 4+ years of Java, Spring Boot, Azure/AWS, PostgreSQL, Redis caching, and building high-throughput low-latency APIs. Agile development practices.',
    'LINKEDIN',
    'https://www.linkedin.com/jobs/view/senior-software-engineer-at-microsoft-3891002',
    1,
    'ext-li-002',
    NOW() - INTERVAL 2 DAY,
    NOW() - INTERVAL 2 DAY,
    NOW() - INTERVAL 2 DAY,
    NULL
);
SET @job_id = LAST_INSERT_ID();
INSERT INTO talentbridge.job_skills (job_job_id, skills) VALUES (@job_id, 'Java'), (@job_id, 'Spring Boot'), (@job_id, 'AWS'), (@job_id, 'PostgreSQL'), (@job_id, 'Redis');

-- 3. LINKEDIN: Swiggy - Full Stack Developer
INSERT INTO talentbridge.job (job_title, company_name, location, job_type, salary, description, platform, apply_url, is_external, external_id, created_at, updated_at, posted_at, recruiter_id)
VALUES (
    'Full Stack Developer (React & Node.js)',
    'Swiggy',
    'Bengaluru / Remote, India',
    'Full-time',
    2000000.0,
    'Join Swiggy Consumer Experience team! We are seeking an experienced Full Stack Developer skilled in React.js, Node.js, TypeScript, Next.js, and MongoDB. You will architect intuitive web interfaces and high-scale backends serving millions of daily active users.',
    'LINKEDIN',
    'https://www.linkedin.com/jobs/view/full-stack-developer-at-swiggy-3891003',
    1,
    'ext-li-003',
    NOW() - INTERVAL 3 DAY,
    NOW() - INTERVAL 3 DAY,
    NOW() - INTERVAL 3 DAY,
    NULL
);
SET @job_id = LAST_INSERT_ID();
INSERT INTO talentbridge.job_skills (job_job_id, skills) VALUES (@job_id, 'React'), (@job_id, 'Node.js'), (@job_id, 'TypeScript'), (@job_id, 'Next.js'), (@job_id, 'MongoDB');

-- 4. LINKEDIN: Amazon - SDE-2
INSERT INTO talentbridge.job (job_title, company_name, location, job_type, salary, description, platform, apply_url, is_external, external_id, created_at, updated_at, posted_at, recruiter_id)
VALUES (
    'Software Development Engineer II (SDE-2)',
    'Amazon',
    'Bengaluru, Karnataka, India',
    'Full-time',
    3500000.0,
    'Amazon Payments team is seeking an SDE-2 to build next-generation fintech systems. Qualifications: Bachelor degree in CS, 3+ years experience with Java, Spring, AWS, DynamoDB, Kafka, distributed transaction processing, and system design. Experience with high-availability systems required.',
    'LINKEDIN',
    'https://www.linkedin.com/jobs/view/software-development-engineer-ii-at-amazon-3891004',
    1,
    'ext-li-004',
    NOW() - INTERVAL 4 DAY,
    NOW() - INTERVAL 4 DAY,
    NOW() - INTERVAL 4 DAY,
    NULL
);
SET @job_id = LAST_INSERT_ID();
INSERT INTO talentbridge.job_skills (job_job_id, skills) VALUES (@job_id, 'Java'), (@job_id, 'AWS'), (@job_id, 'Spring Boot'), (@job_id, 'SQL'), (@job_id, 'DevOps');

-- 5. LINKEDIN: Zomato - AI/ML Engineer
INSERT INTO talentbridge.job (job_title, company_name, location, job_type, salary, description, platform, apply_url, is_external, external_id, created_at, updated_at, posted_at, recruiter_id)
VALUES (
    'AI/ML Engineer - Generative AI & NLP',
    'Zomato',
    'Gurugram, Haryana, India',
    'Full-time',
    2600000.0,
    'Zomato AI Labs is hiring a Machine Learning Engineer to deploy LLM applications, recommendation engines, and conversational agents. Required: Python, PyTorch, HuggingFace, LangChain, REST API integration, Docker, and experience fine-tuning open source models.',
    'LINKEDIN',
    'https://www.linkedin.com/jobs/view/ai-ml-engineer-at-zomato-3891005',
    1,
    'ext-li-005',
    NOW() - INTERVAL 5 DAY,
    NOW() - INTERVAL 5 DAY,
    NOW() - INTERVAL 5 DAY,
    NULL
);
SET @job_id = LAST_INSERT_ID();
INSERT INTO talentbridge.job_skills (job_job_id, skills) VALUES (@job_id, 'Python'), (@job_id, 'AI'), (@job_id, 'Machine Learning'), (@job_id, 'Docker'), (@job_id, 'Rest API');

-- 6. NAUKRI: TCS - Senior Java Full Stack Developer
INSERT INTO talentbridge.job (job_title, company_name, location, job_type, salary, description, platform, apply_url, is_external, external_id, created_at, updated_at, posted_at, recruiter_id)
VALUES (
    'Senior Java Full Stack Developer (Spring Boot, Angular)',
    'Tata Consultancy Services (TCS)',
    'Pune / Mumbai, Maharashtra, India',
    'Full-time',
    1600000.0,
    'TCS Digital is hiring Senior Java Developers with 3 to 6 years of proven hands-on experience in Core Java 17/21, Spring Boot, Microservices, Hibernate, REST APIs, Angular or React, and MySQL database. Immediate joiners or 30 days notice preferred.',
    'NAUKRI',
    'https://www.naukri.com/job-listings-senior-java-developer-tcs-pune-3892001',
    1,
    'ext-nk-001',
    NOW() - INTERVAL 1 DAY,
    NOW() - INTERVAL 1 DAY,
    NOW() - INTERVAL 1 DAY,
    NULL
);
SET @job_id = LAST_INSERT_ID();
INSERT INTO talentbridge.job_skills (job_job_id, skills) VALUES (@job_id, 'Java'), (@job_id, 'Spring Boot'), (@job_id, 'Angular'), (@job_id, 'MySQL'), (@job_id, 'Rest API');

-- 7. NAUKRI: Flipkart - Lead Frontend Engineer
INSERT INTO talentbridge.job (job_title, company_name, location, job_type, salary, description, platform, apply_url, is_external, external_id, created_at, updated_at, posted_at, recruiter_id)
VALUES (
    'Lead Frontend Engineer (React.js, Redux, Next.js)',
    'Flipkart',
    'Bengaluru, Karnataka, India',
    'Full-time',
    2900000.0,
    'Flipkart is hiring a Lead Frontend Engineer to drive web architecture. Must have 5+ years of experience in React.js, Next.js, Webpack, Tailwind CSS, TypeScript, and web performance optimization. Experience with PWA, responsive design, and large scale e-commerce checkout flows.',
    'NAUKRI',
    'https://www.naukri.com/job-listings-lead-frontend-engineer-flipkart-bangalore-3892002',
    1,
    'ext-nk-002',
    NOW() - INTERVAL 2 DAY,
    NOW() - INTERVAL 2 DAY,
    NOW() - INTERVAL 2 DAY,
    NULL
);
SET @job_id = LAST_INSERT_ID();
INSERT INTO talentbridge.job_skills (job_job_id, skills) VALUES (@job_id, 'React'), (@job_id, 'Next.js'), (@job_id, 'TypeScript'), (@job_id, 'JavaScript'), (@job_id, 'HTML');

-- 8. NAUKRI: Wipro - DevOps & Cloud Engineer
INSERT INTO talentbridge.job (job_title, company_name, location, job_type, salary, description, platform, apply_url, is_external, external_id, created_at, updated_at, posted_at, recruiter_id)
VALUES (
    'DevOps & Cloud Engineer (AWS, Kubernetes, Terraform)',
    'Wipro',
    'Hyderabad, Telangana, India',
    'Full-time',
    1800000.0,
    'Wipro Cloud Solutions is seeking a DevOps Engineer with 3+ years experience. Core competencies: AWS, Docker containerization, Kubernetes orchestration, CI/CD pipeline automation (Jenkins, GitHub Actions), Terraform infrastructure as code, and Linux administration.',
    'NAUKRI',
    'https://www.naukri.com/job-listings-devops-cloud-engineer-wipro-hyderabad-3892003',
    1,
    'ext-nk-003',
    NOW() - INTERVAL 3 DAY,
    NOW() - INTERVAL 3 DAY,
    NOW() - INTERVAL 3 DAY,
    NULL
);
SET @job_id = LAST_INSERT_ID();
INSERT INTO talentbridge.job_skills (job_job_id, skills) VALUES (@job_id, 'DevOps'), (@job_id, 'AWS'), (@job_id, 'Kubernetes'), (@job_id, 'Docker'), (@job_id, 'Linux');

-- 9. NAUKRI: Paytm - Backend Specialist
INSERT INTO talentbridge.job (job_title, company_name, location, job_type, salary, description, platform, apply_url, is_external, external_id, created_at, updated_at, posted_at, recruiter_id)
VALUES (
    'Backend Specialist (Python, Django, FastAPI)',
    'Paytm (One97 Communications)',
    'Noida, Uttar Pradesh, India',
    'Full-time',
    2200000.0,
    'Paytm Lending is looking for Backend Developers proficient in Python, Django, FastAPI, Celery, and PostgreSQL. Responsibilities include developing secure financial transaction APIs, integrating third-party bank gateways, and implementing event-driven architectures with RabbitMQ.',
    'NAUKRI',
    'https://www.naukri.com/job-listings-python-backend-developer-paytm-noida-3892004',
    1,
    'ext-nk-004',
    NOW() - INTERVAL 4 DAY,
    NOW() - INTERVAL 4 DAY,
    NOW() - INTERVAL 4 DAY,
    NULL
);
SET @job_id = LAST_INSERT_ID();
INSERT INTO talentbridge.job_skills (job_job_id, skills) VALUES (@job_id, 'Python'), (@job_id, 'Django'), (@job_id, 'FastAPI'), (@job_id, 'PostgreSQL'), (@job_id, 'Rest API');

-- 10. NAUKRI: Infosys - Java Spring Boot Specialist
INSERT INTO talentbridge.job (job_title, company_name, location, job_type, salary, description, platform, apply_url, is_external, external_id, created_at, updated_at, posted_at, recruiter_id)
VALUES (
    'Java Spring Boot Microservices Specialist',
    'Infosys',
    'Chennai, Tamil Nadu, India',
    'Full-time',
    1500000.0,
    'Infosys Technologies is looking for Java Developers with hands-on expertise in Spring Boot, Spring Security, JPA, Docker, RESTful APIs, and MySQL. You will be part of global banking transformation projects.',
    'NAUKRI',
    'https://www.naukri.com/job-listings-java-spring-boot-infosys-chennai-3892005',
    1,
    'ext-nk-005',
    NOW() - INTERVAL 6 DAY,
    NOW() - INTERVAL 6 DAY,
    NOW() - INTERVAL 6 DAY,
    NULL
);
SET @job_id = LAST_INSERT_ID();
INSERT INTO talentbridge.job_skills (job_job_id, skills) VALUES (@job_id, 'Java'), (@job_id, 'Spring Boot'), (@job_id, 'Rest API'), (@job_id, 'MySQL'), (@job_id, 'Docker');

-- 11. INDEED: Accenture - Application Developer
INSERT INTO talentbridge.job (job_title, company_name, location, job_type, salary, description, platform, apply_url, is_external, external_id, created_at, updated_at, posted_at, recruiter_id)
VALUES (
    'Application Developer - Java Microservices',
    'Accenture',
    'Bengaluru, Karnataka, India',
    'Full-time',
    1750000.0,
    'Accenture Technology is hiring Java Developers for enterprise application modernization. Key skills: Java 17, Spring Boot, Microservices, Spring Cloud, Kafka, SQL, and Agile Scrum methodologies. Competitive benefits and hybrid working model.',
    'INDEED',
    'https://www.indeed.com/viewjob?jk=accenture_java_developer_ind001',
    1,
    'ext-ind-001',
    NOW() - INTERVAL 1 DAY,
    NOW() - INTERVAL 1 DAY,
    NOW() - INTERVAL 1 DAY,
    NULL
);
SET @job_id = LAST_INSERT_ID();
INSERT INTO talentbridge.job_skills (job_job_id, skills) VALUES (@job_id, 'Java'), (@job_id, 'Spring Boot'), (@job_id, 'Microservices'), (@job_id, 'SQL'), (@job_id, 'Agile');

-- 12. INDEED: IBM - Senior Data Engineer
INSERT INTO talentbridge.job (job_title, company_name, location, job_type, salary, description, platform, apply_url, is_external, external_id, created_at, updated_at, posted_at, recruiter_id)
VALUES (
    'Senior Data Engineer (Spark, Python, Big Data)',
    'IBM',
    'Kochi, Kerala, India',
    'Full-time',
    2100000.0,
    'IBM Data & AI team is seeking a Senior Data Engineer. Build end-to-end data pipelines using Apache Spark, Python, Hadoop, PostgreSQL, and AWS S3. Experience with data warehousing, ETL optimization, and Kafka streaming.',
    'INDEED',
    'https://www.indeed.com/viewjob?jk=ibm_senior_data_engineer_ind002',
    1,
    'ext-ind-002',
    NOW() - INTERVAL 3 DAY,
    NOW() - INTERVAL 3 DAY,
    NOW() - INTERVAL 3 DAY,
    NULL
);
SET @job_id = LAST_INSERT_ID();
INSERT INTO talentbridge.job_skills (job_job_id, skills) VALUES (@job_id, 'Python'), (@job_id, 'Data Science'), (@job_id, 'SQL'), (@job_id, 'AWS'), (@job_id, 'Linux');

-- 13. INDEED: Oracle - Database Administrator
INSERT INTO talentbridge.job (job_title, company_name, location, job_type, salary, description, platform, apply_url, is_external, external_id, created_at, updated_at, posted_at, recruiter_id)
VALUES (
    'Database Administrator & Cloud Engineer',
    'Oracle',
    'Bengaluru, Karnataka, India',
    'Full-time',
    2500000.0,
    'Oracle Cloud Infrastructure (OCI) team is hiring a Database Administrator. Proficiency in Oracle DB 19c/21c, MySQL, Linux shell scripting, performance tuning, and cloud migration required.',
    'INDEED',
    'https://www.indeed.com/viewjob?jk=oracle_cloud_database_ind003',
    1,
    'ext-ind-003',
    NOW() - INTERVAL 5 DAY,
    NOW() - INTERVAL 5 DAY,
    NOW() - INTERVAL 5 DAY,
    NULL
);
SET @job_id = LAST_INSERT_ID();
INSERT INTO talentbridge.job_skills (job_job_id, skills) VALUES (@job_id, 'SQL'), (@job_id, 'MySQL'), (@job_id, 'Linux'), (@job_id, 'Cloud'), (@job_id, 'Rest API');

-- 14. GLASSDOOR: Adobe - Full Stack Engineer
INSERT INTO talentbridge.job (job_title, company_name, location, job_type, salary, description, platform, apply_url, is_external, external_id, created_at, updated_at, posted_at, recruiter_id)
VALUES (
    'Full Stack React & Node Engineer',
    'Adobe',
    'Noida / Bengaluru, India',
    'Full-time',
    3100000.0,
    'Adobe Creative Cloud team is looking for a passionate Full Stack Engineer. Experience with React, TypeScript, GraphQL, Node.js, and CSS in JS. Excellent problem-solving skills and user experience focus.',
    'GLASSDOOR',
    'https://www.glassdoor.com/job-listing/full-stack-engineer-adobe-JL_10091',
    1,
    'ext-gd-001',
    NOW() - INTERVAL 2 DAY,
    NOW() - INTERVAL 2 DAY,
    NOW() - INTERVAL 2 DAY,
    NULL
);
SET @job_id = LAST_INSERT_ID();
INSERT INTO talentbridge.job_skills (job_job_id, skills) VALUES (@job_id, 'React'), (@job_id, 'Node.js'), (@job_id, 'TypeScript'), (@job_id, 'HTML'), (@job_id, 'CSS');

-- 15. GLASSDOOR: Uber - Systems Engineer
INSERT INTO talentbridge.job (job_title, company_name, location, job_type, salary, description, platform, apply_url, is_external, external_id, created_at, updated_at, posted_at, recruiter_id)
VALUES (
    'Senior Distributed Systems Engineer',
    'Uber',
    'Bengaluru, Karnataka, India',
    'Full-time',
    3800000.0,
    'Uber Mobility Core team is hiring a Systems Engineer. We process millions of concurrent rides every minute. Required: Go or Java, high-concurrency systems, Redis, Kafka, Cassandra, gRPC, and microservices architecture.',
    'GLASSDOOR',
    'https://www.glassdoor.com/job-listing/systems-engineer-uber-JL_10092',
    1,
    'ext-gd-002',
    NOW() - INTERVAL 3 DAY,
    NOW() - INTERVAL 3 DAY,
    NOW() - INTERVAL 3 DAY,
    NULL
);
SET @job_id = LAST_INSERT_ID();
INSERT INTO talentbridge.job_skills (job_job_id, skills) VALUES (@job_id, 'Java'), (@job_id, 'Redis'), (@job_id, 'Kafka'), (@job_id, 'Microservices'), (@job_id, 'Docker');

-- 16. GLASSDOOR: Salesforce - Platform Developer
INSERT INTO talentbridge.job (job_title, company_name, location, job_type, salary, description, platform, apply_url, is_external, external_id, created_at, updated_at, posted_at, recruiter_id)
VALUES (
    'Cloud Platform Developer (Spring Boot, Kubernetes)',
    'Salesforce',
    'Hyderabad, Telangana, India',
    'Full-time',
    3000000.0,
    'Salesforce is hiring a Platform Developer for building multi-tenant infrastructure. Expertise in Java, Spring Boot, Kubernetes, Docker, and CI/CD pipelines.',
    'GLASSDOOR',
    'https://www.glassdoor.com/job-listing/cloud-platform-developer-salesforce-JL_10093',
    1,
    'ext-gd-003',
    NOW() - INTERVAL 4 DAY,
    NOW() - INTERVAL 4 DAY,
    NOW() - INTERVAL 4 DAY,
    NULL
);
SET @job_id = LAST_INSERT_ID();
INSERT INTO talentbridge.job_skills (job_job_id, skills) VALUES (@job_id, 'Java'), (@job_id, 'Spring Boot'), (@job_id, 'Kubernetes'), (@job_id, 'Docker'), (@job_id, 'Git');
