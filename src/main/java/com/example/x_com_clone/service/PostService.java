package com.example.x_com_clone.service;

import com.example.x_com_clone.domain.Post;
import com.example.x_com_clone.domain.Retweet;
import com.example.x_com_clone.domain.User;
import com.example.x_com_clone.repository.PostRepository;
import com.example.x_com_clone.repository.RetweetRepository;
import com.example.x_com_clone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RetweetRepository retweetRepository;

    // 📌 MediaService 주입 (이미지 저장을 위해 필수)
    private final MediaService mediaService;

    // ===================================
    // 1. 게시물 조회 및 검색
    // ===================================

    @Transactional(readOnly = true)
    public List<Post> findAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Post> findPostsByUser(User user) {
        return postRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Transactional(readOnly = true)
    public List<Post> searchPosts(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAllPosts();
        }
        return postRepository.findByContentContainingIgnoreCaseOrderByCreatedAtDesc(keyword);
    }

    // ===================================
    // 2. 게시물 생성 및 삭제
    // ===================================

    @Transactional
    public Post createPost(Long userId, String content, List<MultipartFile> files) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        // 1. 게시글 먼저 저장 (ID 생성을 위해)
        Post newPost = Post.builder()
                .user(user)
                .content(content)
                .build();

        Post savedPost = postRepository.save(newPost);

        // 📌 2. 미디어 파일이 있으면 MediaService 호출 (주석 해제 및 로직 연결)
        if (files != null && !files.isEmpty()) {
            // 빈 파일이 아닌지 체크
            boolean hasValidFile = files.stream().anyMatch(f -> !f.isEmpty());
            if (hasValidFile) {
                mediaService.uploadMedia(savedPost, files);
            }
        }

        return savedPost;
    }

    /**
     * 게시물 삭제 기능. 작성자만 삭제할 수 있습니다.
     */
    @Transactional
    public void deletePost(Long postId, Long userId) throws AccessDeniedException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postId));

        // 작성자 검증
        if (!post.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("해당 게시물을 삭제할 권한이 없습니다.");
        }

        // DB에서 게시물 삭제
        postRepository.delete(post);
        log.info("게시글 삭제 성공: Post={} User={}", postId, userId);
    }

    // ===================================
    // 3. 리트윗 기능
    // ===================================

    @Transactional
    public boolean toggleRetweet(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postId));

        Optional<Retweet> existingRetweet = retweetRepository.findByUserAndPost(user, post);

        if (existingRetweet.isPresent()) {
            // 취소
            retweetRepository.delete(existingRetweet.get());
            log.info("리트윗 취소: User={} Post={}", userId, postId);
            return false;
        } else {
            // 생성
            Retweet newRetweet = Retweet.builder()
                    .user(user)
                    .post(post)
                    .build();

            retweetRepository.save(newRetweet);
            log.info("리트윗 성공: User={} Post={}", userId, postId);
            return true;
        }
    }
}