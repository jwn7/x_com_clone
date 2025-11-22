package com.example.x_com_clone.controller;

import com.example.x_com_clone.domain.User;
import com.example.x_com_clone.dto.UserSignupRequest;
import com.example.x_com_clone.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 💡 로깅 추가
import org.springframework.http.HttpStatus; // 💡 HttpStatus import 추가
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException; // 💡 ResponseStatusException import 추가
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j // 💡 로깅 기능 사용
public class UserController {

    private final UserService userService;
    // 💡 필요하다면 PostService 등 다른 서비스도 주입 가능 (예: private final PostService postService;)

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
            redirectAttributes.addFlashAttribute("signupSuccessMessage",
                    "회원가입에 성공했습니다! 이제 로그인하여 서비스를 이용해 보세요.");
            return "redirect:/";

        } catch (IllegalStateException e) {
            log.warn("회원가입 실패: {}", e.getMessage()); // 💡 실패 시 로깅
            model.addAttribute("errorMessage", e.getMessage());
            return "signup";
        }
    }

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
            log.info("로그인 실패 (Username: {}): {}", username, e.getMessage()); // 💡 실패 시 로깅
            model.addAttribute("errorMessage", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    /**
     * 특정 사용자의 프로필 페이지로 이동 (개선됨)
     * URL: /users/profile/{username}
     */
    @GetMapping("/profile/{username}")
    public String profile(@PathVariable String username, Model model) {
        log.info("프로필 페이지 요청: @{}", username); // 💡 요청 로깅

        User profileUser;
        try {
            // 1. 서비스 레이어를 통해 해당 username을 가진 사용자 정보를 조회합니다.
            profileUser = userService.findUserByUsername(username);

        } catch (IllegalArgumentException e) {
            // 💡 사용자를 찾을 수 없는 경우 ResponseStatusException을 던져 404를 반환하게 합니다.
            // 이렇게 하면 깔끔하게 에러 처리가 분리되고 사용자에게 'errorPage' 대신 404 상태를 전달할 수 있습니다.
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다: " + username);
        }

        // 2. 조회된 사용자 정보를 모델에 담아 뷰로 전달합니다.
        model.addAttribute("profileUser", profileUser);

        // 3. (추가 로직 필요) 프로필 페이지에 표시할 해당 사용자의 게시물 목록을 조회합니다.
        // List<Post> userPosts = postService.findPostsByUsername(username);
        // model.addAttribute("userPosts", userPosts);

        // 4. 'profile.html' 템플릿을 반환합니다.
        return "profile";
    }
}