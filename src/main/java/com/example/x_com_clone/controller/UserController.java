package com.example.x_com_clone.controller;

import com.example.x_com_clone.domain.User;
import com.example.x_com_clone.dto.TimelineItemDto; // 📌 추가: DTO 사용을 위해
import com.example.x_com_clone.dto.UserProfileUpdateRequest;
import com.example.x_com_clone.dto.UserSignupRequest;
import com.example.x_com_clone.service.FollowService;
import com.example.x_com_clone.service.PostService;
import com.example.x_com_clone.service.UserService;
import com.example.x_com_clone.service.TimelineService; // 📌 추가: 타임라인 통합 서비스
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List; // List 사용을 위해 명시적 추가 (safe)

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final PostService postService;
    private final FollowService followService;
    private final TimelineService timelineService; // 📌 추가: 통합 타임라인 로직을 위해

    // =========================
    // 1. 회원가입 (Signup)
    // =========================

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signupRequest", new UserSignupRequest());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(
            @ModelAttribute("signupRequest") UserSignupRequest request,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            userService.signup(request);
            redirectAttributes.addFlashAttribute(
                    "signupSuccessMessage",
                    "회원가입에 성공했습니다! 이제 로그인하여 서비스를 이용해 보세요."
            );
            return "redirect:/";

        } catch (IllegalStateException e) {
            log.warn("회원가입 실패: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "signup";
        }
    }

    // =========================
    // 2. 로그인 & 로그아웃
    // =========================

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            Model model,
            HttpSession session
    ) {
        try {
            User authenticatedUser = userService.authenticate(username, password);
            session.setAttribute("currentUser", authenticatedUser);
            return "redirect:/";

        } catch (IllegalArgumentException e) {
            log.info("로그인 실패 (Username: {}): {}", username, e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // =========================
    // 3. 프로필 조회 (Profile)
    // =========================

    @GetMapping("/profile/{username}")
    public String profile(@PathVariable String username,
                          Model model,
                          HttpSession session) {

        log.info("프로필 페이지 요청: @{}", username);

        // 1) 프로필 대상 유저 조회
        User profileUser;
        try {
            profileUser = userService.findUserByUsername(username);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "사용자를 찾을 수 없습니다: " + username
            );
        }

        // 2) 해당 사용자의 게시글 및 리트윗 목록 조회 (TimelineService 사용)
        List<TimelineItemDto> timelineItems = Collections.emptyList();
        try {
            // 📌 수정: postService 대신 timelineService를 사용하여 원본 게시물과 리트윗을 통합 조회
            timelineItems = timelineService.getTimelineForUser(profileUser);
        } catch (Exception e) {
            log.error("사용자 타임라인 로드 실패 (User: {}): {}", username, e.getMessage());
        }

        // 📌 Model 속성명 변경: posts -> timelineItems (profile.html 변경 사항에 대응)
        model.addAttribute("timelineItems", timelineItems);


        // 3) 로그인 사용자 정보
        User currentUser = (User) session.getAttribute("currentUser");

        boolean isMyProfile = false;
        boolean isFollowing = false;

        if (currentUser != null) {
            isMyProfile = currentUser.getUserId().equals(profileUser.getUserId());

            // 내 프로필이 아닐 때만 팔로우 여부 확인
            if (!isMyProfile) {
                isFollowing = followService.isFollowing(currentUser, profileUser);
            }
        }

        // 4) 팔로워 / 팔로잉 수 + 리스트
        long followerCount = followService.countFollowers(profileUser);
        long followingCount = followService.countFollowing(profileUser);

        model.addAttribute("profileUser", profileUser);
        model.addAttribute("isMyProfile", isMyProfile);
        model.addAttribute("isFollowing", isFollowing);
        model.addAttribute("followerCount", followerCount);
        model.addAttribute("followingCount", followingCount);

        // ✅ 모달에서 사용할 팔로워/팔로잉 목록
        model.addAttribute("followers", followService.getFollowers(profileUser));
        model.addAttribute("following", followService.getFollowing(profileUser));

        return "profile";
    }

    // =========================
    // 4. 팔로우 / 언팔로우
    // =========================

    @PostMapping("/profile/{username}/follow")
    public String follow(@PathVariable String username,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/users/login";
        }

        try {
            User targetUser = userService.findUserByUsername(username);
            // followService.follow(User currentUser, User targetUser) 호출
            followService.follow(currentUser, targetUser);
            redirectAttributes.addFlashAttribute("successMessage", "@" + username + "님을 팔로우했습니다.");

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("팔로우 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/users/profile/" + username;
    }

    @PostMapping("/profile/{username}/unfollow")
    public String unfollow(@PathVariable String username,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/users/login";
        }

        try {
            User targetUser = userService.findUserByUsername(username);
            // followService.unfollow(User currentUser, User targetUser) 호출
            followService.unfollow(currentUser, targetUser);
            redirectAttributes.addFlashAttribute("successMessage", "@" + username + "님을 언팔로우했습니다.");

        } catch (IllegalArgumentException e) {
            log.warn("언팔로우 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/users/profile/" + username;
    }

    // =========================
    // 5. 프로필 수정 (Edit Profile)
    // =========================

    @GetMapping("/profile/edit")
    public String editProfileForm(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            log.warn("비로그인 사용자가 프로필 수정 폼 요청");
            return "redirect:/users/login";
        }

        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                currentUser.getUsername(),
                currentUser.getBio(),
                currentUser.getProfileImageUrl()
        );

        model.addAttribute("profileUpdateRequest", request);
        return "profile_edit";
    }

    @PostMapping("/profile/edit")
    public String editProfile(
            @ModelAttribute("profileUpdateRequest") UserProfileUpdateRequest request,
            @RequestParam(value = "profileImageFile", required = false) MultipartFile profileImageFile,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            log.warn("비로그인 사용자가 프로필 수정 시도");
            return "redirect:/users/login";
        }

        try {
            User updatedUser = userService.updateProfile(
                    currentUser.getUserId(),
                    request,
                    profileImageFile
            );

            // 세션에 최신 정보로 갱신
            session.setAttribute("currentUser", updatedUser);

            redirectAttributes.addFlashAttribute(
                    "signupSuccessMessage",
                    "프로필 정보가 성공적으로 수정되었습니다."
            );

            return "redirect:/users/profile/" + updatedUser.getUsername();

        } catch (IllegalStateException e) {
            log.warn("프로필 수정 실패 (User: {}): {}", currentUser.getUsername(), e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("profileUpdateRequest", request);
            return "profile_edit";
        } catch (Exception e) {
            log.error("프로필 수정 중 일반 오류 발생: {}", e.getMessage());
            model.addAttribute("errorMessage", "프로필 수정 중 오류가 발생했습니다.");
            model.addAttribute("profileUpdateRequest", request);
            return "profile_edit";
        }
    }
}