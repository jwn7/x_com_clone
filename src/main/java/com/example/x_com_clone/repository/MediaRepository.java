package com.example.x_com_clone.repository;

import com.example.x_com_clone.domain.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media, Long> {
    List<Media> findByPost_PostId(Long postId);
}
