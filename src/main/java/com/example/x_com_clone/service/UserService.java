package com.example.x_com_clone.service;

import com.example.x_com_clone.domain.User;
import com.example.x_com_clone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
// ⚠️ org.springframework.security.crypto.password.PasswordEncoder import 제거
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    // ⚠️ PasswordEncoder 필드 제거
    // private final PasswordEncoder passwordEncoder;

    /**
     * 새로운 사용자를 등록합니다 (비밀번호를 평문으로 저장).
     */
    @Transactional
    public User signup(String username, String email, String rawPassword) {

        validateDuplicateUser(username, email);

        // ⚠️ 암호화 과정 생략 (rawPassword를 그대로 사용)
        String storedPassword = rawPassword;

        // User 엔티티 생성 (생성자의 세 번째 인수는 이제 평문 비밀번호입니다.)
        User user = new User(username, email, storedPassword);

        return userRepository.save(user);
    }

    // ... validateDuplicateUser 메서드는 동일 ...
    private void validateDuplicateUser(String username, String email) {
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalStateException("이미 존재하는 사용자 이름입니다.");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }
    }
}