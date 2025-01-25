package com.jigumulmi.place.dto.response;

import com.jigumulmi.place.vo.District;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DistrictResponseDto {

    private Integer id;
    private String title;

    public static DistrictResponseDto fromDistrict(District district) {
        if (district == null) {
            return null;
        }

        return DistrictResponseDto.builder()
            .id(district.getId())
            .title(district.getTitle())
            .build();
    }

}
