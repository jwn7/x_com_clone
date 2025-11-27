package com.example.x_com_clone.repository;

import com.example.x_com_clone.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.x_com_clone.domain.User;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 내용에 keyword가 포함된 게시물 검색 (대소문자 무시)
    List<Post> findByContentContainingIgnoreCaseOrderByCreatedAtDesc(String keyword);

    // 📌 TimelineService에서 특정 사용자의 원본 게시물을 조회하기 위해 사용됨
    List<Post> findByUser(User user);

    // 특정 사용자가 작성한 게시물 최신순 정렬
    List<Post> findByUserOrderByCreatedAtDesc(User user);

    // 전체 게시물 최신순 정렬
    List<Post> findAllByOrderByCreatedAtDesc();
}