package com.example.x_com_clone.service;

import com.example.x_com_clone.domain.Post;
import com.example.x_com_clone.domain.Reply;
import com.example.x_com_clone.domain.User;
import com.example.x_com_clone.repository.PostRepository;
import com.example.x_com_clone.repository.ReplyRepository;
import com.example.x_com_clone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 댓글 추가
     * @param postId 댓글이 달릴 게시글 ID
     * @param userId 댓글 작성자 ID
     * @param content 댓글 내용
     */
    public Reply addReply(Long postId, Long userId, String content) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found. id=" + postId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found. id=" + userId));

        Reply reply = Reply.builder()
                .post(post)
                .user(user)
                .content(content)
                .build(); // createdAt은 엔티티에서 @CreationTimestamp로 자동 설정

        return replyRepository.save(reply);
    }

    /**
     * 특정 게시글에 달린 댓글들을 시간순으로 조회
     */
    @Transactional(readOnly = true)
    public List<Reply> getRepliesForPost(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found. id=" + postId));

        return replyRepository.findByPostOrderByCreatedAtAsc(post);
    }
}
