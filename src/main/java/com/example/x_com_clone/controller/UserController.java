package com.example.x_com_clone.controller;

import com.example.x_com_clone.domain.User; // 💡 이 import가 누락되었거나 주석 처리되면 오류가 발생합니다.
import com.example.x_com_clone.dto.UserSignupRequest;
import com.example.x_com_clone.service.UserService;
import jakarta.servlet.http.HttpSession;
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
            userService.signup(request);
            // 가입 성공 후 로그인 페이지로 리다이렉트
            return "redirect:/users/login";

        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            // 실패 시 다시 signup.html을 보여줌
            return "signup";
        }
    }

    // --- 3. 로그인 화면 보여주기 (GET /users/login) ---
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    // --- 4. 로그인 데이터 처리 및 세션 저장 (POST /users/login) ---
    @PostMapping("/login")
    public String login(
            @RequestParam String username, // 아이디 또는 이메일
            @RequestParam String password,
            Model model,
            HttpSession session // 세션 객체 주입
    ) {
        try {
            // UserService를 통해 사용자 인증 시도
            User authenticatedUser = userService.authenticate(username, password);

            // 💡 인증 성공 시, User 객체를 "currentUser"라는 이름으로 세션에 저장
            session.setAttribute("currentUser", authenticatedUser);

            // 인증 성공 후 메인 페이지로 리다이렉트
            return "redirect:/";

        } catch (IllegalArgumentException e) {
            // 인증 실패 시 (아이디 없음, 비밀번호 불일치 등)
            model.addAttribute("errorMessage", e.getMessage());

            // 에러 메시지와 함께 다시 login.html을 보여줌
            return "login";
        }
    }

    // --- 5. 로그아웃 기능 (GET /users/logout) ---
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 세션을 완전히 무효화하여 로그아웃 처리
        session.invalidate();

        // 메인 페이지로 리다이렉트
        return "redirect:/";
    }

    // 임시 성공 페이지 렌더링
    @GetMapping("/login-success")
    public String successPage() {
        return "success";
    }
}