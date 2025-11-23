package com.example.x_com_clone.controller;

import com.example.x_com_clone.domain.Post;
import com.example.x_com_clone.domain.User;
import com.example.x_com_clone.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;       // ⬅ 추가
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // ⬅ 추가

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // --- 1. 조회 및 검색 ---
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.findAllPosts());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Post>> searchPosts(@RequestParam String keyword) {
        return ResponseEntity.ok(postService.searchPosts(keyword));
    }

    // --- 2. 게시물 생성 (텍스트 + 이미지) ---
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Post> createPost(
            @RequestPart("content") String content,                          // 글 내용
            @RequestPart(value = "files", required = false) List<MultipartFile> files, // 이미지 파일들
            HttpSession session
    ) {

        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        }

        Post newPost = postService.createPost(currentUser.getUserId(), content, files);

        return ResponseEntity.status(HttpStatus.CREATED).body(newPost); // 201
    }

    // 🔸 만약 JSON으로만 올리는 기존 방식도 유지하고 싶다면 아래처럼 별도 엔드포인트 둬도 됨
    /*
    @PostMapping(path = "/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Post> createPostJson(@RequestBody PostCreateRequest request, HttpSession session) {

        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        }

        Post newPost = postService.createPost(currentUser.getUserId(), request.getContent());

        return ResponseEntity.status(HttpStatus.CREATED).body(newPost); // 201
    }
    */

    // --- 3. 게시물 삭제 ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, HttpSession session) {

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        }

        try {
            postService.deletePost(id, currentUser.getUserId());
            return ResponseEntity.noContent().build(); // 204

        } catch (IllegalArgumentException e) {
            // Post not found
            return ResponseEntity.notFound().build(); // 404
        } catch (IllegalStateException e) {
            // No permission
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403
        }
    }
}
