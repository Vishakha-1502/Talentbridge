package com.talentbridge.backend.jobs.service;

import com.talentbridge.backend.jobs.model.Job;
import com.talentbridge.backend.jobs.repo.JobRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobService {

    @Autowired
    private JobRepo jobRepo;

    @Autowired
    private ExternalJobService externalJobService;

    // Create or update job
    public Job saveJob(Job job) {
        return jobRepo.save(job);
    }

    // Get all jobs (internal + synced external)
    public List<Job> getAllJobs() {
        List<Job> list = jobRepo.findAll(Sort.by(Sort.Direction.DESC, "jobId"));
        if (list.isEmpty()) {
            externalJobService.syncExternalJobs();
            list = jobRepo.findAll(Sort.by(Sort.Direction.DESC, "jobId"));
        }
        return list;
    }

    // Get job by ID
    public Optional<Job> getJobById(Long id) {
        return jobRepo.findById(id);
    }

    // Get jobs by recruiter ID
    public List<Job> getJobsByRecruiterId(Long recruiterId) {
        return jobRepo.findByRecruiter_RecruiterId(recruiterId);
    }

    // Search jobs by title
    public List<Job> searchJobs(String keyword) {
        return jobRepo.findByJobTitleContainingIgnoreCase(keyword);
    }

    // Filter external jobs by platform and past days
    public List<Job> getFilteredJobs(String platform, Integer days, String keyword) {
        List<Job> list = getAllJobs();

        if (platform != null && !platform.isBlank() && !platform.equalsIgnoreCase("ALL")) {
            list = list.stream()
                    .filter(j -> j.getPlatform() != null && j.getPlatform().equalsIgnoreCase(platform))
                    .collect(Collectors.toList());
        }

        if (days != null && days > 0) {
            LocalDateTime threshold = LocalDateTime.now().minusDays(days);
            list = list.stream()
                    .filter(j -> {
                        LocalDateTime date = j.getPostedAt() != null ? j.getPostedAt() : j.getCreated_at();
                        return date != null && date.isAfter(threshold);
                    })
                    .collect(Collectors.toList());
        }

        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.toLowerCase();
            list = list.stream()
                    .filter(j -> (j.getJobTitle() != null && j.getJobTitle().toLowerCase().contains(kw))
                            || (j.getCompanyName() != null && j.getCompanyName().toLowerCase().contains(kw))
                            || (j.getDescription() != null && j.getDescription().toLowerCase().contains(kw)))
                    .collect(Collectors.toList());
        }

        return list;
    }

    // Sync external jobs on demand
    public List<Job> syncExternalJobs() {
        return externalJobService.syncExternalJobs();
    }

    // Delete job
    public void deleteJob(Long id) {
        jobRepo.deleteById(id);
    }
}
