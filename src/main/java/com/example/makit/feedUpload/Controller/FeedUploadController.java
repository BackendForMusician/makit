package com.example.makit.feedUpload.Controller;

import com.example.makit.feedUpload.DTO.FeedUploadRequestDTO;
import com.example.makit.feedUpload.Service.FeedUploadService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/feed")
public class FeedUploadController {

    private final FeedUploadService feedUploadService;

    public FeedUploadController(FeedUploadService feedUploadService) {
        this.feedUploadService = feedUploadService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFeed(
            @RequestParam(name = "data") String requestData,
            @RequestPart(name = "audio", required = true) MultipartFile audioFile,
            @RequestPart(name = "image", required = true) MultipartFile imageFile) {

        ObjectMapper objectMapper = new ObjectMapper();
        FeedUploadRequestDTO request;
        try {
            request = objectMapper.readValue(requestData, FeedUploadRequestDTO.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Invalid JSON format");
        }

        if (!request.isValidTagCount()) {
            return ResponseEntity.badRequest().body("태그는 최대 3개까지 선택할 수 있습니다.");
        }

        if (!feedUploadService.isValidAudioFile(audioFile)) {
            return ResponseEntity.badRequest().body("지원되지 않는 음원 형식이거나 50MB를 초과했습니다.");
        }

        if (!feedUploadService.isValidImageFile(imageFile)) {
            return ResponseEntity.badRequest().body("지원되지 않는 이미지 형식이거나 10MB를 초과했습니다.");
        }

        String audioUrl = feedUploadService.uploadFileToS3(audioFile, "audio");
        String imageUrl = feedUploadService.uploadFileToS3(imageFile, "images");

        request.setAudioUrl(audioUrl);
        request.setImageUrl(imageUrl);

        feedUploadService.saveFeed(request);
        return ResponseEntity.ok("피드가 성공적으로 업로드되었습니다.");
    }
}
