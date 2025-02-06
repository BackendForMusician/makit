package com.example.makit.feedUpload.Controller;

import com.example.makit.feedUpload.DTO.FeedUploadRequestDTO;
import com.example.makit.feedUpload.Service.FeedUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/feed")
public class FeedUploadController {

    private final FeedUploadService feedUploadService;

    public FeedUploadController(FeedUploadService feedUploadService) {
        this.feedUploadService = feedUploadService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFeed(@RequestPart("data") FeedUploadRequestDTO request,
                                             @RequestPart("audio") MultipartFile audioFile,
                                             @RequestPart("image") MultipartFile imageFile) {

        if (!feedUploadService.isValidAudioFile(audioFile)) {
            return ResponseEntity.badRequest().body("지원되지 않는 음원 형식이거나 용량이 초과되었습니다.");
        }

        if (!feedUploadService.isValidImageFile(imageFile)) {
            return ResponseEntity.badRequest().body("지원되지 않는 이미지 형식이거나 용량이 초과되었습니다.");
        }

        String audioUrl = feedUploadService.uploadFileToS3(audioFile, "audio");
        String imageUrl = feedUploadService.uploadFileToS3(imageFile, "images");

        request.setAudioUrl(audioUrl);
        request.setImageUrl(imageUrl);

        feedUploadService.saveFeed(request);
        return ResponseEntity.ok("피드가 성공적으로 업로드되었습니다.");
    }
}
