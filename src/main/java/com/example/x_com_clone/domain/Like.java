package com.example.x_com_clone.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "likes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Like {

    @EmbeddedId
    private LikeId id;

    // 좋아요 누른 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")  // LikeId.userId 매핑
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 좋아요가 달린 글
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")  // LikeId.postId 매핑
    @JoinColumn(name = "post_id", nullable = false)
    private com.example.x_com_clone.domain.PostService post;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
