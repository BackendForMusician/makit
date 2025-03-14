package com.example.makit.trackUpload.controller;

import com.example.makit.login.Util.SessionUtil;
import com.example.makit.signup.Entity.UserEntity;
import com.example.makit.trackUpload.dto.TrackUploadRequestDTO;
import com.example.makit.trackUpload.dto.TrackUploadResponseDTO;
import com.example.makit.trackUpload.entity.Track;
import com.example.makit.trackUpload.service.TrackService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;
@Slf4j
@RestController
@RequestMapping("/api/tracks")
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;

    @PostMapping(value = "/upload",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TrackUploadResponseDTO> uploadTrack(
            @Valid @RequestPart(value = "request") TrackUploadRequestDTO request,
            BindingResult bindingResult,
            @RequestPart(value = "audioFile", required = false) MultipartFile audioFile,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            HttpServletRequest httpRequest
            ) {
        // 세션에서 로그인한 사용자 가져오기
        UserEntity loginMember = SessionUtil.getUserFromSession(httpRequest.getSession(false));
        if (loginMember == null) {
            return ResponseEntity.status(401).body(new TrackUploadResponseDTO(false, "로그인이 필요합니다."));
        }

        // DTO 유효성 검증 에러 처리
        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity
                    .badRequest()
                    .body(new TrackUploadResponseDTO(false, errorMessages));
        }
        if (audioFile == null || audioFile.isEmpty()) {
            return ResponseEntity.badRequest().body(new TrackUploadResponseDTO(false, "음원 파일이 누락되었습니다."));
        }
        if (imageFile == null || imageFile.isEmpty()) {
            return ResponseEntity.badRequest().body(new TrackUploadResponseDTO(false,"이미지 파일이 누락되었습니다."));
        }

        long maxAudioSize = 50 * 1024 * 1024; // 50MB
        long maxImageSize = 10 * 1024 * 1024; // 10MB

        if (audioFile.getSize() > maxAudioSize) {
            return ResponseEntity.badRequest().body(new TrackUploadResponseDTO(false,"음원 파일은 50MB를 초과할 수 없습니다."));
        }
        if (imageFile.getSize() > maxImageSize) {
            return ResponseEntity.badRequest().body(new TrackUploadResponseDTO(false,"이미지 파일은 10MB를 초과할 수 없습니다."));
        }

        try {
            // Track 엔티티(또는 저장 결과 DTO 등) 리턴 가정
            trackService.uploadTrackWithFiles(request, audioFile, imageFile, loginMember);
            return ResponseEntity.ok(new TrackUploadResponseDTO(true,"성공"));
        } catch (IllegalArgumentException e) {
          // 확장자 검증 실패 등
          return ResponseEntity.badRequest().body(new TrackUploadResponseDTO(false,e.getMessage()));
        } catch (Exception e) {
          // 기타 예외
            log.error(e.getMessage());
           return ResponseEntity.internalServerError().body(new TrackUploadResponseDTO(false,"서버 오류가 발생했습니다."));
        }

    }


}