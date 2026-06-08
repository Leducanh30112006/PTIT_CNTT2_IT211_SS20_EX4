package com.example.ptit_cntt2_it211_ss20_ex4.repository;

import com.example.ptit_cntt2_it211_ss20_ex4.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
}
