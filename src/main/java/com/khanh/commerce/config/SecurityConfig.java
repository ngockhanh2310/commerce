package com.khanh.commerce.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    String[] publicPaths = {
            "/api/v1/auth/**",
            "/v3/api-docs.yaml",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    String[] adminPaths = {
            "/api/v1/products/**",
            "/api/v1/categories/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // disable CSRF
                // 2. Cấu hình Session Management thành STATELESS (Phi trạng thái)
                // (Không tạo Session, đúng kiểu JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exceptions ->
                        // Chỉ định cách xử lý khi xác thực thất bại
                        // (ví dụ: chưa đăng nhập)
                        exceptions.authenticationEntryPoint(
                                // Trả về 401 Unauthorized thay vì 403
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                        )
                )
                // 3. Phân quyền cho các HTTP Request
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(publicPaths).permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/orders").hasRole("USER")
                                .requestMatchers(HttpMethod.POST, adminPaths).hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, adminPaths).hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, adminPaths).hasRole("ADMIN")
                                .anyRequest()
                                .authenticated() // ...phải được xác thực
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
