//package com.example.makit.Scheduler;
//
//import com.example.makit.projectUpload.Repository.ProjectRepository;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//
//@Component
//public class ProjectCleanupScheduler {
//
//    private final ProjectRepository projectRepository;
//
//    public ProjectCleanupScheduler(ProjectRepository projectRepository) {
//        this.projectRepository = projectRepository;
//    }
//
//    @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정 실행
//    public void deleteOldProjects() {
//        LocalDateTime twoWeeksAgo = LocalDateTime.now().minus(14, ChronoUnit.DAYS);
//        projectRepository.deleteByCreatedAtBefore(twoWeeksAgo);
//        System.out.println("2주 지난 프로젝트 삭제 완료!");
//    }
//}
package com.example.makit.Scheduler;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.example.makit.projectUpload.Entity.ProjectEntity;
import com.example.makit.projectUpload.Repository.ProjectRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class ProjectCleanupScheduler {

    private final ProjectRepository projectRepository;
    private final AmazonS3 amazonS3;
    private static final String BUCKET_NAME = "my-audio-image-bucket";

    public ProjectCleanupScheduler(ProjectRepository projectRepository, AmazonS3 amazonS3) {
        this.projectRepository = projectRepository;
        this.amazonS3 = amazonS3;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteOldProjects() {
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minus(14, ChronoUnit.DAYS);
        List<ProjectEntity> oldProjects = projectRepository.findByCreatedAtBefore(twoWeeksAgo);

        for (ProjectEntity project : oldProjects) {
            deleteFilesFromS3(project.getFileUrls());
            projectRepository.delete(project);
        }

        System.out.println("2주 지난 프로젝트 및 관련 파일 삭제");
    }

    private void deleteFilesFromS3(List<String> fileUrls) {
        for (String fileUrl : fileUrls) {
            String fileName = fileUrl.replace("https://my-audio-image-bucket.s3.amazonaws.com/", "");
            amazonS3.deleteObject(new DeleteObjectRequest(BUCKET_NAME, fileName));
        }
    }
}
