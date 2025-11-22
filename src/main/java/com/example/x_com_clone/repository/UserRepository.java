package com.example.x_com_clone.repository;

import com.example.x_com_clone.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // 💡 Optional 클래스 임포트

@Repository
// 💡 ID 타입: Long으로 올바르게 설정되어 있습니다.
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 사용자 이름으로 User 엔티티를 Optional로 조회합니다.
     * UserService에서 인증, 프로필 조회, 중복 검사 시 사용됩니다.
     */
    Optional<User> findByUsername(String username);

    /**
     * 이메일로 User 엔티티를 Optional로 조회합니다.
     * UserService에서 중복 검사 시 사용됩니다.
     */
    Optional<User> findByEmail(String email);
}