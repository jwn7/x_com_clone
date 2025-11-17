package com.example.x_com_clone.service;

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
        Post post = Post.builder()
                .content(content)
                .build();  // createdAt은 엔티티 @PrePersist에서 자동 세팅
        postRepository.save(post);
    }

    /**
     * 게시물 검색
     */
    public List<Post> searchPosts(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            // 검색어 없으면 전체 목록 리턴
            return findAllPosts();
        }
        return postRepository.findByContentContainingIgnoreCaseOrderByCreatedAtDesc(keyword);
    }
}
