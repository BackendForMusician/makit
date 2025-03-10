package com.example.makit.projectUpload.DTO;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ProjectUploadRequestDTO {
    private String title;
    private List<String> genres;
    private List<String> fields;
    private String description;
    private boolean isMusician;

    public boolean isValidFieldCount() {
        return fields != null && fields.size() <= 3;
    }

    public boolean isValidGenreCount() {
        return genres != null && genres.size() <= 3;
    }
}
