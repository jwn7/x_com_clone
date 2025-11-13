package com.example.x_com_clone.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
//CREATE TABLE Users (
//  user_id BIGINT PRIMARY KEY,
//  username VARCHAR(50) NOT NULL UNIQUE,
//  email VARCHAR(255) NOT NULL UNIQUE,
//  password_hash CHAR(60) NOT NULL,
//  bio TEXT, ?
//  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
//  profile_image_url VARCHAR(512)
//) ENGINE=InnoDB;
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", unique = true, nullable = false)
    private String username;
    @Column(name = "password", nullable = false, length = 60)
    private String password;
    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "profile_image_url", length = 512)
    private String profileImageUrl;


}
