package com.example.makit.track.controller;

import com.example.makit.track.dto.TrackUploadRequest;
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
    private final UserService userService;


    @PostMapping(value = "/upload",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadTrack(
            @Valid @RequestPart TrackUploadRequest request,
            BindingResult bindingResult,
            @RequestPart("audioFile") MultipartFile audioFile,
            @RequestPart("imageFile") MultipartFile imageFile
            ) {

        // DTO 유효성 검증 에러 처리
        if (bindingResult.hasErrors()) {
            // 에러 메시지를 수집해서 반환할 수 있습니다.
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }



        try {
            trackService.uploadTrack(
                    user,
                    request.getTitle(),
                    request.getDescription(),
                    request.getGenreIds(),
                    request.getTagIds(),
                    request.getMusicFile(),
                    request.getImageFile());
            return ResponseEntity.ok("음원이 성공적으로 업로드되었습니다.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("업로드 처리 중 오류가 발생하였습니다.");
        }
    }
}