package com.example.x_com_clone.controller;

import com.example.x_com_clone.domain.Media;
import com.example.x_com_clone.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/media")
public class MediaController {

    private final MediaRepository mediaRepository;

    @GetMapping("/{postId}")
    public List<Media> getMediaByPost(@PathVariable Long postId) {
        return mediaRepository.findByPost_PostId(postId);
    }
}
