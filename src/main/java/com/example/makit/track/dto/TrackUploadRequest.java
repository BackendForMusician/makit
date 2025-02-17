package com.example.makit.track.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class TrackUploadRequest {

    @NotBlank(message = "제목은 필수 항목입니다.")
    @Size(min = 1, max = 20, message = "제목은 1자 이상 20자 이하이어야 합니다.")
    private String title;

    @NotBlank(message = "가사 및 생각은 필수 항목입니다.")
    @Size(min = 1, max = 500, message = "작성글은 1자 이상 500자 이하이어야 합니다.")
    private String description;

    @NotNull(message = "장르는 필수 항목입니다.")
    @Size(min = 1, max = 3, message = "장르는 최소 1개 이상 최대 3개까지 선택해야 합니다.")
    private List<Long> genreIds;

    // TODO 준서 리포지토리 가져와서 Tag 엔티티 삽입하기
    // 태그는 선택사항이므로 별도의 유효성 검사는 생략할 수 있음 (필요시 @Size(max=3) 등 추가 가능)
    private List<Long> tagIds;


}