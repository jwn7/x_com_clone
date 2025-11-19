package com.example.x_com_clone.Service;

import com.example.x_com_clone.domain.Post;
import com.example.x_com_clone.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    /**
     * 전체 게시물을 최신순으로 조회
     */
    public List<Post> findAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * 새 게시물 작성
     */
    public void createPost(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("내용이 비어있습니다.");
        }

        // 나중에 Post 엔티티에 맞춰서 필드만 채우면 됨
        Post post = Post.builder()
                .content(content)   // Post에 content 필드가 있다고 가정
                .build();

        postRepository.save(post);
    }

    /**
     * 게시물 검색
     */
    public List<Post> searchPosts(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAllPosts();  // 검색어 없으면 전체 목록
        }
        return postRepository.findByContentContainingIgnoreCaseOrderByCreatedAtDesc(keyword);
    }
}
