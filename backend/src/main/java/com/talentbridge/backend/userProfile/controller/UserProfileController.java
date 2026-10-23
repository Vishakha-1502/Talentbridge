package com.talentbridge.backend.userProfile.controller;

import com.talentbridge.backend.userProfile.model.UserProfileModel;
import com.talentbridge.backend.userProfile.service.UserProfileService;

import com.talentbridge.backend.userProfile.dto.ParsedResumeDTO;
import com.talentbridge.backend.userProfile.service.ResumeParsingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/userProfile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ResumeParsingService resumeParsingService;

    // ✅ Parse Resume (AI with Local NLP fallback)
    @PostMapping("/parse-resume")
    public ResponseEntity<ParsedResumeDTO> parseResume(@RequestParam("file") MultipartFile file) {
        ParsedResumeDTO parsedData = resumeParsingService.parseResume(file);
        return ResponseEntity.ok(parsedData);
    }

    // ✅ Create profile for user
    @PostMapping("/create/{userId}")
    public UserProfileModel createProfile(@PathVariable Long userId, @RequestBody UserProfileModel profileData) {
        return userProfileService.createProfile(userId, profileData);
    }

    @GetMapping("/user/{userId}")
    public UserProfileModel getProfileByUserId(@PathVariable Long userId) {
        return userProfileService.getProfileByUserId(userId);
    }

    // ✅ Get all profiles
    @GetMapping("/all")
    public List<UserProfileModel> getAllProfiles() {
        return userProfileService.getAllProfiles();
    }

    // ✅ Get profile by ID
    @GetMapping("/{id}")
    public UserProfileModel getProfileById(@PathVariable Long id) {
        return userProfileService.getProfileById(id);
    }

    // ✅ Update profile
    @PutMapping("/update/{id}")
    public UserProfileModel updateProfile(@PathVariable Long id, @RequestBody UserProfileModel updatedProfile) {
        return userProfileService.updateProfile(id, updatedProfile);
    }

    // ✅ Delete profile
    @DeleteMapping("/delete/{id}")
    public String deleteProfile(@PathVariable Long id) {
        userProfileService.deleteProfile(id);
        return "Profile with ID " + id + " deleted successfully.";
    }
}
