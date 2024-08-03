package com.jigumulmi.admin.dto.response;

import com.jigumulmi.place.dto.response.PlaceResponseDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Builder
public class AdminPlaceListResponseDto {

    @Getter
    @SuperBuilder
    @NoArgsConstructor
    public static class PlaceDto extends PlaceResponseDto {

        private String category;
        private Boolean isApproved;
        private Boolean isFromAdmin;
        private String googlePlaceId;
    }

    private List<PlaceDto> data;
    private PageDto page;
}
