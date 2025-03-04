package com.example.makit.trackUpload.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.makit.exception.GenreNotFoundException;
import com.example.makit.exception.TagNotFoundException;
import com.example.makit.feedUpload.Entity.TagEntity;
import com.example.makit.feedUpload.Repository.TagRepository;
import com.example.makit.signup.Entity.GenreEntity;
import com.example.makit.signup.Entity.UserEntity;
import com.example.makit.signup.Repository.GenreRepository;
import com.example.makit.trackUpload.dto.TrackUploadRequestDTO;
import com.example.makit.trackUpload.entity.Track;
import com.example.makit.trackUpload.entity.TrackGenre;
import com.example.makit.trackUpload.entity.TrackTag;
import com.example.makit.trackUpload.repository.TrackGenreRepository;
import com.example.makit.trackUpload.repository.TrackRepository;
import com.example.makit.trackUpload.repository.TrackTagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackService {

    private final TrackRepository trackRepository;
    private final GenreRepository genreRepository;
    private final TagRepository tagRepository;
    private final TrackGenreRepository trackGenreRepository;
    private final TrackTagRepository trackTagRepository;
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

    @Transactional
    public void uploadTrackWithFiles(TrackUploadRequestDTO request,
                                      MultipartFile audioFile,
                                      MultipartFile imageFile,
                                      UserEntity loginMember) {

        // 1) 파일 확장자 + MIME 타입 검증
        validateAudioFile(audioFile);
        validateImageFile(imageFile);


        // 2) S3 업로드
        String audioUrl = uploadFile(audioFile, "trackAudio");
        String imageUrl = uploadFile(imageFile, "trackImage");

        // 3) DB 저장
        Track track = new Track();
        track.setTitle(request.getTitle());
        track.setLyrics(request.getLyrics());
        track.setDescription(request.getDescription());
        track.setAudioUrl(audioUrl);
        track.setImageUrl(imageUrl);
        track.setUser(loginMember);
        trackRepository.save(track);

        // 장르 및 태그 설정 (오류 발생 시 롤백)
        Set<TrackGenre> trackGenres = request.getGenres().stream()
                .map(genreName -> createTrackGenre(track, genreName))
                .collect(Collectors.toSet());

        Set<TrackTag> trackTags = request.getTags().stream()
                .map(tagName -> createTrackTag(track, tagName))
                .collect(Collectors.toSet());

        // TrackGenre, TrackTag 저장
        trackGenreRepository.saveAll(trackGenres);
        trackTagRepository.saveAll(trackTags);
        // 필요한 필드들 설정 (장르, 태그 등)

    }


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
        String fileName = "track/"+folder + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
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

    private TrackGenre createTrackGenre(Track track, String genreName) {
        GenreEntity genre = genreRepository.findByGenreName(genreName)
                .orElseThrow(() -> new GenreNotFoundException(genreName));

        TrackGenre trackGenre = new TrackGenre();
        trackGenre.setTrack(track);
        trackGenre.setGenre(genre);
        return trackGenre;
    }

    private TrackTag createTrackTag(Track track, String tagName) {
        TagEntity tag = tagRepository.findByName(tagName)
                .orElseThrow(() -> new TagNotFoundException(tagName));

        TrackTag trackTag = new TrackTag();
        trackTag.setTrack(track);
        trackTag.setTag(tag);
        return trackTag;
    }
}
