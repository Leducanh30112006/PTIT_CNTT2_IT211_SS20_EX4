package com.example.ptit_cntt2_it211_ss20_ex4.repository;

import com.example.ptit_cntt2_it211_ss20_ex4.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(Long studentId);
}
