package com.example.makit.track.controller;

import com.example.makit.track.dto.TrackUploadRequest;
import com.example.makit.track.service.TrackService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/tracks")
public class TrackController {

    private final TrackService trackService;

    @PostMapping(value = "/upload",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadTrack(
            @Valid @RequestPart TrackUploadRequest request,
            BindingResult bindingResult,
            @RequestPart(value = "audioFile", required = false) MultipartFile audioFile,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
            ) {

        // DTO 유효성 검증 에러 처리
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        if (audioFile == null || audioFile.isEmpty()) {
            return ResponseEntity.badRequest().body("음원 파일이 누락되었습니다.");
        }
        if (imageFile == null || imageFile.isEmpty()) {
            return ResponseEntity.badRequest().body("이미지 파일이 누락되었습니다.");
        }

        long maxAudioSize = 50 * 1024 * 1024; // 50MB
        long maxImageSize = 10 * 1024 * 1024; // 10MB



    }
}