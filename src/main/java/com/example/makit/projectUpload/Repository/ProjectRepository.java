package com.example.makit.projectUpload.Repository;
import com.example.makit.signup.Entity.UserEntity;
import com.example.makit.projectUpload.Entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
    Optional<ProjectEntity> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    void deleteByCreatedAtBefore(LocalDateTime date);
    long countByUser(UserEntity user);
    List<ProjectEntity> findByCreatedAtBefore(LocalDateTime dateTime);

}
