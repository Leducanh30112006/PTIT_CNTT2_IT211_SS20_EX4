package com.example.ptit_cntt2_it211_ss20_ex4.controller;

import com.example.ptit_cntt2_it211_ss20_ex4.dto.ClaimCertificateResponse;
import com.example.ptit_cntt2_it211_ss20_ex4.dto.ProgressResponse;
import com.example.ptit_cntt2_it211_ss20_ex4.model.Student;
import com.example.ptit_cntt2_it211_ss20_ex4.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/elearning/study")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @GetMapping("/my-progress")
    public ResponseEntity<ProgressResponse> getMyProgress(@AuthenticationPrincipal Student student) {
        return ResponseEntity.ok(studyService.getMyProgress(student));
    }

    @PostMapping("/{courseId}/claim-certificate")
    public ResponseEntity<ClaimCertificateResponse> claimCertificate(
            @PathVariable Long courseId,
            @AuthenticationPrincipal Student student
    ) {
        return ResponseEntity.ok(studyService.claimCertificate(courseId, student));
    }
}
