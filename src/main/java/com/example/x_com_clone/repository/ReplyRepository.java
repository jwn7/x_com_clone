package com.example.x_com_clone.repository;

import com.example.x_com_clone.domain.Post;
import com.example.x_com_clone.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    // 특정 게시글에 달린 댓글을 작성 시간 순으로 가져오기
    List<Reply> findByPostOrderByCreatedAtAsc(Post post);
}
