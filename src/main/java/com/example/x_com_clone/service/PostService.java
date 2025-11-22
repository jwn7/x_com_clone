package com.example.x_com_clone.service;

import com.example.x_com_clone.domain.Post;
import com.example.x_com_clone.domain.User; // User 엔티티 import 필요
import com.example.x_com_clone.repository.PostRepository;
import com.example.x_com_clone.repository.UserRepository; // 💡 UserRepository import 필요
import jakarta.transaction.Transactional; // 💡 Transactional import 필요
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException; // 예외 처리용

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository; // 💡 UserRepository 주입

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

    // --- 2. 게시물 생성 (Create) ---

    /**
     * 새로운 게시물을 생성하고 DB에 저장합니다.
     * @param userId 현재 로그인한 사용자의 ID (세션에서 가져옴)
     * @param content 게시물 내용
     * @return 생성된 Post 객체
     */
    @Transactional
    public Post createPost(Long userId, String content) {

        // 1. User ID를 사용하여 User 엔티티를 찾습니다.
        // Optional을 사용하지 않고 get()을 바로 사용하면, 존재하지 않을 경우 NoSuchElementException 발생
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        // 2. Post 엔티티 생성 (User 객체와 content를 사용)
        Post post = Post.builder()
                .user(user) // 💡 User 객체를 직접 연결
                .content(content)
                .build(); // createdAt은 Post 엔티티에서 자동으로 설정됨

        // 3. 저장 및 반환
        return postRepository.save(post);
    }

    // 💡 기존 createPost(String content) 메서드는 사용되지 않으므로 제거합니다.
    // 💡 기존 createPost(Post post) 메서드도 API에서 사용하지 않으므로 제거합니다.

    // --- 3. 게시물 삭제 (Delete with Authority Check) ---

    /**
     * 게시물을 삭제합니다. 요청 사용자가 게시물의 작성자인지 검증합니다.
     * @param postId 삭제할 게시물 ID
     * @param currentUserId 현재 로그인한 사용자의 ID (삭제 권한 검증용)
     */
    @Transactional
    public void deletePost(Long postId, Long currentUserId) {

        // 1. 게시물 조회 (없으면 예외 발생)
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found. id=" + postId));

        // 2. 권한 검증: 현재 사용자의 ID와 게시물 작성자의 ID를 비교합니다.
        Long postAuthorId = post.getUser().getUserId();

        if (!postAuthorId.equals(currentUserId)) {
            throw new IllegalStateException("You do not have permission to delete this post.");
        }

        // 3. 삭제 실행
        postRepository.delete(post);
    }

    // 💡 기존 deletePost(Long postId) 메서드는 권한 검증이 없어 사용되지 않으므로 제거합니다.

}