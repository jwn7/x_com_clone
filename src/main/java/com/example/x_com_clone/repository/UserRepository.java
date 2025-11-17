package com.example.x_com_clone.repository;

import com.example.x_com_clone.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface UserRepository extends JpaRepository<User,Integer> {

    User findByUsername(String username);

    User findByEmail(String email);

}
