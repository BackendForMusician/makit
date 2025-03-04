package com.example.makit.feedUpload.Controller;

import com.example.makit.feedUpload.DTO.FeedUploadRequestDTO;
import com.example.makit.feedUpload.Service.FeedUploadService;
import com.example.makit.login.Util.SessionUtil;
import com.example.makit.signup.Entity.UserEntity;
import com.example.makit.trackUpload.dto.TrackUploadResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadFeed(
            @Valid @RequestPart(value = "data") FeedUploadRequestDTO request,
            @RequestPart(name = "audio", required = true) MultipartFile audioFile,
            @RequestPart(name = "image", required = true) MultipartFile imageFile,
            HttpServletRequest httpRequest) {

        // 세션에서 로그인한 사용자 가져오기
        UserEntity loginMember = SessionUtil.getUserFromSession(httpRequest.getSession(false));
        if (loginMember == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
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

        feedUploadService.saveFeed(request, loginMember);
        return ResponseEntity.ok("피드가 성공적으로 업로드되었습니다.");
    }
}
