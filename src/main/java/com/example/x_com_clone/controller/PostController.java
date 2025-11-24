package com.example.x_com_clone.controller;

import com.example.x_com_clone.domain.Post;
import com.example.x_com_clone.service.LikeService;
import com.example.x_com_clone.service.PostService;
import com.example.x_com_clone.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final LikeService likeService;
    private final ReplyService replyService;

    /**
     * 타임라인(게시글 목록) 화면
     * - /posts
     * - /posts?keyword=검색어
     */
    @GetMapping
    public String listPosts(@RequestParam(required = false) String keyword,
                            Model model) {

        List<Post> posts = (keyword == null || keyword.isBlank())
                ? postService.findAllPosts()
                : postService.searchPosts(keyword);

        model.addAttribute("posts", posts);

        // ✅ 임시: 로그인 유저 ID (나중에 세션/스프링 시큐리티로 대체)
        Long currentUserId = 1L; // TODO: 로그인 붙이면 실제 값으로 교체
        model.addAttribute("currentUserId", currentUserId);

        // 타임라인 템플릿 이름 (index.html, timeline.html 등으로 바꿔도 됨)
        return "posts/timeline";
    }

    /**
     * 좋아요 토글
     * - POST /posts/{postId}/like
     * - 파라미터: userId (임시로 hidden input에서 전달)
     */
    @PostMapping("/{postId}/like")
    public String toggleLike(@PathVariable Long postId,
                             @RequestParam Long userId,
                             @RequestHeader(value = "Referer", required = false) String referer) {

        likeService.toggleLike(postId, userId);

        // 🔙 원래 보던 페이지로 리다이렉트 (타임라인/상세 어디서 눌러도 원래 페이지로)
        if (referer != null && !referer.isBlank()) {
            return "redirect:" + referer;
        }
        return "redirect:/posts"; // fallback
    }

    /**
     * 댓글 달기
     * - POST /posts/{postId}/reply
     * - 파라미터: userId, content
     */
    @PostMapping("/{postId}/reply")
    public String addReply(@PathVariable Long postId,
                           @RequestParam Long userId,
                           @RequestParam String content,
                           @RequestHeader(value = "Referer", required = false) String referer) {

        // 내용이 비어있으면 그냥 무시하고 돌아가기
        if (content == null || content.isBlank()) {
            if (referer != null && !referer.isBlank()) {
                return "redirect:" + referer;
            }
            return "redirect:/posts";
        }

        replyService.addReply(postId, userId, content);

        if (referer != null && !referer.isBlank()) {
            return "redirect:" + referer;
        }
        return "redirect:/posts";
    }
}
