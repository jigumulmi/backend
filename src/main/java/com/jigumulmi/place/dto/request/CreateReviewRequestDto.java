package com.jigumulmi.place.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class CreateReviewRequestDto {

    @Schema(name = "image", description = "리뷰 이미지 목록", requiredMode = RequiredMode.NOT_REQUIRED)
    private List<MultipartFile> imageList;
    @Schema(requiredMode = RequiredMode.REQUIRED)
    private int rating;
    private String content;

    public CreateReviewRequestDto(List<MultipartFile> image, int rating,
        String content) {
        this.imageList = image == null ? new ArrayList<>() : image;
        this.rating = rating;
        this.content = content;
    }
}
