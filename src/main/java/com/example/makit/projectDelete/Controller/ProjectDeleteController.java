package com.example.makit.projectDelete.Controller;

import com.example.makit.feedDelete.DTO.DeleteResponseDTO;
import com.example.makit.projectDelete.Service.ProjectDeleteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/project")
@CrossOrigin(origins = "*")
public class ProjectDeleteController {

    private final ProjectDeleteService projectDeleteService;

    public ProjectDeleteController(ProjectDeleteService projectDeleteService) {
        this.projectDeleteService = projectDeleteService;
    }

    @DeleteMapping("/delete/{projectId}")
    public ResponseEntity<DeleteResponseDTO> deleteProject(@PathVariable Long projectId) {
        try {
            projectDeleteService.deleteProject(projectId);
            return ResponseEntity.ok(new DeleteResponseDTO(true, "프로젝트가 정상적으로 삭제되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new DeleteResponseDTO(false, e.getMessage()));
        }
    }
}
