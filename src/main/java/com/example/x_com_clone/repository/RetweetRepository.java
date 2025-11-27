package com.example.x_com_clone.repository;

import com.example.x_com_clone.domain.Retweet;
import com.example.x_com_clone.domain.User;
import com.example.x_com_clone.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface RetweetRepository extends JpaRepository<Retweet, Long> {

    /**
     * 특정 사용자가 특정 게시물을 리트윗했는지 확인합니다. (리트윗 토글/중복 확인용)
     */
    Optional<Retweet> findByUserAndPost(User user, Post post);

    // 📌 추가: 특정 사용자가 수행한 리트윗 목록을 조회합니다. (사용자 타임라인용)
    List<Retweet> findByUser(User user);

    /**
     * 특정 게시물의 총 리트윗 수를 계산합니다. (카운트 표시용)
     */
    long countByPost(Post post);

    /**
     * 모든 리트윗 기록을 최신순으로 조회합니다. (타임라인 통합용)
     */
    List<Retweet> findAllByOrderByCreatedAtDesc();
}