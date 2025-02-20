package com.jigumulmi.admin.place.manager;

import com.jigumulmi.admin.place.dto.WeeklyBusinessHourDto;
import com.jigumulmi.admin.place.dto.response.AdminPlaceBasicResponseDto;
import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.AdminErrorCode;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.dto.ImageDto;
import com.jigumulmi.place.dto.MenuDto;
import com.jigumulmi.place.dto.PositionDto;
import com.jigumulmi.place.dto.response.PlaceCategoryDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import com.jigumulmi.place.repository.PlaceRepository;
import java.time.DayOfWeek;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminPlaceValidator {

    private final AdminPlaceManager adminPlaceManager;

    private final PlaceRepository placeRepository;

    public void validatePlaceRemoval(Long placeId) {
        Place place = placeRepository.findById(placeId)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));

        if (place.getIsApproved()) {
            throw new CustomException(AdminErrorCode.INVALID_PLACE_REMOVAL);
        }
    }

    @Transactional(readOnly = true)
    public void validatePlaceApprovalIfNeeded(Long placeId, boolean approve) {
        if (!approve) {
            return;
        }

        AdminPlaceBasicResponseDto placeBasic = adminPlaceManager.getPlaceBasic(placeId);
        if (placeBasic.getName().isBlank() || placeBasic.getAddress().isBlank()
            || placeBasic.getRegion() == null || placeBasic.getDistrict() == null) {
            throw new CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "기본 정보 누락");
        }

        PositionDto position = placeBasic.getPosition();
        if (position.getLatitude() == null || (position.getLatitude() < 33
            || position.getLatitude() > 39)
            || position.getLongitude() == null || (position.getLongitude() < 124
            || position.getLongitude() > 132)) {
            throw new CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "좌표 오류");
        }

        List<SubwayStationResponseDto> subwayStationList = placeBasic.getSubwayStationList();
        if (subwayStationList == null || subwayStationList.isEmpty()) {
            throw new CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "지하철 누락");
        }

        List<PlaceCategoryDto> categoryList = placeBasic.getCategoryList();
        if (categoryList == null || categoryList.isEmpty()) {
            throw new CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "카테고리 누락");
        }

        List<ImageDto> placeImage = adminPlaceManager.getPlaceImage(placeId);
        if (placeImage.isEmpty()) {
            throw new CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "이미지 누락");
        }

        List<MenuDto> menuDtoList = adminPlaceManager.getMenu(placeId);
        if (menuDtoList.isEmpty()) {
            throw new CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "메뉴 누락");
        }

        WeeklyBusinessHourDto fixedBusinessHour = adminPlaceManager.getFixedBusinessHour(placeId);
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            if (fixedBusinessHour.getBusinessHour(dayOfWeek) == null) {
                throw new CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "영업 시간 누락");
            }
        }
    }


}
