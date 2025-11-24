package com.example.x_com_clone.controller;

import com.example.x_com_clone.domain.User;
import com.example.x_com_clone.dto.UserSignupRequest;
import com.example.x_com_clone.dto.UserProfileUpdateRequest;
import com.example.x_com_clone.service.UserService;
import jakarta.servlet.http.HttpSession;
// import jakarta.validation.Valid; // 📌 제거
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
// import org.springframework.validation.BindingResult; // 📌 제거
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    // --- 1. 회원가입 (Signup) ---

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signupRequest", new UserSignupRequest());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(
            @ModelAttribute("signupRequest") UserSignupRequest request, // ✅ @Valid 제거
            // BindingResult bindingResult, // 📌 제거
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        // 1. DTO 유효성 검사 로직 제거

        try {
            userService.signup(request);
            redirectAttributes.addFlashAttribute("signupSuccessMessage",
                    "회원가입에 성공했습니다! 이제 로그인하여 서비스를 이용해 보세요.");
            return "redirect:/";

        } catch (IllegalStateException e) {
            // 2. 비즈니스 로직(중복 체크) 예외 처리는 유지
            log.warn("회원가입 실패: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "signup";
        }
    }

    // --- 2. 로그인 및 로그아웃 (Login & Logout) ---
    // (이 부분은 수정할 필요가 없습니다.)

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

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // --- 3. 프로필 조회 (Profile) ---
    // (이 부분은 수정할 필요가 없습니다.)

    @GetMapping("/profile/{username}")
    public String profile(@PathVariable String username, Model model) {
        log.info("프로필 페이지 요청: @{}", username);

        User profileUser;
        try {
            profileUser = userService.findUserByUsername(username);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다: " + username);
        }

        model.addAttribute("profileUser", profileUser);
        return "profile";
    }

    // --- 4. 프로필 수정 (Edit Profile) ---

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
            @ModelAttribute("profileUpdateRequest") UserProfileUpdateRequest request, // ✅ @Valid 제거
            // BindingResult bindingResult, // 📌 제거
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

        // 1. DTO 유효성 검사 로직 제거

        try {
            // Service는 여전히 호출
            User updatedUser = userService.updateProfile(currentUser.getUserId(), request, profileImageFile);

            session.setAttribute("currentUser", updatedUser);

            redirectAttributes.addFlashAttribute("signupSuccessMessage", "프로필 정보가 성공적으로 수정되었습니다.");

            return "redirect:/users/profile/" + updatedUser.getUsername();

        } catch (IllegalStateException e) {
            // 2. 비즈니스 로직(중복 체크) 예외 처리는 유지
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