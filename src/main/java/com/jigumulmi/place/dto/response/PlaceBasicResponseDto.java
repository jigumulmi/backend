package com.jigumulmi.place.dto.response;

import com.jigumulmi.place.dto.ImageDto;
import com.jigumulmi.place.dto.PositionDto;
import com.jigumulmi.place.vo.LiveOpeningStatus;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceBasicResponseDto {

    private Long id;
    private String name;
    private PositionDto position;
    private String address;
    private String contact;
    private String additionalInfo;
    private SubwayStationResponseDto subwayStation;

    @Setter
    private List<ImageDto> imageList;

    @Setter
    private List<PlaceCategoryDto> categoryList;

    @Setter
    private LiveOpeningStatus liveOpeningStatus;
}
