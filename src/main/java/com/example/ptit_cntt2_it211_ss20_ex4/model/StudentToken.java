package com.example.ptit_cntt2_it211_ss20_ex4.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_string", nullable = false, length = 1000)
    private String tokenString;

    @Column(name = "is_revoked")
    private Boolean isRevoked;

    @Column(name = "is_expired")
    private Boolean isExpired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
}
