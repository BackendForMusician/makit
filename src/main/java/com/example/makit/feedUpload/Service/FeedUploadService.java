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

    public FeedUploadService(AmazonS3 amazonS3, FeedRepository feedRepository) {
        this.amazonS3 = amazonS3;
        this.feedRepository = feedRepository;
    }

    public boolean isValidAudioFile(MultipartFile file) {
        return file.getSize() <= 100 * 1024 * 1024 && SUPPORTED_AUDIO_TYPES.contains(file.getContentType());
    }

    public boolean isValidImageFile(MultipartFile file) {
        return file.getSize() <= 10 * 1024 * 1024 && SUPPORTED_IMAGE_TYPES.contains(file.getContentType());
    }

    public String uploadFileToS3(MultipartFile file, String folder) {
        String fileName = folder + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject("your-bucket-name", fileName, inputStream, metadata);
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }

        return amazonS3.getUrl("your-bucket-name", fileName).toString();
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
