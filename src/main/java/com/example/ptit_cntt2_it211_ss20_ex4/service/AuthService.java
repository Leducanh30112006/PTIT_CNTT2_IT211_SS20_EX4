package com.example.ptit_cntt2_it211_ss20_ex4.service;

import com.example.ptit_cntt2_it211_ss20_ex4.dto.AuthRequest;
import com.example.ptit_cntt2_it211_ss20_ex4.dto.AuthResponse;
import com.example.ptit_cntt2_it211_ss20_ex4.model.Student;
import com.example.ptit_cntt2_it211_ss20_ex4.model.StudentToken;
import com.example.ptit_cntt2_it211_ss20_ex4.repository.StudentRepository;
import com.example.ptit_cntt2_it211_ss20_ex4.repository.StudentTokenRepository;
import com.example.ptit_cntt2_it211_ss20_ex4.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final StudentRepository repository;
    private final StudentTokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(user, jwtToken);
        saveUserToken(user, refreshToken);

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveUserToken(Student user, String jwtToken) {
        var token = StudentToken.builder()
                .student(user)
                .tokenString(jwtToken)
                .isExpired(false)
                .isRevoked(false)
                .build();
        tokenRepository.save(token);
    }

    public void revokeAllUserTokens(Student user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        List<StudentToken> revokedTokens = validUserTokens.stream()
                .peek(token -> {
                    token.setIsExpired(true);
                    token.setIsRevoked(true);
                })
                .collect(Collectors.toList());
                
        tokenRepository.saveAll(revokedTokens);
    }

    public AuthResponse refreshToken(String refreshToken) {
        final String userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();
            
            var isTokenValid = tokenRepository.findByTokenString(refreshToken)
                    .map(t -> !t.getIsExpired() && !t.getIsRevoked())
                    .orElse(false);

            if (jwtService.isTokenValid(refreshToken, user) && isTokenValid) {
                var accessToken = jwtService.generateToken(user);
                saveUserToken(user, accessToken);
                return AuthResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        }
        throw new RuntimeException("Refresh token is invalid");
    }

    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        String jwt = authHeader.substring(7);
        String userEmail = jwtService.extractUsername(jwt);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail).orElse(null);
            if (user != null) {
                revokeAllUserTokens(user);
                SecurityContextHolder.clearContext();
            }
        }
    }
}
