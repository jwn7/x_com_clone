package com.example.x_com_clone.repository;

import com.example.x_com_clone.domain.Like;
import com.example.x_com_clone.domain.LikeId;
import com.example.x_com_clone.domain.Post;
import com.example.x_com_clone.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, LikeId> {

    // 해당 유저가 해당 글에 좋아요 눌렀는지 여부
    boolean existsByUserAndPost(User user, Post post);

    // 특정 글에 달린 좋아요 전부
    List<Like> findByPost(Post post);

    // 특정 유저가 누른 좋아요 전부
    List<Like> findByUser(User user);

    // 🔥 좋아요 개수 (토글 후 개수 보여줄 때 사용)
    long countByPost(Post post);

    // 🔥 토글용: 유저 & 글로 해당 Like 한 개 찾기
    Optional<Like> findByUserAndPost(User user, Post post);
}
