package com.example.makit.feedUpload.Entity;

import com.example.makit.signup.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class FeedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String title;
    private String description;
    @Column(length = 2000)
    private String audioUrl;
    @Column(length = 2000)
    private String imageUrl;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FeedTag> feedTags= new HashSet<>();
}
