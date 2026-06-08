package com.example.ptit_cntt2_it211_ss20_ex4.repository;

import com.example.ptit_cntt2_it211_ss20_ex4.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
