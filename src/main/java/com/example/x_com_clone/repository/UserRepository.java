package com.example.x_com_clone.repository;

import com.example.x_com_clone.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// 💡 ID 타입 수정: Integer -> Long
public interface UserRepository extends JpaRepository<User, Long> {

    // 사용자 이름으로 User 엔티티 조회 (중복 검사 등에 사용)
    User findByUsername(String username);

    // 이메일로 User 엔티티 조회 (중복 검사 등에 사용)
    User findByEmail(String email);
}