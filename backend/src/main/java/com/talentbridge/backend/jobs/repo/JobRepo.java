package com.talentbridge.backend.jobs.repo;

import com.talentbridge.backend.jobs.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JobRepo extends JpaRepository<Job, Long> {
    List<Job> findByRecruiter_RecruiterId(Long recruiterId);
    List<Job> findByJobTitleContainingIgnoreCase(String keyword);
    List<Job> findByPlatformIgnoreCase(String platform);
    List<Job> findByIsExternal(Boolean isExternal);
    
    Optional<Job> findByExternalId(String externalId);
    boolean existsByExternalId(String externalId);
    boolean existsByApplyUrl(String applyUrl);

    @Query("SELECT j FROM Job j WHERE j.postedAt >= :since ORDER BY j.postedAt DESC")
    List<Job> findRecentJobs(@Param("since") LocalDateTime since);

    @Query("SELECT j FROM Job j WHERE LOWER(j.platform) = LOWER(:platform) AND j.postedAt >= :since ORDER BY j.postedAt DESC")
    List<Job> findByPlatformAndRecent(@Param("platform") String platform, @Param("since") LocalDateTime since);
}
