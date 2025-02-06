package com.example.makit.feedUpload.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FeedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String audioUrl;
    private String imageUrl;

    @Lob
    private String tags;  // 태그는 문자열로 저장 (콤마로 구분)
}
