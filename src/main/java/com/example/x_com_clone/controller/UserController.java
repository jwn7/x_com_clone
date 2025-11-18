package com.example.x_com_clone.controller;

import com.example.x_com_clone.dto.UserSignupRequest;
import com.example.x_com_clone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller // View(HTML)를 반환하는 컨트롤러
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // --- 1. 회원가입 화면 보여주기 (GET /users/signup) ---
    @GetMapping("/signup")
    public String signupForm(Model model) {
        // Thymeleaf가 폼을 바인딩할 객체를 미리 전달
        model.addAttribute("signupRequest", new UserSignupRequest());
        return "signup"; // templates/signup.html 템플릿 반환
    }

    // --- 2. 회원가입 데이터 처리 (POST /users/signup) ---
    @PostMapping("/signup")
    public String signup(@ModelAttribute("signupRequest") UserSignupRequest request, Model model) {
        try {
            // Service 계층의 비즈니스 로직 호출
            userService.signup(request.getUsername(), request.getEmail(), request.getPassword());

            // 성공 시 리다이렉트
            return "redirect:/users/login-success";

        } catch (IllegalStateException e) {
            // 중복 등의 예외 발생 시 에러 메시지를 모델에 담아 다시 회원가입 페이지로
            model.addAttribute("errorMessage", e.getMessage());
            return "signup";
        }
    }

    // 임시 성공 페이지 렌더링
    @GetMapping("/login-success")
    public String successPage() {
        return "success"; // templates/success.html 템플릿 반환
    }
}