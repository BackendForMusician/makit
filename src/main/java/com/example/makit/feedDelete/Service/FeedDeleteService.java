package com.example.makit.feedDelete.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.example.makit.feedUpload.Entity.FeedEntity;
import com.example.makit.feedUpload.Repository.FeedRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import
        java.net.URI;

@Service
public class FeedDeleteService {

    private final FeedRepository feedRepository;
    private final AmazonS3 amazonS3;
    private static final String BUCKET_NAME = "my-audio-image-bucket";

    public FeedDeleteService(FeedRepository feedRepository, AmazonS3 amazonS3) {
        this.feedRepository = feedRepository;
        this.amazonS3 = amazonS3;
    }

    @Transactional
    public void deleteFeed(Long feedId) {
        FeedEntity feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 피드를 찾을 수 없습니다."));

        deleteFileFromS3(feed.getAudioUrl());
        deleteFileFromS3(feed.getImageUrl());

        feedRepository.delete(feed);
    }

    private void deleteFileFromS3(String fileUrl) {
        if (fileUrl != null && !fileUrl.isEmpty()) {
            String fileName = extractFileName(fileUrl);
            amazonS3.deleteObject(new DeleteObjectRequest(BUCKET_NAME, fileName));
        }
    }

    private String extractFileName(String fileUrl) {
        try {
            URI uri = new URI(fileUrl);
            return uri.getPath().substring(1);
        } catch (Exception e) {
            throw new RuntimeException("파일 URL 파싱 오류: " + e.getMessage());
        }
    }
}
