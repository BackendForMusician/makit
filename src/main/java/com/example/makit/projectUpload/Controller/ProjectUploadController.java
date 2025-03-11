package com.example.makit.projectUpload.Controller;

import com.example.makit.projectUpload.DTO.ProjectUploadRequestDTO;
import com.example.makit.projectUpload.DTO.ProjectUploadResponseDTO;
import com.example.makit.projectUpload.Service.ProjectUploadService;
import com.example.makit.login.Util.SessionUtil;
import com.example.makit.signup.Entity.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/project")
public class ProjectUploadController {

    private final ProjectUploadService projectUploadService;

    public ProjectUploadController(ProjectUploadService projectUploadService) {
        this.projectUploadService = projectUploadService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectUploadResponseDTO> uploadProject(
            @Valid @RequestPart(value = "data") ProjectUploadRequestDTO request,
            @RequestPart(name = "files", required = true) List<MultipartFile> files,
            HttpServletRequest httpRequest) {

        UserEntity loginMember = SessionUtil.getUserFromSession(httpRequest.getSession(false));
        if (loginMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ProjectUploadResponseDTO(false, "로그인이 필요합니다."));
        }

        ProjectUploadResponseDTO response = projectUploadService.saveProject(request, loginMember, files);

        if (!response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
