package com.example.makit.feedUpload.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.makit.feedUpload.DTO.FeedUploadRequestDTO;
import com.example.makit.feedUpload.Entity.FeedEntity;
import com.example.makit.feedUpload.Repository.FeedRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FeedUploadService {

    private final AmazonS3 amazonS3;
    private final FeedRepository feedRepository;


    private static final List<String> SUPPORTED_AUDIO_TYPES = Arrays.asList("audio/mpeg", "audio/wav", "audio/aac", "audio/flac");
    private static final List<String> SUPPORTED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/raw");


    private static final long MAX_AUDIO_SIZE = 50 * 1024 * 1024; // 50MB
    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10MB

    public FeedUploadService(AmazonS3 amazonS3, FeedRepository feedRepository) {
        this.amazonS3 = amazonS3;
        this.feedRepository = feedRepository;
    }


    public boolean isValidAudioFile(MultipartFile file) {
        return file.getSize() <= MAX_AUDIO_SIZE && SUPPORTED_AUDIO_TYPES.contains(file.getContentType());
    }


    public boolean isValidImageFile(MultipartFile file) {
        return file.getSize() <= MAX_IMAGE_SIZE && SUPPORTED_IMAGE_TYPES.contains(file.getContentType());
    }


    public String uploadFileToS3(MultipartFile file, String folder) {
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


    public void saveFeed(FeedUploadRequestDTO request) {
        FeedEntity feed = new FeedEntity();
        feed.setTitle(request.getTitle());
        feed.setDescription(request.getDescription());
        feed.setAudioUrl(request.getAudioUrl());
        feed.setImageUrl(request.getImageUrl());
        feed.setTags(String.join(",", request.getTags()));
        feedRepository.save(feed);
    }
}
