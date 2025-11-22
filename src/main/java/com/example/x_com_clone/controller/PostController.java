package com.example.x_com_clone.controller;

import com.example.x_com_clone.domain.Post;
import com.example.x_com_clone.domain.User;
import com.example.x_com_clone.dto.PostCreateRequest;
import com.example.x_com_clone.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // --- 2. 게시물 생성 ---
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostCreateRequest request, HttpSession session) {

        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        }

        // currentUser.getUserId()는 User 엔티티에 postId가 있어야 작동
        Post newPost = postService.createPost(currentUser.getUserId(), request.getContent());

        return ResponseEntity.status(HttpStatus.CREATED).body(newPost); // 201
    }

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