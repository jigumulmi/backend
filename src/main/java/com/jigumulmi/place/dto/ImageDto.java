package com.jigumulmi.place.dto;

import com.jigumulmi.place.domain.PlaceImage;
import com.jigumulmi.place.dto.response.PlaceResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {

    private String url;
    private Boolean isMain;

    public static ImageDto from(PlaceImage image) {
        return ImageDto.builder()
            .url(image.getUrl())
            .isMain(image.getIsMain())
            .build();
    }
}
