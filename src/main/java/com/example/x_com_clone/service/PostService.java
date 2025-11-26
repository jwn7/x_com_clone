package com.example.x_com_clone.service;

import com.example.x_com_clone.domain.Post;
import com.example.x_com_clone.domain.User;
import com.example.x_com_clone.repository.PostRepository;
import com.example.x_com_clone.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final MediaService mediaService; // MediaService 주입 (가정)

    // --- 1. 조회 및 검색 (기존 유지) ---

    /**
     * 홈 화면에서 전체 게시물 최신순 조회
     */
    public List<Post> findAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * 검색: keyword 포함 글들 최신순
     */
    public List<Post> searchPosts(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAllPosts();
        }
        return postRepository.findByContentContainingIgnoreCaseOrderByCreatedAtDesc(keyword);
    }

    // 📌 추가된 기능: 특정 사용자의 게시물 목록 조회
    /**
     * 특정 사용자가 작성한 게시물 목록을 최신순으로 조회합니다.
     */
    public List<Post> findPostsByUser(User user) {
        // PostRepository에 findByUserOrderByCreatedAtDesc(User user) 메서드가 있다고 가정합니다.
        return postRepository.findByUserOrderByCreatedAtDesc(user);
    }

    // --- 2. 게시물 생성 (Create) ---

    @Transactional
    public Post createPost(Long userId, String content, List<MultipartFile> files) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        Post post = Post.builder()
                .user(user)
                .content(content)
                .build();

        Post savedPost = postRepository.save(post);

        if (files != null && !files.isEmpty()) {
            mediaService.uploadMedia(savedPost, files);
        }

        return savedPost;
    }

    @Transactional
    public Post createPost(Long userId, String content) {
        return createPost(userId, content, null);
    }

    // --- 3. 게시물 삭제 (Delete with Authority Check) ---

    @Transactional
    public void deletePost(Long postId, Long currentUserId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found. id=" + postId));

        Long postAuthorId = post.getUser().getUserId();

        if (!postAuthorId.equals(currentUserId)) {
            throw new IllegalStateException("You do not have permission to delete this post.");
        }

        postRepository.delete(post);
    }
}