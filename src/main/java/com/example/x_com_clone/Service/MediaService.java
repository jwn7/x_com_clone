package com.example.x_com_clone.service;

import com.example.x_com_clone.domain.Media;
import com.example.x_com_clone.domain.Post;
import com.example.x_com_clone.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;

    private static final String UPLOAD_DIR = "uploads/";

    public List<Media> uploadMedia(Post post, List<MultipartFile> files) {
        List<Media> savedFiles = new ArrayList<>();

        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            for (MultipartFile file : files) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR + fileName);

                Files.write(filePath, file.getBytes());

                Media media = Media.builder()
                        .post(post)
                        .fileUrl("/uploads/" + fileName)
                        .fileType(file.getContentType())
                        .build();

                savedFiles.add(mediaRepository.save(media));
            }
        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }

        return savedFiles;
    }
}
