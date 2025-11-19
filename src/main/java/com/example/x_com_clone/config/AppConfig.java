package com.example.x_com_clone.config; // 패키지는 프로젝트 구조에 맞게 설정

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration // 이 클래스가 스프링 설정 파일임을 나타냅니다.
public class AppConfig {

    @Bean // 이 메서드가 반환하는 객체(PasswordEncoder)를 스프링 빈으로 등록합니다.
    public PasswordEncoder passwordEncoder() {
        // 💡 BCrypt는 현재 널리 사용되는 강력한 해시 함수입니다.
        return new BCryptPasswordEncoder();
    }
}