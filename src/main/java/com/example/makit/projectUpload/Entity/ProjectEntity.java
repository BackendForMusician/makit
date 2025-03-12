package com.example.makit.projectUpload.Entity;

import com.example.makit.signup.Entity.FieldEntity;
import com.example.makit.signup.Entity.GenreEntity;
import com.example.makit.signup.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    private String title;
    private String description;

    @ManyToMany
    @JoinTable(
            name = "project_fields",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "field_id")
    )
    private Set<FieldEntity> fields;

    @ManyToMany
    @JoinTable(
            name = "project_genres",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<GenreEntity> genres;

    @ElementCollection
    @CollectionTable(name = "project_entity_file_urls", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "file_url", length = 2000)
    private List<String> fileUrls;

    @CreationTimestamp
    private LocalDateTime createdAt;

}

