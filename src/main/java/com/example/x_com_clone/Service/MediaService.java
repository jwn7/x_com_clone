package com.example.x_com_clone.service;

import com.example.x_com_clone.domain.Media;
import com.example.x_com_clone.domain.Post;
import com.example.x_com_clone.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;

    // 📌 [수정됨] C드라이브 절대 경로로 변경 (WebConfig와 경로 일치시키기 위해 'media' 폴더 추가)
    private static final String UPLOAD_DIR = "C:/xcom_upload_folder/uploads/media/";

    public List<Media> uploadMedia(Post post, List<MultipartFile> files) {
        List<Media> savedFiles = new ArrayList<>();

        try {
            // 폴더가 없으면 생성 (C:/xcom_upload_folder/uploads/media/)
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            for (MultipartFile file : files) {
                String uuid = UUID.randomUUID().toString();
                String fileName = uuid + "_" + file.getOriginalFilename();

                // 1. 물리적 파일 저장 (C드라이브)
                Path filePath = Paths.get(UPLOAD_DIR + fileName);
                Files.write(filePath, file.getBytes());

                // 2. DB 저장 (웹 접근 URL)
                // WebConfig 설정에 따라 /uploads/media/... 로 요청하면
                // 실제로는 C:/xcom_upload_folder/uploads/media/... 파일을 보여줌
                Media media = Media.builder()
                        .post(post)
                        .fileUrl("/uploads/media/" + fileName) // 📌 URL 경로 주의
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