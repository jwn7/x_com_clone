package com.example.x_com_clone.service;

import com.example.x_com_clone.domain.Post;
import com.example.x_com_clone.domain.User;
import com.example.x_com_clone.repository.PostRepository;
import com.example.x_com_clone.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile; // ⬅ 추가

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final MediaService mediaService; // ⬅ MediaService 주입 (이미 만들어놨다고 가정)

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
     * @param files 첨부할 이미지 파일들 (없으면 null 또는 빈 리스트)
     * @return 생성된 Post 객체
     */
    @Transactional
    public Post createPost(Long userId, String content, List<MultipartFile> files) {

        // 1. User ID를 사용하여 User 엔티티를 찾습니다.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        // 2. Post 엔티티 생성
        Post post = Post.builder()
                .user(user)
                .content(content)
                .build(); // createdAt은 엔티티에서 자동 설정된다고 가정

        // 3. 게시물 저장
        Post savedPost = postRepository.save(post);

        // 4. 파일이 있다면 Media 저장
        if (files != null && !files.isEmpty()) {
            mediaService.uploadMedia(savedPost, files);
        }

        return savedPost;
    }

    /**
     * 파일 없이 텍스트만 올리는 기존 API가 필요하면 이 오버로드를 써도 됨
     */
    @Transactional
    public Post createPost(Long userId, String content) {
        return createPost(userId, content, null);
    }

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

        // 2. 권한 검증
        Long postAuthorId = post.getUser().getUserId();

        if (!postAuthorId.equals(currentUserId)) {
            throw new IllegalStateException("You do not have permission to delete this post.");
        }

        // 3. 삭제 실행
        postRepository.delete(post);
    }

}
