package com.example.x_com_clone.service;

import com.example.x_com_clone.domain.User;
import com.example.x_com_clone.dto.UserSignupRequest;
import com.example.x_com_clone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 새로운 사용자를 등록합니다 (비밀번호를 암호화하여 저장).
     */
    @Transactional
    public User signup(UserSignupRequest request) {
        String username = request.getUsername();
        String email = request.getEmail();
        String rawPassword = request.getPassword();

        validateDuplicateUser(username, email);

        String hashedPassword = passwordEncoder.encode(rawPassword);

        // User 생성자가 passwordHash를 받도록 되어 있음
        User user = new User(username, email, hashedPassword);

        return userRepository.save(user);
    }

    // --------------------------------------------------------
    // ✨ [로그인 기능: authenticate 메서드]
    // --------------------------------------------------------

    /**
     * 사용자 인증을 처리합니다.
     * @param identifier 로그인에 사용되는 값 (아이디 또는 이메일)
     * @param rawPassword 사용자가 입력한 평문 비밀번호
     * @return 인증에 성공한 User 객체
     * @throws IllegalArgumentException 인증 실패 시 발생
     */
    public User authenticate(String identifier, String rawPassword) {

        // 1. 사용자 찾기: 아이디나 이메일 중 하나로 검색을 시도합니다.
        // findByUsernameOrEmail 메서드를 UserRepository에 추가하는 것을 가정하고 사용합니다.
        // 만약 추가하지 않았다면, 기존처럼 findByUsername -> findByEmail 순으로 호출해야 합니다.
        User user = userRepository.findByUsername(identifier);
        if (user == null) {
            user = userRepository.findByEmail(identifier);
        }

        // 2. 사용자 존재 여부 확인
        if (user == null) {
            throw new IllegalArgumentException("아이디(또는 이메일)를 찾을 수 없습니다.");
        }

        // 3. 비밀번호 일치 여부 확인 (핵심)
        // 🚨 수정된 부분: user.getPassword() 대신 user.getPasswordHash() 호출
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 4. 인증 성공
        return user;
    }

    // --------------------------------------------------------
    // ... (기존 validateDuplicateUser 메서드) ...

    private void validateDuplicateUser(String username, String email) {
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalStateException("이미 존재하는 사용자 이름입니다.");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }
    }
}