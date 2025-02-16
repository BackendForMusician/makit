package com.example.makit.feedUpload.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FeedUploadRequestDTO {
    private String title;
    private String description;
    private String audioUrl;
    private String imageUrl;
    private List<String> tags;

    public boolean isValidTagCount() {
        return tags != null && tags.size() <= 3;
    }
}
