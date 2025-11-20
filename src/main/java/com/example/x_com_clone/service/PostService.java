package com.example.x_com_clone.service;

import com.example.x_com_clone.domain.Post;
import com.example.x_com_clone.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    /**
     * 홈 화면에서 전체 게시물 최신순 조회
     */
    public List<Post> findAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * 검색: keyword 포함 글들 최신순
     * HomeController의 search()에서 사용
     */
    public List<Post> searchPosts(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAllPosts();
        }
        return postRepository.findByContentContainingIgnoreCaseOrderByCreatedAtDesc(keyword);
    }

    /**
     * 폼에서 content 문자열만 넘어오는 경우
     * HomeController의 createPost()에서 사용
     */
    public Post createPost(String content) {
        Post post = Post.builder()
                .content(content)
                // TODO: 나중에 로그인 붙으면 여기서 user 넣어주기
                .createdAt(LocalDateTime.now())
                .build();

        return postRepository.save(post);
    }

    // 필요하면 나중에 Post 객체 직접 받는 버전도 같이 둘 수 있음
    public Post createPost(Post post) {
        if (post.getCreatedAt() == null) {
            post.setCreatedAt(LocalDateTime.now());
        }
        return postRepository.save(post);
    }

    public void deletePost(Long postId) {
        if (!postRepository.existsById(postId)) {
            // 못 찾았을 때 그냥 조용히 넘어가고 싶으면 이 if 블록을 통째로 지워도 됨
            throw new IllegalArgumentException("Post not found. id=" + postId);
        }
        postRepository.deleteById(postId);
    }

}
