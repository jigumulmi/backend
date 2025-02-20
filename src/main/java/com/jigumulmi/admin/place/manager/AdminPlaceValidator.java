package com.jigumulmi.admin.place.manager;

import com.jigumulmi.admin.place.dto.WeeklyBusinessHourDto;
import com.jigumulmi.admin.place.dto.request.AdminCreatePlaceRequestDto;
import com.jigumulmi.admin.place.dto.response.AdminPlaceBasicResponseDto;
import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.AdminErrorCode;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.dto.ImageDto;
import com.jigumulmi.place.dto.MenuDto;
import com.jigumulmi.place.dto.PositionDto;
import com.jigumulmi.place.dto.response.DistrictResponseDto;
import com.jigumulmi.place.dto.response.PlaceCategoryDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import com.jigumulmi.place.repository.PlaceRepository;
import com.jigumulmi.place.vo.District;
import com.jigumulmi.place.vo.Region;
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
        validatePlaceEssential(placeBasic.getName(), placeBasic.getAddress(),
            placeBasic.getRegion(),
            DistrictResponseDto.toDistrict(placeBasic.getDistrict()));

        PositionDto position = placeBasic.getPosition();
        validatePlacePosition(position);

        List<SubwayStationResponseDto> subwayStationList = placeBasic.getSubwayStationList();
        if (subwayStationList == null || subwayStationList.isEmpty()) {
            throw new CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "지하철 누락");
        }

        List<PlaceCategoryDto> categoryList = placeBasic.getCategoryList();
        validateCategory(categoryList);

        List<ImageDto> placeImage = adminPlaceManager.getPlaceImage(placeId);
        if (placeImage.isEmpty()) {
            throw new CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "이미지 누락");
        }

        List<MenuDto> menuDtoList = adminPlaceManager.getMenu(placeId);
        validateMenu(menuDtoList);

        WeeklyBusinessHourDto fixedBusinessHour = adminPlaceManager.getFixedBusinessHour(placeId);
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            if (fixedBusinessHour.getBusinessHour(dayOfWeek) == null) {
                throw new CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "영업 시간 누락");
            }
        }
    }

    private void validateMenu(List<MenuDto> menuList) {
        if (menuList == null || menuList.isEmpty() || menuList.stream()
            .allMatch(menu -> menu.getName().isBlank())) {
            throw new CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "메뉴 누락");
        }
    }

    private void validateCategory(List<PlaceCategoryDto> categoryList) {
        if (categoryList == null || categoryList.isEmpty() || categoryList.stream()
            .allMatch(dto -> dto.getCategoryGroup() == null)) {
            throw new CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "카테고리 누락");
        }
    }

    private void validatePlacePosition(PositionDto position) {
        if (position.getLatitude() == null || (position.getLatitude() < 33
            || position.getLatitude() > 39)
            || position.getLongitude() == null || (position.getLongitude() < 124
            || position.getLongitude() > 132)) {
            throw new CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "좌표 오류");
        }
    }

    private void validatePlaceEssential(String name, String address, Region region,
        District district) {
        if (name.isBlank() || address.isBlank() || region == null || district == null) {
            throw new CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "기본 정보 누락");
        }
    }

    public void validatePlaceBasicUpdate(Long placeId, AdminCreatePlaceRequestDto requestDto) {
        Place place = placeRepository.findById(placeId)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));

        if (!place.getIsApproved()) {
            return;
        }

        validatePlaceEssential(requestDto.getName(), requestDto.getAddress(),
            requestDto.getRegion(), requestDto.getDistrict());

        if (requestDto.getSubwayStationIdList() == null || requestDto.getSubwayStationIdList()
            .isEmpty()) {
            throw new CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "지하철 누락");
        }

        validatePlacePosition(requestDto.getPosition());

        validateCategory(requestDto.getCategoryList());
    }

    public void validatePlaceImageUpdate(Long placeId, List<ImageDto> imageDtoList) {
        Place place = placeRepository.findById(placeId)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));

        if (!place.getIsApproved()) {
            return;
        }

        if (imageDtoList == null || imageDtoList.isEmpty()) {
            throw new CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "이미지 누락");
        }
    }

}
