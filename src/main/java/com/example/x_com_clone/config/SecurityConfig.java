package com.example.x_com_clone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 보호 비활성화: 개발 환경에서 폼 제출 등의 편의를 위해 비활성화합니다.
                .csrf(AbstractHttpConfigurer::disable)

                // 2. 폼 로그인 및 HTTP 기본 인증 비활성화: Spring Security의 기본 로그인 페이지를 사용하지 않습니다.
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 3. 접근 권한 설정 (핵심: 모든 접근 허용)
                .authorizeHttpRequests(authorize -> authorize
                        // 💡 [핵심] 모든 요청 경로에 대해 인증 없이 접근을 허용합니다. (permitAll)
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    /* * 주의: 이 코드는 보안을 완전히 해제하므로,
     * 실제 서비스 배포 시에는 반드시 제거하고 적절한 인증/인가 설정을 추가해야 합니다.
     */
}