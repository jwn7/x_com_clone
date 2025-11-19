package com.example.x_com_clone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // 💡 import 추가
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화 (개발 편의를 위해)

                // 💡 폼 로그인 기능 비활성화: 자동으로 생성되는 로그인 폼을 없앱니다.
                .formLogin(AbstractHttpConfigurer::disable)

                // 💡 HTTP 기본 인증도 비활성화 (브라우저 팝업창 방지)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 💡 접근 권한 설정 (홈, 회원가입은 누구나 접근 가능하도록 허용)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/users/signup", "/css/**", "/js/**", "/images/**", "/search").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    // 참고: PasswordEncoder Bean은 AppConfig에 이미 있으므로 생략합니다.
}