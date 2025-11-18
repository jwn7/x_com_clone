package com.example.x_com_clone.controller;

import com.example.x_com_clone.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PostService postService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("posts", postService.findAllPosts());
        return "index"; // templates/home.html
    }

    @GetMapping("/mypage")
    public String myPage() {
        return "mypage"; // templates/mypage.html
    }

    @GetMapping("/posts/new")
    public String showCreatePostForm() {
        return "post-create"; // templates/post-create.html
    }

    @PostMapping("/posts")
    public String createPost(@RequestParam("content") String content) {
        postService.createPost(content);
        return "redirect:/";
    }

    @GetMapping("/search")
    public String search(@RequestParam("keyword") String keyword, Model model) {
        model.addAttribute("posts", postService.searchPosts(keyword));
        model.addAttribute("keyword", keyword);
        return "index";
    }
}
