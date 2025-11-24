package com.example.x_com_clone.controller;

import com.example.x_com_clone.domain.Post;
import com.example.x_com_clone.domain.User; // User 도메인 import 필요
import com.example.x_com_clone.service.LikeService;
import com.example.x_com_clone.service.PostService;
import com.example.x_com_clone.service.ReplyService;
import jakarta.servlet.http.HttpSession; // 세션 import
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // 파일 업로드 import
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

    // ... (listPosts 메서드는 기존과 동일) ...

    /**
     * 타임라인(게시글 목록) 화면
     * - /posts
     * - /posts?keyword=검색어
     */
    @GetMapping
    public String listPosts(@RequestParam(required = false) String keyword,
                            Model model, HttpSession session) { // HttpSession 추가

        List<Post> posts = (keyword == null || keyword.isBlank())
                ? postService.findAllPosts()
                : postService.searchPosts(keyword);

        model.addAttribute("posts", posts);

        // ✅ 세션에서 로그인 유저 정보 가져오기
        User currentUser = (User) session.getAttribute("currentUser");
        model.addAttribute("currentUser", currentUser);
        // post list에서도 user.userId를 가져올 때 사용하기 위해 currentUserId 대신 currentUser 객체를 전달합니다.
        // model.addAttribute("currentUserId", currentUser != null ? currentUser.getUserId() : null);

        return "index"; // 템플릿 이름이 index.html이라고 가정합니다.
    }

    // --------------------------------------------------------------------------
    // 📌 새로 추가된 부분: 게시물 생성
    // --------------------------------------------------------------------------
    /**
     * 게시물 생성
     * - POST /posts (단, 파일 처리를 위해 @RestController의 /api/posts 대신 이 경로를 사용할 수 있음)
     * - AJAX가 아닌 전통적인 폼 제출(Post 후 Redirect) 방식으로 처리
     */
    @PostMapping
    public String createPost(@RequestParam String content,
                             @RequestParam(required = false) List<MultipartFile> files,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        User currentUser = (User) session.getAttribute("currentUser");

        // 1. 로그인 확인
        if (currentUser == null) {
            // 로그인 페이지로 리다이렉트하거나 오류 메시지 전달
            redirectAttributes.addFlashAttribute("errorMessage", "게시물을 작성하려면 로그인해야 합니다.");
            return "redirect:/users/login";
        }

        // 2. 내용 또는 파일이 없으면 오류 처리
        if (content.isBlank() && (files == null || files.isEmpty())) {
            redirectAttributes.addFlashAttribute("errorMessage", "내용 또는 이미지를 첨부해주세요.");
            return "redirect:/posts"; // 다시 홈으로
        }

        try {
            // 3. PostService 호출
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

    // --------------------------------------------------------------------------
    // 📌 새로 추가된 부분: 게시물 삭제
    // --------------------------------------------------------------------------
    /**
     * 게시물 삭제 (DELETE는 폼에서 지원 안 되므로 POST로 대체하거나 DELETE+AJAX 사용)
     * - POST /posts/{postId}/delete (URL을 명시적으로 변경하여 충돌 방지)
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


    // ... (toggleLike 메서드는 기존과 동일) ...

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

    // ... (addReply 메서드는 기존과 동일) ...

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