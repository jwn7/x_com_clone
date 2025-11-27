package com.example.x_com_clone.service;

import com.example.x_com_clone.domain.Post;
import com.example.x_com_clone.domain.User;
import com.example.x_com_clone.domain.Retweet;
import com.example.x_com_clone.repository.PostRepository;
import com.example.x_com_clone.repository.UserRepository;
import com.example.x_com_clone.repository.RetweetRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.nio.file.AccessDeniedException; // AccessDeniedException import 추가

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RetweetRepository retweetRepository;
    private final MediaService mediaService;

    // ===================================
    // 1. 게시물 조회 및 검색
    // ===================================

    @Transactional // (readOnly = true) 제거
    public List<Post> findAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional // (readOnly = true) 제거
    public List<Post> findPostsByUser(User user) {
        return postRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Transactional // (readOnly = true) 제거
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

        Post newPost = Post.builder()
                .user(user)
                .content(content)
                .build();

        // 실제 MediaService 로직은 생략하고 Post만 저장합니다.
        // List<Media> mediaList = mediaService.uploadFiles(files, newPost);
        // newPost.setMediaList(mediaList);

        return postRepository.save(newPost);
    }

    /**
     * 게시물 삭제 기능. 작성자만 삭제할 수 있습니다.
     * @param userId 삭제를 요청한 사용자 ID
     * @param postId 삭제할 게시물 ID
     */
    @Transactional
    public void deletePost(Long postId, Long userId) throws AccessDeniedException { // 매개변수 순서 변경
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postId));

        // 작성자 검증
        if (!post.getUser().getUserId().equals(userId)) {
            // IllegalStateException 대신 AccessDeniedException을 던지도록 수정
            throw new AccessDeniedException("해당 게시물을 삭제할 권한이 없습니다.");
        }

        // DB에서 게시물 삭제 (DB 반영)
        postRepository.delete(post);
        log.info("게시글 삭제 성공: Post={} User={}", postId, userId);
    }

    // ===================================
    // 3. 리트윗 기능
    // ===================================

    /**
     * 특정 게시물에 대한 리트윗을 생성하거나 이미 존재하면 취소(삭제)합니다.
     * @param userId 리트윗을 시도하는 사용자 ID
     * @param postId 리트윗할 원본 게시물 ID
     * @return true: 리트윗 성공(생성), false: 리트윗 취소(삭제)
     */
    @Transactional
    public boolean toggleRetweet(Long userId, Long postId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postId));

        Optional<Retweet> existingRetweet = retweetRepository.findByUserAndPost(user, post);

        if (existingRetweet.isPresent()) {
            // 이미 리트윗 했으면: 취소 (삭제)
            retweetRepository.delete(existingRetweet.get());
            log.info("리트윗 취소: User={} Post={}", userId, postId);
            return false;
        } else {
            // 리트윗 하지 않았으면: 생성
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