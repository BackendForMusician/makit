package com.example.makit.track.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.makit.track.dto.TrackUploadRequestDTO;
import com.example.makit.track.entity.Track;
import com.example.makit.track.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrackService {

    private final TrackRepository trackRepository;
    private final AmazonS3 amazonS3;

    // 허용되는 확장자
    private static final List<String> ALLOWED_AUDIO_EXT = List.of("mp3", "wav", "flac");
    private static final List<String> ALLOWED_IMAGE_EXT = List.of("png", "jpg", "jpeg");

    // 허용되는 MIME 타입
    private static final List<String> ALLOWED_AUDIO_MIME = List.of(
            "audio/mpeg",       // mp3
            "audio/x-wav",      // wav
            "audio/wav",        // wav
            "audio/flac"        // flac
    );
    private static final List<String> ALLOWED_IMAGE_MIME = List.of(
            "image/png",
            "image/jpeg",
            "image/jpg"
    );

    public Track uploadTrackWithFiles(TrackUploadRequestDTO request,
                                      MultipartFile audioFile,
                                      MultipartFile imageFile) {
        // 1) 파일 확장자 + MIME 타입 검증
        validateAudioFile(audioFile);
        validateImageFile(imageFile);
        // 2) S3 업로드 (아래는 예시 - 실제로는 s3Uploader 같은 유틸 클래스를 따로 두는 경우가 많음)
        String audioUrl = uploadFile(audioFile, "trackAudio");
        String imageUrl = uploadFile(imageFile, "trackImage");

        // 3) DB 저장
        Track track = new Track();
        track.setTitle(request.getTitle());
        track.setDescription(request.getDescription());
        track.setAudioUrl(audioUrl);
        track.setImageUrl(imageUrl);
        // 필요한 필드들 설정 (장르, 태그 등)
        return trackRepository.save(track);
    }

    /**
     * 오디오 파일 (확장자 + MIME) 검증
     */
    private void validateAudioFile(MultipartFile audioFile) {
        String originalFilename = audioFile.getOriginalFilename();
        String ext = getExtension(originalFilename).toLowerCase();

        // 확장자 검증
        if (!ALLOWED_AUDIO_EXT.contains(ext)) {
            throw new IllegalArgumentException("허용되지 않는 음원 파일 형식(확장자)입니다: " + ext);
        }

        // MIME 타입 검증
        String mimeType = audioFile.getContentType();
        if (mimeType == null || !ALLOWED_AUDIO_MIME.contains(mimeType)) {
            throw new IllegalArgumentException("허용되지 않는 음원 파일 형식(MIME)입니다: " + mimeType);
        }
    }

    private void validateImageFile(MultipartFile imageFile) {
        String originalFilename = imageFile.getOriginalFilename();
        String ext = getExtension(originalFilename).toLowerCase();

        // 확장자 검증
        if (!ALLOWED_IMAGE_EXT.contains(ext)) {
            throw new IllegalArgumentException("허용되지 않는 이미지 파일 형식(확장자)입니다: " + ext);
        }

        // MIME 타입 검증
        String mimeType = imageFile.getContentType();
        if (mimeType == null || !ALLOWED_IMAGE_MIME.contains(mimeType)) {
            throw new IllegalArgumentException("허용되지 않는 이미지 파일 형식(MIME)입니다: " + mimeType);
        }
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return ""; // 확장자가 없는 경우
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    private String uploadFile(MultipartFile file, String folder) {
        String fileName = folder + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject("my-audio-image-bucket", fileName, inputStream, metadata);
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }

        return amazonS3.getUrl("my-audio-image-bucket", fileName).toString();
    }
}
