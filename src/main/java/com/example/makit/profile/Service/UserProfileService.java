package com.example.makit.profile.Service;

import com.example.makit.profile.Entity.UserProfileImage;
import com.example.makit.profile.Repository.UserProfileImageRepository;
import com.example.makit.signup.Entity.UserEntity;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class UserProfileService {

    private final UserProfileImageRepository repository;

    public UserProfileService(UserProfileImageRepository repository) {
        this.repository = repository;
    }

    // 프로필 이미지 업로드 처리
    public UserProfileImage uploadProfileImage(MultipartFile file, UserEntity user) throws IOException {
        // 1. 파일 크기 검사 (10MB 제한)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("파일 크기는 10MB를 초과할 수 없습니다.");
        }

        // 2. 확장자 검사
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isValidExtension(originalFilename)) {
            throw new IllegalArgumentException("허용되지 않은 파일 형식입니다. (JPG, PNG, RAW만 허용)");
        }

        // 3. 저장 경로 생성 (상대경로)
        String uploadDir = "uploads/profile/";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 4. 파일 저장
        String newFileName = UUID.randomUUID() + ".jpg"; // 새 파일명 생성
        File resizedFile = new File(uploadDir + newFileName);

        // 이미지 리사이즈 (400x400)
        Thumbnails.of(file.getInputStream())
                .size(400, 400)
                .outputFormat("jpg")
                .toFile(resizedFile);

        // 5. 기존 프로필 이미지 삭제 (수정)
        UserProfileImage existingImage = repository.findByUser(user);
        if (existingImage != null) {
            File oldFile = new File(existingImage.getUrl());
            if (oldFile.exists()) {
                oldFile.delete();
            }
            repository.delete(existingImage);
        }

        // 6. 새로운 프로필 이미지 저장
        UserProfileImage profileImage = new UserProfileImage();
        profileImage.setUser(user);
        profileImage.setUrl(resizedFile.getPath()); // 상대 경로 저장
        profileImage.setCreatedAt(LocalDate.now());
        return repository.save(profileImage);
    }

    // 파일 확장자 유효성 검사
    private boolean isValidExtension(String filename) {
        String lowerCaseFilename = filename.toLowerCase();
        return lowerCaseFilename.endsWith(".jpg") ||
               lowerCaseFilename.endsWith(".jpeg") ||
               lowerCaseFilename.endsWith(".png") ||
               lowerCaseFilename.endsWith(".raw");
    }
}
