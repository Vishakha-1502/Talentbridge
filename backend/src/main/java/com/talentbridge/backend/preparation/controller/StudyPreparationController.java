package com.talentbridge.backend.preparation.controller;

import com.talentbridge.backend.preparation.dto.*;
import com.talentbridge.backend.preparation.service.StudyPreparationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prep")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class StudyPreparationController {

    @Autowired
    private StudyPreparationService studyPreparationService;

    @GetMapping("/companies")
    public ResponseEntity<List<CompanyStudyStrategyDTO>> getAllCompanies() {
        return ResponseEntity.ok(studyPreparationService.getAllCompanies());
    }

    @GetMapping("/company/{companyName}")
    public ResponseEntity<CompanyStudyStrategyDTO> getCompanyStrategy(@PathVariable String companyName) {
        return ResponseEntity.ok(studyPreparationService.getCompanyStrategy(companyName));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleStudyStrategyDTO>> getAllRoles() {
        return ResponseEntity.ok(studyPreparationService.getAllRoles());
    }

    @GetMapping("/role/{roleName}")
    public ResponseEntity<RoleStudyStrategyDTO> getRoleStrategy(@PathVariable String roleName) {
        return ResponseEntity.ok(studyPreparationService.getRoleStrategy(roleName));
    }

    @PostMapping("/custom-plan")
    public ResponseEntity<CustomStudyPlanDTO> generateCustomPlan(@RequestBody CustomPlanRequestDTO request) {
        return ResponseEntity.ok(studyPreparationService.generateCustomPlan(request));
    }
}
