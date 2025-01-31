package com.example.makit.profile.Controller;

import com.example.makit.login.Util.SessionUtil;
import com.example.makit.profile.Entity.UserProfileImage;
import com.example.makit.profile.Service.UserProfileService;
import com.example.makit.signup.Entity.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    private final UserProfileService service;

    public UserProfileController(UserProfileService service) {
        this.service = service;
    }

    @PostMapping("/image-upload")
    public ResponseEntity<String> uploadProfileImage(
            @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        UserEntity loginUser = SessionUtil.getUserFromSession(request.getSession(false));
        if (loginUser == null) {
            return ResponseEntity.status(401).body("Unauthorized: Please log in.");
        }
        try {
            UserProfileImage profileImage = service.uploadProfileImage(file, loginUser);
            return ResponseEntity.ok("이미지 업로드 성공: " + profileImage.getUrl());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("이미지 처리 중 오류가 발생했습니다.");
        }
    }
}
