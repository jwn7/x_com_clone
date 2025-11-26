package com.example.x_com_clone.controller;

import com.example.x_com_clone.domain.Post;
import com.example.x_com_clone.domain.User;
import com.example.x_com_clone.service.LikeService;
import com.example.x_com_clone.service.PostService;
import com.example.x_com_clone.service.ReplyService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final LikeService likeService;
    private final ReplyService replyService;

    /**
     * 타임라인(게시글 목록) 화면
     */
    @GetMapping
    public String listPosts(@RequestParam(required = false) String keyword,
                            Model model, HttpSession session) {

        List<Post> posts = (keyword == null || keyword.isBlank())
                ? postService.findAllPosts()
                : postService.searchPosts(keyword);

        model.addAttribute("posts", posts);

        User currentUser = (User) session.getAttribute("currentUser");
        model.addAttribute("currentUser", currentUser);

        return "index"; // 템플릿 이름이 index.html이라고 가정합니다.
    }

    /**
     * 게시물 생성 (POST /posts)
     * - HTML 폼에서 직접 파일을 전송받아 처리합니다.
     */
    @PostMapping
    public String createPost(@RequestParam String content,
                             @RequestParam(value = "files", required = false) List<MultipartFile> files, // files 파라미터명과 일치
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        User currentUser = (User) session.getAttribute("currentUser");

        // 1. 로그인 확인
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "게시물을 작성하려면 로그인해야 합니다.");
            return "redirect:/users/login";
        }

        // 2. 내용 또는 파일이 없으면 오류 처리
        if (content.isBlank() && (files == null || files.isEmpty())) {
            redirectAttributes.addFlashAttribute("errorMessage", "내용 또는 이미지를 첨부해주세요.");
            return "redirect:/posts";
        }

        try {
            // 3. PostService 호출 (게시물 및 파일 저장)
            postService.createPost(currentUser.getUserId(), content, files);

        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "사용자 정보를 찾을 수 없습니다.");
        } catch (Exception e) {
            // 파일 업로드, DB 저장 등 기타 오류
            redirectAttributes.addFlashAttribute("errorMessage", "게시물 작성 중 오류가 발생했습니다: " + e.getMessage());
        }

        // 4. 성공 시 타임라인으로 리다이렉트
        return "redirect:/posts";
    }

    /**
     * 게시물 삭제 (POST /posts/{postId}/delete)
     * - HTML 폼 제출로 처리합니다.
     */
    @PostMapping("/{postId}/delete")
    public String deletePost(@PathVariable Long postId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "삭제 권한이 없습니다. 로그인하세요.");
            return "redirect:/users/login";
        }

        try {
            postService.deletePost(postId, currentUser.getUserId());
            redirectAttributes.addFlashAttribute("successMessage", "게시물이 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "게시물을 찾을 수 없습니다.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "삭제 권한이 없습니다. 본인의 게시물만 삭제할 수 있습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "삭제 중 오류가 발생했습니다.");
        }

        return "redirect:/posts";
    }

    /**
     * 좋아요 토글
     */
    @PostMapping("/{postId}/like")
    public String toggleLike(@PathVariable Long postId,
                             @RequestParam Long userId,
                             @RequestHeader(value = "Referer", required = false) String referer) {

        likeService.toggleLike(postId, userId);

        if (referer != null && !referer.isBlank()) {
            return "redirect:" + referer;
        }
        return "redirect:/posts";
    }

    /**
     * 댓글 달기
     */
    @PostMapping("/{postId}/reply")
    public String addReply(@PathVariable Long postId,
                           @RequestParam Long userId,
                           @RequestParam String content,
                           @RequestHeader(value = "Referer", required = false) String referer) {

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