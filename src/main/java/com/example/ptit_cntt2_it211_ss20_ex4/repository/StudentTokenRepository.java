package com.example.ptit_cntt2_it211_ss20_ex4.repository;

import com.example.ptit_cntt2_it211_ss20_ex4.model.StudentToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentTokenRepository extends JpaRepository<StudentToken, Long> {
    Optional<StudentToken> findByTokenString(String tokenString);

    @Query("SELECT t FROM StudentToken t INNER JOIN Student u ON t.student.id = u.id WHERE u.id = :userId AND (t.isExpired = false OR t.isRevoked = false)")
    List<StudentToken> findAllValidTokenByUser(Long userId);
}
