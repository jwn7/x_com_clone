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
        model.addAttribute("signupRequest", new UserSignupRequest());
        return "signup";
    }

    // --- 2. 회원가입 데이터 처리 (POST /users/signup) ---
    @PostMapping("/signup")
    public String signup(@ModelAttribute("signupRequest") UserSignupRequest request, Model model) {
        try {
            // 💡 수정된 부분: DTO 객체 전체를 서비스로 전달
            userService.signup(request);

            return "redirect:/";

        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "signup";
        }
    }

    // 임시 성공 페이지 렌더링
    @GetMapping("/login-success")
    public String successPage() {
        return "success";
    }
}