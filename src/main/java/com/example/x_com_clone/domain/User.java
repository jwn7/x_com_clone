package com.example.x_com_clone.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    // username VARCHAR(50) NOT NULL UNIQUE
    @Column(nullable = false, length = 50, unique = true)
    private String username;

    // email VARCHAR(255) NOT NULL UNIQUE
    @Column(nullable = false, length = 255, unique = true)
    private String email;

    // 🔑 password_hash CHAR(60) NOT NULL (⚠️ 필드명 수정)
    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;

    // 💡 bio TEXT (누락된 필드 추가)
    @Column(columnDefinition = "TEXT")
    private String bio;

    // 💡 created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP (누락된 필드 추가)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // profile_image_url VARCHAR(512)
    @Column(name = "profile_image_url", length = 512)
    private String profileImageUrl;
    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }
}