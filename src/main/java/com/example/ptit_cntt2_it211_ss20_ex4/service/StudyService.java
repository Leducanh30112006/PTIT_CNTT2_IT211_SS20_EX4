package com.example.ptit_cntt2_it211_ss20_ex4.service;

import com.example.ptit_cntt2_it211_ss20_ex4.client.CertificateClient;
import com.example.ptit_cntt2_it211_ss20_ex4.dto.ClaimCertificateResponse;
import com.example.ptit_cntt2_it211_ss20_ex4.dto.ProgressResponse;
import com.example.ptit_cntt2_it211_ss20_ex4.exception.CourseNotCompletedException;
import com.example.ptit_cntt2_it211_ss20_ex4.model.Enrollment;
import com.example.ptit_cntt2_it211_ss20_ex4.model.Student;
import com.example.ptit_cntt2_it211_ss20_ex4.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyService {
    private final EnrollmentRepository enrollmentRepository;
    private final CertificateClient certificateClient;

    public ProgressResponse getMyProgress(Student student) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(student.getId());

        List<String> graduatedCourses = enrollments.stream()
                .filter(e -> e.getLessonsCompleted().equals(e.getCourse().getTotalLessons()))
                .map(e -> e.getCourse().getTitle())
                .collect(Collectors.toList());

        Integer totalCompletedLessons = enrollments.stream()
                .mapToInt(Enrollment::getLessonsCompleted)
                .sum();

        return ProgressResponse.builder()
                .totalCompletedLessons(totalCompletedLessons)
                .graduatedCourses(graduatedCourses)
                .build();
    }

    public ClaimCertificateResponse claimCertificate(Long courseId, Student student) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(student.getId());

        Enrollment targetEnrollment = enrollments.stream()
                .filter(e -> e.getCourse().getId().equals(courseId))
                .findFirst()
                .orElseThrow(() -> new CourseNotCompletedException("Enrollment not found for course " + courseId));

        if (!targetEnrollment.getLessonsCompleted().equals(targetEnrollment.getCourse().getTotalLessons())) {
            throw new CourseNotCompletedException("Course is not 100% completed");
        }

        Map<String, String> payload = Map.of(
                "student_name", student.getFullName(),
                "course_title", targetEnrollment.getCourse().getTitle()
        );

        ClaimCertificateResponse clientResponse = certificateClient.claimCertificate(payload);

        return ClaimCertificateResponse.builder()
                .message("Certificate claimed successfully")
                .certificateUrl(clientResponse.getCertificateUrl())
                .build();
    }
}
