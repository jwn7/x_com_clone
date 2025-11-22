package com.example.x_com_clone.controller;

import com.example.x_com_clone.domain.Post;
import com.example.x_com_clone.domain.User; // ⚠️ User 엔티티가 있어야 작동
import com.example.x_com_clone.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PostService postService;

    /**
     * 메인 페이지 (index.html) 렌더링
     */
    @GetMapping("/")
    public String home(Model model, HttpSession session) {

        // 1. 로그인 상태 확인 및 Model 추가
        User user = (User) session.getAttribute("currentUser");
        if (user != null) {
            model.addAttribute("currentUser", user);
        }

        // 2. 포스트 목록 로딩 및 NULL 방지 처리 (posts.isEmpty() 오류 해결)
        List<Post> posts = postService.findAllPosts();

        if (posts == null) {
            posts = Collections.emptyList();
        }

        // 3. Model에 "posts"로 추가
        model.addAttribute("posts", posts);

        return "index";
    }

}