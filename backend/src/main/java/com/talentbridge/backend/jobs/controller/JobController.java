package com.talentbridge.backend.jobs.controller;

import com.talentbridge.backend.jobs.model.Job;
import com.talentbridge.backend.jobs.service.JobService;
import com.talentbridge.backend.recruiter.model.Recruiter;
import com.talentbridge.backend.recruiter.repo.RecruiterRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private RecruiterRepo recruiterRepo;

    // Create job for a recruiter
    @PostMapping("/{recruiterId}")
    public ResponseEntity<Job> createJob(@PathVariable Long recruiterId, @RequestBody Job job) {
        Recruiter recruiter = recruiterRepo.findById(recruiterId)
                .orElseThrow(() -> new RuntimeException("Recruiter not found"));

        job.setRecruiter(recruiter);
        job.setPlatform("TALENT_BRIDGE");
        job.setIsExternal(false);
        Job savedJob = jobService.saveJob(job);
        return ResponseEntity.ok(savedJob);
    }

    // Update job by ID
    @PutMapping("/{jobId}")
    public ResponseEntity<Job> updateJob(@PathVariable Long jobId, @RequestBody Job updatedJob) {
        return jobService.getJobById(jobId)
                .map(existingJob -> {
                    existingJob.setJobTitle(updatedJob.getJobTitle());
                    existingJob.setDescription(updatedJob.getDescription());
                    existingJob.setLocation(updatedJob.getLocation());
                    existingJob.setJob_type(updatedJob.getJob_type());
                    existingJob.setSalary(updatedJob.getSalary());

                    Job savedJob = jobService.saveJob(existingJob);
                    return ResponseEntity.ok(savedJob);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all jobs (includes internal & external postings)
    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    // Filter external / live jobs by platform (LINKEDIN, NAUKRI, INDEED, GLASSDOOR), days (e.g. past 7 days), etc.
    @GetMapping("/filter")
    public ResponseEntity<List<Job>> getFilteredJobs(
            @RequestParam(required = false) String platform,
            @RequestParam(required = false, defaultValue = "7") Integer days,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(jobService.getFilteredJobs(platform, days, keyword));
    }

    // Sync external jobs from LinkedIn, Naukri, Indeed, Glassdoor on demand
    @PostMapping("/sync-external")
    public ResponseEntity<Map<String, Object>> syncExternalJobs() {
        List<Job> added = jobService.syncExternalJobs();
        Map<String, Object> res = new HashMap<>();
        res.put("status", "success");
        res.put("syncedCount", added.size());
        res.put("message", "Successfully synced live and past-week job postings from LinkedIn, Naukri, Indeed, and Glassdoor");
        return ResponseEntity.ok(res);
    }

    // Get job by ID
    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id) {
        return jobService.getJobById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get jobs by recruiter
    @GetMapping("/recruiter/{recruiterId}")
    public ResponseEntity<List<Job>> getJobsByRecruiter(@PathVariable Long recruiterId) {
        return ResponseEntity.ok(jobService.getJobsByRecruiterId(recruiterId));
    }

    // Search jobs by title
    @GetMapping("/search")
    public ResponseEntity<List<Job>> searchJobs(@RequestParam String keyword) {
        return ResponseEntity.ok(jobService.searchJobs(keyword));
    }

    // Delete job
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }
}
