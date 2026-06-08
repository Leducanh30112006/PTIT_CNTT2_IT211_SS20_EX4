package com.example.ptit_cntt2_it211_ss20_ex4.security;

import com.example.ptit_cntt2_it211_ss20_ex4.repository.StudentTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final StudentTokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (request.getServletPath().contains("/api/elearning/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            
            // Validate against DB to see if revoked or expired
            // NOTE: Usually we store refresh tokens in DB, but the requirement says:
            // "Filter cần xác thực Access Token từ Header. Bắt buộc phải query vào database để chặn các token thuộc về phiên làm việc đã bị thu hồi"
            // Wait, "đối soát khi làm mới hoặc thu hồi phiên đăng nhập" and "Lưu chuỗi Refresh Token vào bảng StudentToken". 
            // Wait, the requirement says: "Lưu chuỗi Refresh Token vào bảng StudentToken với trạng thái is_revoked = false"
            // "Filter cần xác thực Access Token từ Header. Bắt buộc phải query vào database để chặn các token thuộc về phiên làm việc đã bị thu hồi (is_revoked = true)"
            // How does the filter validate the Access Token if only the Refresh Token is stored in DB?
            // "lặp qua toàn bộ các token còn hiệu lực của sinh viên trong DB và cập nhật cờ is_revoked = true. Xóa Security Context"
            // If the filter needs to check if the session is revoked, maybe it can check if the student has any non-revoked token, or maybe we can store Access Token in the DB too?
            // Let's store both tokens or assume the user has a valid refresh token means session is valid.
            // Requirement says: "Bắt buộc phải query vào database để chặn các token thuộc về phiên làm việc đã bị thu hồi (is_revoked = true)"
            // Maybe we just check if the user has any valid refresh token in DB? Or maybe we save AccessToken as well?
            // "Cấp phát Access Token và Refresh Token... Lưu chuỗi Refresh Token vào bảng StudentToken"
            // Let's check if the Student has at least one valid token?
            // Let's just find the latest valid token or any valid token by student email to consider session active.
            // Wait, let's look closely at requirement 4.
            
            // Requirement: "Các API liên quan đến học tập (/api/elearning/study/) phải được bảo mật. Filter cần xác thực Access Token từ Header. Bắt buộc phải query vào database để chặn các token thuộc về phiên làm việc đã bị thu hồi (is_revoked = true)"
            // Ah, usually we store the access token in `StudentToken` too, but the requirement specifically says:
            // "Lưu chuỗi Refresh Token vào bảng StudentToken" for login.
            // Let me check if I should store both? Or just Access Token?
            // "StudentToken: id, token_string, is_revoked, is_expired, student_id (Bảng này chịu trách nhiệm lưu trữ Refresh Token để đối soát khi làm mới hoặc thu hồi phiên đăng nhập)"
            // Wait, if it only stores Refresh Token, how can we check if the Access Token in header is revoked?
            // Maybe by checking if the student's *Refresh Token* is revoked? But we only have Access Token in the Header.
            // So we might need to store the Access Token as well, or the requirement meant storing the Access Token?
            // Let's assume the requirement means we store the *Access Token* in DB, or it means when we check access token, we find the student and check if they have active sessions, or maybe we just store the Access Token as `StudentToken` as well?
            // Let's store both Access Token and Refresh Token in `StudentToken` to be safe, or just Access Token.
            // Let's re-read: "Lưu chuỗi Refresh Token vào bảng StudentToken... Bảng này chịu trách nhiệm lưu trữ Refresh Token".
            // It explicitly says "Lưu chuỗi Refresh Token". So how can we check access token? 
            // In a typical implementation, when logging out, we revoke the refresh token. And since access token is stateless and short-lived, it's hard to revoke. 
            // But requirement says: "Bắt buộc phải query vào database để chặn các token thuộc về phiên làm việc đã bị thu hồi (is_revoked = true)".
            // If we only have access token in header, and we query DB, the DB must contain the access token to check `is_revoked`. So we MUST store the access token in `StudentToken` too. 
            // I will store the Access Token in the DB when logging in.

            // Let's check if the access token is in the DB and valid.
            var isTokenValid = tokenRepository.findByTokenString(jwt)
                    .map(t -> !t.getIsExpired() && !t.getIsRevoked())
                    .orElse(false);

            if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
