package com.example.x_com_clone.controller;

import com.example.x_com_clone.domain.User;
import com.example.x_com_clone.dto.TimelineItemDto;
import com.example.x_com_clone.service.LikeService;
import com.example.x_com_clone.service.PostService;
import com.example.x_com_clone.service.ReplyService;
import com.example.x_com_clone.service.TimelineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final TimelineService timelineService;

    /**
     * 타임라인 (게시글 목록)
     */
    @GetMapping
    public String listPosts(@RequestParam(required = false) String keyword,
                            Model model, HttpSession session) {

        List<TimelineItemDto> timelineItems = timelineService.getGlobalTimeline();
        model.addAttribute("timelineItems", timelineItems);

        model.addAttribute("currentUser", session.getAttribute("currentUser"));

        return "index";
    }

    /**
     * 게시글 작성
     */
    @PostMapping
    public String createPost(@RequestParam String content,
                             @RequestParam(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/users/login";

        boolean hasContent = content != null && !content.isBlank();
        boolean hasMedia = mediaFiles != null &&
                mediaFiles.stream().anyMatch(f -> f.getSize() > 0);

        if (!hasContent && !hasMedia) {
            redirectAttributes.addFlashAttribute("errorMessage", "내용 또는 파일이 필요합니다.");
            return "redirect:/posts";
        }

        postService.createPost(currentUser.getUserId(), content, mediaFiles);
        redirectAttributes.addFlashAttribute("successMessage", "게시글 등록됨");

        return "redirect:/posts";
    }

    /**
     * 게시글 삭제 (AJAX 요청에 대응하여 ResponseEntity 반환)
     */
    @PostMapping("/{postId}/delete")
    @ResponseBody // 응답 본문을 직접 작성 (뷰 템플릿 사용 안 함)
    public ResponseEntity<String> deletePost(@PathVariable Long postId,
                                             HttpSession session) {

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            // 401 Unauthorized (미인증)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            // 서비스 계층 호출
            postService.deletePost(postId, currentUser.getUserId());

            // 200 OK (성공)
            return ResponseEntity.ok("게시물이 성공적으로 삭제되었습니다.");

        } catch (NoSuchElementException e) {
            // 404 Not Found (게시글 없음)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물을 찾을 수 없습니다.");
        } catch (Exception e) {
            // 500 Internal Server Error (기타 오류)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시물 삭제 중 오류가 발생했습니다.");
        }
    }

    /**
     * 좋아요 토글
     */
    @PostMapping("/{postId}/like")
    public String toggleLike(@PathVariable Long postId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes,
                             @RequestHeader(value = "Referer", required = false) String referer) {

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/users/login";

        boolean wasLiked = likeService.hasUserLiked(postId, currentUser.getUserId());
        likeService.toggleLike(postId, currentUser.getUserId());

        redirectAttributes.addFlashAttribute("successMessage",
                wasLiked ? "좋아요 취소" : "좋아요 완료");

        if (referer != null && !referer.isBlank()) return "redirect:" + referer;
        return "redirect:/posts";
    }

    /**
     * 댓글 작성 (수정됨: IllegalArgumentException 처리 추가)
     */
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
            // 서비스 호출
            // postId와 currentUserId를 인자로 넘김 (ReplyService에서 매개변수 순서: postId, userId)
            replyService.addReply(postId, currentUser.getUserId(), content);
            redirectAttributes.addFlashAttribute("successMessage", "댓글 작성됨");

        } catch (IllegalArgumentException e) {
            // ReplyService에서 Post나 User를 찾지 못했을 때 던지는 예외를 캐치
            // e.getMessage()를 사용할 수도 있지만, 사용자 친화적인 메시지를 직접 제공합니다.
            if (e.getMessage().contains("Post not found")) {
                redirectAttributes.addFlashAttribute("errorMessage", "댓글을 달 게시글을 찾을 수 없습니다. (ID: " + postId + ")");
            } else if (e.getMessage().contains("User not found")) {
                redirectAttributes.addFlashAttribute("errorMessage", "사용자 정보를 찾을 수 없습니다. 다시 로그인해 주세요.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "댓글 작성 중 오류가 발생했습니다.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "댓글 작성 중 알 수 없는 오류가 발생했습니다.");
        }

        if (referer != null && !referer.isBlank()) return "redirect:" + referer;
        return "redirect:/posts";
    }

    /**
     * 리트윗 토글
     */
    @PostMapping("/{postId}/retweet")
    public String toggleRetweet(@PathVariable Long postId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes,
                                @RequestHeader(value = "Referer", required = false) String referer) {

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/users/login";

        boolean isRetweeted = postService.toggleRetweet(currentUser.getUserId(), postId);
        redirectAttributes.addFlashAttribute("successMessage",
                isRetweeted ? "리트윗됨" : "리트윗 취소");

        if (referer != null && !referer.isBlank()) return "redirect:" + referer;
        return "redirect:/posts";
    }
}