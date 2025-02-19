package com.example.makit.track.controller;

import com.example.makit.track.dto.TrackUploadRequest;
import com.example.makit.track.service.TrackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/tracks")
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;

    @PostMapping(value = "/upload",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadTrack(
            @Valid @RequestPart(value = "request") TrackUploadRequest request,
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

        if (audioFile.getSize() > maxAudioSize) {
            return ResponseEntity.badRequest().body("음원 파일은 50MB를 초과할 수 없습니다.");
        }
        if (imageFile.getSize() > maxImageSize) {
            return ResponseEntity.badRequest().body("이미지 파일은 10MB를 초과할 수 없습니다.");
        }

        try {
            // Track 엔티티(또는 저장 결과 DTO 등) 리턴 가정
            Track savedTrack = trackService.uploadTrackWithFiles(request, audioFile, imageFile);
            return ResponseEntity.ok(savedTrack);
        } catch (IllegalArgumentException e) {
            // 확장자 검증 실패 등
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // 기타 예외
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }

    }
}