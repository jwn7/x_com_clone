package com.example.x_com_clone.service;

import com.example.x_com_clone.domain.*;
import com.example.x_com_clone.repository.LikeRepository;
import com.example.x_com_clone.repository.PostRepository;
import com.example.x_com_clone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 좋아요 토글 기능
     * - 아직 안 눌렀으면: 좋아요 추가
     * - 이미 눌렀으면: 좋아요 취소
     * @return 처리 후 해당 게시글의 총 좋아요 수
     */
    public long toggleLike(Long postId, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found. id=" + postId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found. id=" + userId));

        return likeRepository.findByUserAndPost(user, post)
                .map(existingLike -> {
                    // 이미 눌러져 있으면 -> 삭제(취소)
                    likeRepository.delete(existingLike);
                    return likeRepository.countByPost(post);
                })
                .orElseGet(() -> {
                    // 아직 안 눌렀으면 -> 새로 생성
                    Like like = Like.builder()
                            .id(new LikeId(user.getUserId(), post.getPostId())) // 복합키 설정
                            .user(user)
                            .post(post)
                            .build();
                    likeRepository.save(like);
                    return likeRepository.countByPost(post);
                });
    }

    /**
     * 특정 게시글의 좋아요 수
     */
    @Transactional(readOnly = true)
    public long getLikeCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found. id=" + postId));
        return likeRepository.countByPost(post);
    }

    /**
     * 해당 유저가 이 게시글에 좋아요 눌렀는지 여부
     */
    @Transactional(readOnly = true)
    public boolean hasUserLiked(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found. id=" + postId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found. id=" + userId));
        return likeRepository.existsByUserAndPost(user, post);
    }
}
