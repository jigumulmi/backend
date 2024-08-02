package com.jigumulmi.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminSavePlaceBasicRequestDto {

    @Schema(description = "장소 ID | 장소 등록 전에 기본 정보 불러오는 경우 null")
    private Long placeId = -1L;
    private String googlePlaceId;

    /**
     * placeId에 대한 setter 메서드 오버라이딩하여 dto 생성 시 관여
     * 명시적으로 null로 넘어오는 경우 -1L로 처리
     * 장소 등록 전 기본 정보 불러오는 경우 장소 생성 후 정보 저장하기 위함
     * @param placeId 장소 ID
     */
    public void setPlaceId(Long placeId) {
        this.placeId = (placeId == null) ? -1L : placeId;
    }
}
