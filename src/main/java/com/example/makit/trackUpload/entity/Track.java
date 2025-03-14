package com.example.makit.trackUpload.entity;

import com.example.makit.signup.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    private String title;
    private String lyrics;
    private String description;
    @Column(length = 2000)
    private String audioUrl;
    @Column(length = 2000)
    private String imageUrl;


    @OneToMany(mappedBy = "track")
    private Set<TrackGenre> trackGenres = new HashSet<>();

    @OneToMany(mappedBy = "track")
    private Set<TrackTag> trackTags = new HashSet<>();
}
