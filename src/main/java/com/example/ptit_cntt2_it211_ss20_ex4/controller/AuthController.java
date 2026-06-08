package com.example.ptit_cntt2_it211_ss20_ex4.controller;

import com.example.ptit_cntt2_it211_ss20_ex4.dto.AuthRequest;
import com.example.ptit_cntt2_it211_ss20_ex4.dto.AuthResponse;
import com.example.ptit_cntt2_it211_ss20_ex4.dto.RefreshTokenRequest;
import com.example.ptit_cntt2_it211_ss20_ex4.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/elearning/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody AuthRequest request
    ) {
        return ResponseEntity.ok(service.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(service.refreshToken(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        service.logout(authHeader);
        return ResponseEntity.ok().build();
    }
}
