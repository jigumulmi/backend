package com.jigumulmi.place.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class UpdateReviewRequestDto {

    @Schema(name = "newImage", description = "신규 추가된 이미지 목록", requiredMode = RequiredMode.NOT_REQUIRED)
    private List<MultipartFile> newImageList;
    @Schema(name = "trashImageId", description = "삭제된 이미지 ID 목록", requiredMode = RequiredMode.NOT_REQUIRED)
    private List<Long> trashImageIdList;
    @Schema(requiredMode = RequiredMode.REQUIRED)
    private int rating;
    private String content;

    public UpdateReviewRequestDto(List<MultipartFile> newImage, List<Long> trashImageId,
        int rating, String content) {
        this.newImageList = newImage == null ? new ArrayList<>() : newImage;
        this.trashImageIdList = trashImageId == null ? new ArrayList<>() : trashImageId;
        this.rating = rating;
        this.content = content;
    }
}
