package com.example.x_com_clone.repository;

import com.example.x_com_clone.domain.Follow;
import com.example.x_com_clone.domain.FollowId;
import com.example.x_com_clone.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {

    List<Follow> findByFollower(User follower);

    List<Follow> findByFollowing(User following);

    boolean existsByFollowerAndFollowing(User follower, User following);

    // 🔥 언팔로우용
    void deleteByFollowerAndFollowing(User follower, User following);
}
