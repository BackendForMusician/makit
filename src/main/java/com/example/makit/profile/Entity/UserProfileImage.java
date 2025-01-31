package com.example.makit.profile.Entity;

import com.example.makit.signup.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "USER_PROFILE_IMAGE")
public class UserProfileImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "URL", nullable = false)
    private String url;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDate createdAt;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
