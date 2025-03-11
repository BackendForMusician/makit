package com.example.makit.projectUpload.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.makit.exception.FieldNotFoundException;
import com.example.makit.exception.GenreNotFoundException;
import com.example.makit.projectUpload.DTO.ProjectUploadRequestDTO;
import com.example.makit.projectUpload.DTO.ProjectUploadResponseDTO;
import com.example.makit.projectUpload.Entity.ProjectEntity;
import com.example.makit.signup.Entity.FieldEntity;
import com.example.makit.signup.Entity.GenreEntity;
import com.example.makit.projectUpload.Repository.ProjectRepository;
import com.example.makit.signup.Repository.FieldRepository;
import com.example.makit.signup.Repository.GenreRepository;
import com.example.makit.signup.Entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.makit.signup.Repository.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectUploadService {

    private final AmazonS3 amazonS3;
    private final ProjectRepository projectRepository;
    private final FieldRepository fieldRepository;
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;
    private static final long MAX_TOTAL_SIZE = 250 * 1024 * 1024;
    private static final int MAX_FILE_COUNT = 5;
    private static final String BUCKET_NAME = "my-audio-image-bucket";

    public ProjectUploadService(AmazonS3 amazonS3, ProjectRepository projectRepository,
                                FieldRepository fieldRepository, GenreRepository genreRepository, UserRepository userRepository) {
        this.amazonS3 = amazonS3;
        this.projectRepository = projectRepository;
        this.fieldRepository = fieldRepository;
        this.genreRepository = genreRepository;
        this.userRepository = userRepository;;
    }

    @Transactional
    public ProjectUploadResponseDTO saveProject(ProjectUploadRequestDTO request, UserEntity loginMember, List<MultipartFile> files) {
        if (!request.isValidFieldCount() || !request.isValidGenreCount()) {
            return new ProjectUploadResponseDTO(false, "필수 항목이 누락되었거나 최대 개수를 초과했습니다.");
        }

        if (files == null || files.isEmpty()) {
            return new ProjectUploadResponseDTO(false, "최소 1개의 파일을 업로드해야 합니다.");
        }
        if (files.size() > MAX_FILE_COUNT) {
            return new ProjectUploadResponseDTO(false, "최대 5개의 파일만 업로드할 수 있습니다.");
        }

        if (request.getTitle() == null || request.getTitle().length() < 1 || request.getTitle().length() > 20) {
            return new ProjectUploadResponseDTO(false, "제목은 최소 1자 이상, 최대 20자 이하로 입력해야 합니다.");
        }

        if (request.getDescription() == null || request.getDescription().length() < 1 || request.getDescription().length() > 500) {
            return new ProjectUploadResponseDTO(false, "설명은 최소 1자 이상, 최대 500자 이하로 입력해야 합니다.");
        }

        UserEntity updatedUser = userRepository.findById(loginMember.getId())
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        if (!updatedUser.isRegistration()) {
            long existingProjectCount = projectRepository.countByUser(updatedUser);
            if (existingProjectCount > 0) {
                return new ProjectUploadResponseDTO(false, "구독하지 않은 사용자는 하나의 프로젝트만 업로드할 수 있습니다.");
            }
        }

        ProjectEntity project = new ProjectEntity();
        project.setUser(updatedUser);
        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());

        Set<FieldEntity> fields = request.getFields().stream()
                .map(fieldName -> fieldRepository.findByFieldName(fieldName)
                        .orElseThrow(() -> new FieldNotFoundException(fieldName)))
                .collect(Collectors.toSet());
        project.setFields(fields);

        Set<GenreEntity> genres = request.getGenres().stream()
                .map(genreName -> genreRepository.findByGenreName(genreName)
                        .orElseThrow(() -> new GenreNotFoundException(genreName)))
                .collect(Collectors.toSet());
        project.setGenres(genres);

        List<String> fileUrls = files.stream()
                .map(file -> uploadFileToS3(file, "project"))
                .collect(Collectors.toList());

        project.setFileUrls(fileUrls);
        projectRepository.save(project);

        return new ProjectUploadResponseDTO(true, "프로젝트가 성공적으로 업로드되었습니다.");
    }


    private String uploadFileToS3(MultipartFile file, String folder) {
        String fileName = folder + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(BUCKET_NAME, fileName, inputStream, metadata);
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }

        return amazonS3.getUrl(BUCKET_NAME, fileName).toString();
    }
}
