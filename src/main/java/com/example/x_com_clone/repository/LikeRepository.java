package com.example.x_com_clone.repository;

import com.example.x_com_clone.domain.Like;
import com.example.x_com_clone.domain.LikeId;
import com.example.x_com_clone.domain.PostService;
import com.example.x_com_clone.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, LikeId> {

    boolean existsByUserAndPost(User user, PostService post);

    List<Like> findByPost(PostService post);

    List<Like> findByUser(User user);
}
