package com.khanh.commerce.config;

import com.khanh.commerce.service.JwtService;
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

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // LOG 1: Kiểm tra Header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("FILTER: Không tìm thấy Header Authorization hoặc không có Bearer prefix");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        System.out.println("🟡 FILTER: Token nhận được: " + jwt.substring(0, 10) + "..."); // In 10 ký tự đầu

        try {
            username = jwtService.extractUsername(jwt);
            System.out.println("FILTER: Username giải mã được: " + username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                boolean isValid = jwtService.isTokenValid(jwt, userDetails);
                System.out.println("🔵 FILTER: Token Valid? " + isValid);

                if (isValid) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("✅ FILTER: Xác thực thành công cho user: " + username);
                } else {
                    System.out.println("FILTER: Token KHÔNG hợp lệ (Hết hạn hoặc sai chữ ký)");
                }
            }
        } catch (Exception e) {
            System.out.println("FILTER ERROR: Lỗi khi xử lý Token!");
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }
}
