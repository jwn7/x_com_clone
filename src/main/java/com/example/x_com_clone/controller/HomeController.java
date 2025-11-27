package com.example.x_com_clone.controller;

import com.example.x_com_clone.domain.User;
import com.example.x_com_clone.dto.TimelineItemDto; // DTO import
import com.example.x_com_clone.service.TimelineService; // 📌 TimelineService import
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    // private final PostService postService; // ❌ PostService 대신
    private final TimelineService timelineService; // 📌 TimelineService 주입

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

        // 2. 타임라인 목록 로딩 (Post와 Retweet 통합)
        List<TimelineItemDto> timelineItems = timelineService.getGlobalTimeline();

        // 3. Model에 "timelineItems"로 추가 (index.html에서 사용하는 변수명)
        // posts가 아니라 timelineItems를 사용해야 index.html 오류 해결
        model.addAttribute("timelineItems", timelineItems);

        return "index";
    }

}