package com.example.makit.track.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TrackUploadRequestDTO {

    @NotBlank(message = "제목은 필수 항목입니다.")
    @Size(min = 1, max = 20, message = "제목은 1자 이상 20자 이하이어야 합니다.")
    private String title;

    @NotBlank(message = "가사는 필수 항목입니다.")
    @Size(min = 1, max = 500, message = "작성글은 1자 이상 500자 이하이어야 합니다.")
    private String lyrics;

    @NotBlank(message = "코멘트는 필수 항목입니다.")
    @Size(min = 1, max = 500, message = "작성글은 1자 이상 500자 이하이어야 합니다.")
    private String description;

    @NotNull(message = "장르는 필수 항목입니다.")
    @Size(min = 1, max = 3, message = "장르는 최소 1개 이상 최대 3개까지 선택해야 합니다.")
    private List<String> genres;

    private List<String> tags;


}