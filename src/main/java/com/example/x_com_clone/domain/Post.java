package com.example.x_com_clone.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity                          //
@Table(name = "posts")           // DB 테이블 이름 (Posts/ posts 중 실제 이름에 맞게)
@Getter
@Setter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;           // 글 쓴 사람

    @Column(nullable = false, length = 280)
    private String content;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;
}
