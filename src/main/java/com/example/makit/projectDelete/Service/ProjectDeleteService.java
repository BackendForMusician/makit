package com.example.makit.projectDelete.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.example.makit.projectUpload.Entity.ProjectEntity;
import com.example.makit.projectUpload.Repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;

@Service
public class ProjectDeleteService {

    private final ProjectRepository projectRepository;
    private final AmazonS3 amazonS3;
    private static final String BUCKET_NAME = "my-audio-image-bucket";

    public ProjectDeleteService(ProjectRepository projectRepository, AmazonS3 amazonS3) {
        this.projectRepository = projectRepository;
        this.amazonS3 = amazonS3;
    }

    @Transactional
    public void deleteProject(Long projectId) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 프로젝트를 찾을 수 없습니다."));
        deleteFilesFromS3(project.getFileUrls());
        projectRepository.delete(project);
    }

    private void deleteFilesFromS3(List<String> fileUrls) {
        if (fileUrls != null && !fileUrls.isEmpty()) {
            for (String fileUrl : fileUrls) { //
                String fileName = extractFileName(fileUrl);
                amazonS3.deleteObject(new DeleteObjectRequest(BUCKET_NAME, fileName));
            }
        }
    }

    private String extractFileName(String fileUrl) {
        try {
            URI uri = new URI(fileUrl);
            String path = uri.getPath();
            return path.startsWith("/") ? path.substring(1) : path;
        } catch (Exception e) {
            throw new RuntimeException("파일 URL 파싱 오류: " + e.getMessage());
        }
    }
}
