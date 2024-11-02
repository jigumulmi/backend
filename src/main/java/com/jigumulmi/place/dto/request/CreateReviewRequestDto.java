package com.jigumulmi.place.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class CreateReviewRequestDto {
    @Schema(name = "image", requiredMode = RequiredMode.NOT_REQUIRED)
    @NotNull
    private List<MultipartFile> imageList;

    @NotNull
    private Long placeId;
    @NotNull
    private Integer rating;
    private String content;

    public CreateReviewRequestDto(List<MultipartFile> image, Long placeId, Integer rating,
        String content) {
        this.imageList = image == null ? new ArrayList<>() : image;
        this.placeId = placeId;
        this.rating = rating;
        this.content = content;
    }
}
