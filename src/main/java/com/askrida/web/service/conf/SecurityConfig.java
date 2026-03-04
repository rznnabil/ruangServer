package com.askrida.web.service.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security Configuration.
 *
 * Strategi: autentikasi tetap ditangani secara manual via HttpSession (PageController),
 * Spring Security digunakan untuk:
 * - BCrypt password encoding (PasswordUtil)
 * - CSRF protection pada form halaman web
 * - Baseline protection (headers, clickjacking, dll)
 *
 * Semua endpoint dibuka di sini; pembatasan akses berdasarkan role
 * ditangani di masing-masing controller via session check.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Matikan CSRF untuk REST API endpoints (API menggunakan JWT/stateless)
            // Form HTML tetap dilindungi CSRF jika diperlukan
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    "/rest/**",
                    "/api/**",
                    "/expense/**",
                    "/project/**",
                    "/property/**",
                    "/language/**",
                    "/music/**",
                    "/chatbot/**"
                )
            )
            // Izinkan semua request — autentikasi ditangani di controller via HttpSession
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            // Nonaktifkan Spring Security login page bawaan
            .formLogin(AbstractHttpConfigurer::disable)
            // Nonaktifkan HTTP Basic Auth bawaan
            .httpBasic(AbstractHttpConfigurer::disable)
            // Security headers: X-Frame-Options, X-Content-Type-Options, dll
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            );

        return http.build();
    }
}
