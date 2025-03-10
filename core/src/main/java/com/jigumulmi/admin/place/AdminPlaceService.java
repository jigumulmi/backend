package com.jigumulmi.admin.place;


import com.jigumulmi.admin.place.dto.WeeklyBusinessHourDto;
import com.jigumulmi.admin.place.dto.request.AdminCreatePlaceRequestDto;
import com.jigumulmi.admin.place.dto.request.AdminCreateTemporaryBusinessHourRequestDto;
import com.jigumulmi.admin.place.dto.request.AdminGetPlaceListRequestDto;
import com.jigumulmi.admin.place.dto.response.AdminCreatePlaceResponseDto;
import com.jigumulmi.admin.place.dto.response.AdminPlaceBasicResponseDto;
import com.jigumulmi.admin.place.dto.response.AdminPlaceBusinessHourResponseDto;
import com.jigumulmi.admin.place.dto.response.AdminPlaceBusinessHourResponseDto.TemporaryBusinessHourDto;
import com.jigumulmi.admin.place.dto.response.AdminPlaceListResponseDto.PlaceDto;
import com.jigumulmi.admin.place.dto.response.AdminS3DeletePresignedUrlResponseDto;
import com.jigumulmi.admin.place.dto.response.AdminS3PutPresignedUrlResponseDto;
import com.jigumulmi.admin.place.manager.AdminPlaceManager;
import com.jigumulmi.admin.place.manager.AdminPlaceValidator;
import com.jigumulmi.common.PagedResponseDto;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.place.dto.ImageDto;
import com.jigumulmi.place.dto.MenuDto;
import com.jigumulmi.place.dto.request.MenuImageS3DeletePresignedUrlRequestDto;
import com.jigumulmi.place.dto.request.MenuImageS3PutPresignedUrlRequestDto;
import com.jigumulmi.place.dto.response.DistrictResponseDto;
import com.jigumulmi.place.vo.Region;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminPlaceService {

    private final AdminPlaceManager adminPlaceManager;
    private final AdminPlaceValidator adminPlaceValidator;

    public PagedResponseDto<PlaceDto> getPlaceList(Pageable pageable,
        AdminGetPlaceListRequestDto requestDto) {
        return adminPlaceManager.getPlaceList(pageable, requestDto);
    }

    public AdminPlaceBasicResponseDto getPlaceBasic(Long placeId) {
        return adminPlaceManager.getPlaceBasic(placeId);
    }

    @Transactional
    public void updatePlaceBasic(Long placeId, AdminCreatePlaceRequestDto requestDto) {
        adminPlaceValidator.validatePlaceBasicUpdate(placeId, requestDto);
        adminPlaceManager.updatePlaceBasic(placeId, requestDto);
    }

    public List<ImageDto> getPlaceImage(Long placeId) {
        return adminPlaceManager.getPlaceImage(placeId);
    }

    @Transactional
    public void updatePlaceImage(Long placeId, List<ImageDto> imageDtoList) {
        adminPlaceValidator.validatePlaceImageUpdate(placeId, imageDtoList);
        adminPlaceManager.updatePlaceImage(placeId, imageDtoList);
    }

    public List<MenuDto> getMenu(Long placeId) {
        return adminPlaceManager.getMenu(placeId);
    }

    @Transactional
    public void updateMenu(Long placeId, List<MenuDto> menuDtoList) {
        adminPlaceValidator.validateMenuUpdate(placeId, menuDtoList);
        adminPlaceManager.updateMenu(placeId, menuDtoList);
    }

    public void updateFixedBusinessHour(Long placeId, WeeklyBusinessHourDto requestDto) {
        adminPlaceManager.updateFixedBusinessHour(placeId, requestDto);
    }

    public void createTemporaryBusinessHour(Long placeId,
        AdminCreateTemporaryBusinessHourRequestDto requestDto) {
        adminPlaceManager.createTemporaryBusinessHour(placeId, requestDto);
    }

    public void updateTemporaryBusinessHour(Long hourId,
        AdminCreateTemporaryBusinessHourRequestDto requestDto) {
        adminPlaceManager.updateTemporaryBusinessHour(hourId, requestDto);
    }

    public void deleteTemporaryBusinessHour(Long hourId) {
        adminPlaceManager.deleteTemporaryBusinessHour(hourId);
    }

    public AdminPlaceBusinessHourResponseDto getPlaceBusinessHour(Long placeId, Integer month) {
        WeeklyBusinessHourDto fixedBusinessHourResponseDto = adminPlaceManager.getFixedBusinessHour(
            placeId);
        List<TemporaryBusinessHourDto> tempBusinessHourResponseDto = adminPlaceManager.getTemporaryBusinessHour(
            placeId, month);

        return AdminPlaceBusinessHourResponseDto.from(fixedBusinessHourResponseDto,
            tempBusinessHourResponseDto);
    }

    public AdminCreatePlaceResponseDto createPlace(AdminCreatePlaceRequestDto requestDto,
        Member member) {
        return adminPlaceManager.createPlace(requestDto, member);
    }

    @Transactional
    public void togglePlaceApprove(Long placeId, boolean approve) {
        adminPlaceValidator.validatePlaceApprovalIfNeeded(placeId, approve);
        adminPlaceManager.togglePlaceApprove(placeId, approve);
    }

    public void deletePlace(Long placeId) {
        adminPlaceValidator.validatePlaceRemoval(placeId);
        adminPlaceManager.deletePlace(placeId);
        adminPlaceManager.deleteMenuImageFileList(placeId);
    }

    public List<Region> getRegionList() {
        return Arrays.stream(Region.values()).toList();
    }

    public List<DistrictResponseDto> getDistrictList(Region region) {
        return adminPlaceManager.getDistrictListOrderByName(region);
    }

    public AdminS3PutPresignedUrlResponseDto createMenuImageS3PutPresignedUrl(
        MenuImageS3PutPresignedUrlRequestDto requestDto) {
        return adminPlaceManager.createMenuImageS3PutPresignedUrl(requestDto.getPlaceId());
    }

    public AdminS3DeletePresignedUrlResponseDto createMenuImageS3DeletePresignedUrl(
        MenuImageS3DeletePresignedUrlRequestDto requestDto) {
        return adminPlaceManager.createMenuImageS3DeletePresignedUrl(requestDto.getS3Key());
    }
}
