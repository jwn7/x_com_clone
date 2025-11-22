package com.example.x_com_clone.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 💡 JPA 표준: 기본 생성자 접근 제한
@AllArgsConstructor // @Builder를 위해 모든 필드를 인자로 받는 생성자 유지
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    // ManyToOne 관계 설정: 지연 로딩(LAZY)을 사용하여 성능 최적화
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;           // 글 쓴 사람 (User 엔티티 참조)

    @Column(nullable = false, length = 280)
    private String content;

    // 💡 생성 시각 자동 설정: 엔티티가 저장될 때 시간을 자동으로 기록
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


    // --- 💡 비즈니스 로직용 Builder 생성자 ---
    // User 객체를 직접 받아 연관관계를 설정합니다.
    @Builder
    public Post(User user, String content) {
        this.user = user;
        this.content = content;
        // createdAt은 필드 초기화 시 자동으로 설정됩니다.
        // this.createdAt = LocalDateTime.now();
    }

    // --- (선택적) 생성/업데이트 시점 자동화 리스너 ---
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}