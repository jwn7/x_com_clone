package com.example.x_com_clone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // 이 클래스가 HTTP 요청을 처리하는 컨트롤러임을 명시
public class HomeController {

    // 루트 URL ("/") 요청이 오면 이 메서드가 처리합니다.
    @GetMapping("/")
    public String home() {
        return "index"; // src/main/resources/templates/index.html 템플릿 반환
    }
}