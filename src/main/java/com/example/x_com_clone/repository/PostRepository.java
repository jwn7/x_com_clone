package com.example.x_com_clone.repository;

import com.example.x_com_clone.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 내용에 keyword가 포함된 게시물 검색 (대소문자 무시)
    List<Post> findByContentContainingIgnoreCaseOrderByCreatedAtDesc(String keyword);

    // 전체 게시물 최신순 정렬
    List<Post> findAllByOrderByCreatedAtDesc();
}
