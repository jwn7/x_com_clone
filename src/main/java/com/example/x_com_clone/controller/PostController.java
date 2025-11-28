package com.example.x_com_clone.controller;

import com.example.x_com_clone.domain.User;
import com.example.x_com_clone.dto.TimelineItemDto;
import com.example.x_com_clone.service.LikeService;
import com.example.x_com_clone.service.PostService;
import com.example.x_com_clone.service.ReplyService;
import com.example.x_com_clone.service.TimelineService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final LikeService likeService;
    private final ReplyService replyService;
    private final TimelineService timelineService;

    // 타임라인 (홈)
    @GetMapping
    public String listPosts(@RequestParam(required = false) String keyword,
                            Model model, HttpSession session) {
        List<TimelineItemDto> timelineItems = timelineService.getGlobalTimeline();
        model.addAttribute("timelineItems", timelineItems);
        model.addAttribute("currentUser", session.getAttribute("currentUser"));
        return "index"; // home.html 또는 index.html
    }

    // 게시글 작성
    @PostMapping
    public String createPost(@RequestParam String content,
                             @RequestParam(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/users/login";

        // 내용과 파일 유효성 검사
        boolean hasContent = content != null && !content.isBlank();
        boolean hasMedia = mediaFiles != null && mediaFiles.stream().anyMatch(f -> !f.isEmpty());

        if (!hasContent && !hasMedia) {
            redirectAttributes.addFlashAttribute("errorMessage", "내용 또는 파일이 필요합니다.");
            return "redirect:/posts";
        }

        postService.createPost(currentUser.getUserId(), content, mediaFiles);
        redirectAttributes.addFlashAttribute("successMessage", "게시글 등록됨");

        return "redirect:/posts";
    }

    // 게시글 삭제 (AJAX)
    @PostMapping("/{postId}/delete")
    @ResponseBody
    public ResponseEntity<String> deletePost(@PathVariable Long postId, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            postService.deletePost(postId, currentUser.getUserId());
            return ResponseEntity.ok("게시물이 성공적으로 삭제되었습니다.");

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물을 찾을 수 없습니다.");
        } catch (AccessDeniedException e) {
            // 권한 없음 (403)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시물 삭제 중 오류가 발생했습니다.");
        }
    }

    // 좋아요
    @PostMapping("/{postId}/like")
    public String toggleLike(@PathVariable Long postId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes,
                             @RequestHeader(value = "Referer", required = false) String referer) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/users/login";

        boolean wasLiked = likeService.hasUserLiked(postId, currentUser.getUserId());
        likeService.toggleLike(postId, currentUser.getUserId());

        redirectAttributes.addFlashAttribute("successMessage", wasLiked ? "좋아요 취소" : "좋아요 완료");

        return (referer != null && !referer.isBlank()) ? "redirect:" + referer : "redirect:/posts";
    }

    // 댓글 작성
    @PostMapping("/{postId}/reply")
    public String addReply(@PathVariable Long postId,
                           @RequestParam String content,
                           HttpSession session,
                           RedirectAttributes redirectAttributes,
                           @RequestHeader(value = "Referer", required = false) String referer) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/users/login";

        if (content.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "댓글 입력 필요");
            return referer != null ? "redirect:" + referer : "redirect:/posts";
        }

        try {
            replyService.addReply(postId, currentUser.getUserId(), content);
            redirectAttributes.addFlashAttribute("successMessage", "댓글 작성됨");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "댓글 작성 중 오류가 발생했습니다.");
        }

        return (referer != null && !referer.isBlank()) ? "redirect:" + referer : "redirect:/posts";
    }

    // 리트윗
    @PostMapping("/{postId}/retweet")
    public String toggleRetweet(@PathVariable Long postId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes,
                                @RequestHeader(value = "Referer", required = false) String referer) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/users/login";

        boolean isRetweeted = postService.toggleRetweet(currentUser.getUserId(), postId);
        redirectAttributes.addFlashAttribute("successMessage", isRetweeted ? "리트윗됨" : "리트윗 취소");

        return (referer != null && !referer.isBlank()) ? "redirect:" + referer : "redirect:/posts";
    }
}