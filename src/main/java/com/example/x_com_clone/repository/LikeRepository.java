package com.example.x_com_clone.repository;

import com.example.x_com_clone.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, LikeId> {

    boolean existsByUserAndPost(User user, Post post);

    List<Like> findByPost(Post post);

    List<Like> findByUser(User user);
}
