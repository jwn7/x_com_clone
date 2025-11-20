package com.example.x_com_clone.controller;

import com.example.x_com_clone.domain.User; // 💡 User 엔티티 import 필요
import jakarta.servlet.http.HttpSession; // 💡 HttpSession import 필요
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class HomeController {


    // --- 💡 메인 페이지: 로그인 상태 확인 로직 추가 ---
    @GetMapping("/")
    public String home(Model model, HttpSession session) { // 💡 HttpSession 추가

        // 1. 세션에서 현재 로그인된 사용자 정보(User 객체)를 가져옵니다.
        User user = (User) session.getAttribute("currentUser");

        // 2. 사용자 정보가 있으면 모델에 추가합니다. (index.html에서 ${currentUser}로 사용)
        if (user != null) {
            model.addAttribute("currentUser", user);
        }

        // 기존 포스트 목록 로직 유지

        return "index"; // templates/index.html로 가정
    }

    // --- 💡 마이페이지: 로그인 상태 확인 로직 추가 (추천) ---
    // 마이페이지는 보통 로그인된 사용자만 접근 가능해야 합니다.
    @GetMapping("/mypage")
    public String myPage(Model model, HttpSession session) { // 💡 HttpSession 추가
        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            // 로그인되어 있지 않다면 로그인 페이지로 리다이렉트
            return "redirect:/users/login";
        }

        model.addAttribute("currentUser", user);
        // postService.findMyPosts(user.getUserId()) 등의 로직이 추가될 수 있음

        return "mypage"; // templates/mypage.html
    }

    // ... (나머지 메서드: showCreatePostForm, createPost, search는 그대로 유지) ...
}