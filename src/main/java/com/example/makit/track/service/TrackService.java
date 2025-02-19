package com.example.makit.track.service;

import com.amazonaws.services.s3.AmazonS3;
import com.example.makit.track.dto.TrackUploadRequest;
import com.example.makit.track.entity.Track;
import com.example.makit.track.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackService {

    private final TrackRepository trackRepository;  // JPA Repository 가정
    private final AmazonS3 amazonS3;            // S3 업로더 유틸(예시)

    /**
     * 파일 확장자 검증, S3 업로드, DB 저장 로직
     */
    public Track uploadTrackWithFiles(TrackUploadRequest request,
                                      MultipartFile audioFile,
                                      MultipartFile imageFile) {
        // 1) 파일 확장자 검증
        validateAudioExtension(audioFile.getOriginalFilename());  // 원하는 로직
        validateImageExtension(imageFile.getOriginalFilename());  // 원하는 로직

        // 2) S3 업로드
        String audioUrl = amazonS3.uploadFile(audioFile, "audio");
        String imageUrl = amazonS3.uploadFile(imageFile, "image");

        // 3) DB 저장 (Track 엔티티에 S3 경로 세팅)
        Track track = new Track();
        track.setTitle(request.getTitle());
        track.setDescription(request.getDescription());
        track.setAudioPath(audioUrl);
        track.setImagePath(imageUrl);

        // 추가 필드 (genreIds, tagIds 등)도 필요하면 세팅
        // track.setGenres(...);

        // 실제 DB 저장
        return trackRepository.save(track);
    }

    private void validateAudioExtension(String fileName) {
        // 확장자 추출 (단순 소문자 비교 예시)
        String ext = getExtension(fileName).toLowerCase();
        // 예: mp3, wav, flac 등 허용
        List<String> allowedAudioExt = List.of("mp3", "wav", "flac");
        if (!allowedAudioExt.contains(ext)) {
            throw new IllegalArgumentException("허용되지 않는 음원 파일 형식입니다. (" + ext + ")");
        }
    }

    private void validateImageExtension(String fileName) {
        // 확장자 추출 (단순 소문자 비교 예시)
        String ext = getExtension(fileName).toLowerCase();
        // 예: png, jpg, jpeg 등 허용
        List<String> allowedImageExt = List.of("png", "jpg", "jpeg");
        if (!allowedImageExt.contains(ext)) {
            throw new IllegalArgumentException("허용되지 않는 이미지 파일 형식입니다. (" + ext + ")");
        }
    }

    /**
     * 파일명에서 확장자만 추출
     * 예) "test.mp3" -> "mp3"
     */
    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return ""; // 확장자가 없는 경우
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}

