package com.jigumulmi.admin.place;

import com.jigumulmi.admin.place.dto.WeeklyBusinessHourDto;
import com.jigumulmi.admin.place.dto.request.AdminCreatePlaceRequestDto;
import com.jigumulmi.admin.place.dto.request.AdminCreateTemporaryBusinessHourRequestDto;
import com.jigumulmi.admin.place.dto.request.AdminGetPlaceListRequestDto;
import com.jigumulmi.admin.place.dto.response.AdminCreatePlaceResponseDto;
import com.jigumulmi.admin.place.dto.response.AdminPlaceBasicResponseDto;
import com.jigumulmi.admin.place.dto.response.AdminPlaceBusinessHourResponseDto.TemporaryBusinessHourDto;
import com.jigumulmi.admin.place.dto.response.AdminPlaceListResponseDto;
import com.jigumulmi.admin.place.dto.response.AdminPlaceListResponseDto.PlaceDto;
import com.jigumulmi.admin.place.dto.response.AdminS3DeletePresignedUrlResponseDto;
import com.jigumulmi.admin.place.dto.response.AdminS3PutPresignedUrlResponseDto;
import com.jigumulmi.admin.place.repository.SubwayStationPlaceRepository;
import com.jigumulmi.aws.S3Manager;
import com.jigumulmi.common.FileUtils;
import com.jigumulmi.common.PagedResponseDto;
import com.jigumulmi.common.WeekUtils;
import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.AdminErrorCode;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.place.domain.FixedBusinessHour;
import com.jigumulmi.place.domain.Menu;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.domain.PlaceCategoryMapping;
import com.jigumulmi.place.domain.PlaceImage;
import com.jigumulmi.place.domain.SubwayStation;
import com.jigumulmi.place.domain.SubwayStationPlace;
import com.jigumulmi.place.domain.TemporaryBusinessHour;
import com.jigumulmi.place.dto.BusinessHour;
import com.jigumulmi.place.dto.ImageDto;
import com.jigumulmi.place.dto.MenuDto;
import com.jigumulmi.place.dto.PositionDto;
import com.jigumulmi.place.dto.response.DistrictResponseDto;
import com.jigumulmi.place.dto.response.PlaceCategoryDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import com.jigumulmi.place.repository.FixedBusinessHourRepository;
import com.jigumulmi.place.repository.MenuRepository;
import com.jigumulmi.place.repository.PlaceCategoryMappingRepository;
import com.jigumulmi.place.repository.PlaceImageRepository;
import com.jigumulmi.place.repository.PlaceRepository;
import com.jigumulmi.place.repository.SubwayStationRepository;
import com.jigumulmi.place.repository.TemporaryBusinessHourRepository;
import com.jigumulmi.place.vo.District;
import com.jigumulmi.place.vo.Region;
import jakarta.validation.constraints.NotBlank;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;

@Component
@RequiredArgsConstructor
public class AdminPlaceManager {

    private final S3Manager s3Manager;

    private final AdminCustomPlaceRepository adminCustomPlaceRepository;
    private final PlaceRepository placeRepository;
    private final SubwayStationRepository subwayStationRepository;
    private final MenuRepository menuRepository;
    private final PlaceImageRepository placeImageRepository;
    private final FixedBusinessHourRepository fixedBusinessHourRepository;
    private final TemporaryBusinessHourRepository temporaryBusinessHourRepository;
    private final SubwayStationPlaceRepository subwayStationPlaceRepository;
    private final PlaceCategoryMappingRepository placeCategoryMappingRepository;

    @Transactional(readOnly = true)
    public PagedResponseDto<PlaceDto> getPlaceList(Pageable pageable,
        AdminGetPlaceListRequestDto requestDto) {
        Page<PlaceDto> placePage = adminCustomPlaceRepository.getPlaceList(pageable,
            requestDto).map(PlaceDto::from);

        return AdminPlaceListResponseDto.of(placePage, pageable);
    }

    @Transactional(readOnly = true)
    public AdminPlaceBasicResponseDto getPlaceBasic(Long placeId) {
        return placeRepository.findById(placeId).map(AdminPlaceBasicResponseDto::from)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));
    }

    @Transactional
    public void updatePlaceBasic(Long placeId, AdminCreatePlaceRequestDto requestDto) {
        Place place = placeRepository.findById(placeId)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));

        place.adminBasicUpdate(requestDto);

        subwayStationPlaceRepository.deleteAllByPlace(place);
        placeCategoryMappingRepository.deleteAllInBatch(place.getCategoryMappingList());

        List<Long> subwayStationIdList = requestDto.getSubwayStationIdList();
        List<SubwayStation> subwayStationList = subwayStationRepository.findAllById(
                subwayStationIdList)
            .stream()
            .sorted(
                Comparator.comparingInt(station -> subwayStationIdList.indexOf(station.getId())))
            .toList();

        List<SubwayStationPlace> subwayStationPlaceList = IntStream.range(0,
                subwayStationList.size())
            .mapToObj(i -> SubwayStationPlace.builder()
                .subwayStation(subwayStationList.get(i))
                .place(place)
                .isMain(i == 0)
                .build()
            )
            .collect(Collectors.toList());

        ArrayList<PlaceCategoryMapping> categoryMappingList = new ArrayList<>();
        for (PlaceCategoryDto categoryRequestDto : requestDto.getCategoryList()) {
            categoryMappingList.add(
                PlaceCategoryMapping.builder()
                    .category(categoryRequestDto.getCategory())
                    .categoryGroup(categoryRequestDto.getCategoryGroup())
                    .place(place)
                    .build()
            );
        }

        subwayStationPlaceRepository.saveAll(subwayStationPlaceList);
        placeCategoryMappingRepository.saveAll(categoryMappingList);
    }

    public List<ImageDto> getPlaceImage(Long placeId) {
        return placeImageRepository.findByPlace_Id(placeId).stream()
            .map(ImageDto::from).toList();
    }

    @Transactional
    public void updatePlaceImage(Long placeId, List<ImageDto> imageDtoList) {
        Place place = placeRepository.findById(placeId)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));
        placeImageRepository.deleteAllByPlace(place);

        List<PlaceImage> placeImageList = new ArrayList<>();
        for (ImageDto imageDto : imageDtoList) {
            placeImageList.add(
                PlaceImage.builder()
                    .url(imageDto.getUrl())
                    .isMain(imageDto.getIsMain())
                    .place(place)
                    .build()
            );
        }

        placeImageRepository.saveAll(placeImageList);
    }

    public List<MenuDto> getMenu(Long placeId) {
        return menuRepository.findAllByPlaceId(placeId).stream()
            .map(MenuDto::from).toList();
    }

    @Transactional
    public void updateMenu(Long placeId, List<MenuDto> menuDtoList) {
        Place place = placeRepository.findById(placeId)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));
        menuRepository.deleteAllByPlace(place);

        List<Menu> menuList = new ArrayList<>();
        for (MenuDto menuDto : menuDtoList) {
            menuList.add(MenuDto.toMenu(menuDto, place));
        }

        menuRepository.saveAll(menuList);
    }

    @Transactional
    public void updateFixedBusinessHour(Long placeId, WeeklyBusinessHourDto requestDto) {
        Place place = placeRepository.findById(placeId)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));
        fixedBusinessHourRepository.deleteAllByPlace(place);

        List<FixedBusinessHour> businessHourList = new ArrayList<>();
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            BusinessHour businessHour = requestDto.getBusinessHour(dayOfWeek);
            businessHourList.add(
                FixedBusinessHour.builder()
                    .place(place)
                    .dayOfWeek(dayOfWeek)
                    .openTime(businessHour.getOpenTime())
                    .closeTime(businessHour.getCloseTime())
                    .breakStart(businessHour.getBreakStart())
                    .breakEnd(businessHour.getBreakEnd())
                    .isDayOff(businessHour.getIsDayOff())
                    .build()
            );
        }

        fixedBusinessHourRepository.saveAll(businessHourList);
    }

    public void createTemporaryBusinessHour(Long placeId,
        AdminCreateTemporaryBusinessHourRequestDto requestDto) {
        Place place = placeRepository.findById(placeId)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));

        BusinessHour businessHour = requestDto.getBusinessHour();

        LocalDate date = requestDto.getDate();
        int weekOfYear = WeekUtils.getWeekOfYear(date);

        TemporaryBusinessHour temporaryBusinessHour = TemporaryBusinessHour.builder()
            .place(place)
            .month(date.getMonthValue())
            .weekOfYear(weekOfYear)
            .date(date)
            .dayOfWeek(date.getDayOfWeek())
            .openTime(businessHour.getOpenTime())
            .closeTime(businessHour.getCloseTime())
            .breakStart(businessHour.getBreakStart())
            .breakEnd(businessHour.getBreakEnd())
            .isDayOff(businessHour.getIsDayOff())
            .build();

        temporaryBusinessHourRepository.save(temporaryBusinessHour);
    }

    @Transactional
    public void updateTemporaryBusinessHour(Long hourId,
        AdminCreateTemporaryBusinessHourRequestDto requestDto) {
        TemporaryBusinessHour temporaryBusinessHour = temporaryBusinessHourRepository.findById(
            hourId).orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));

        temporaryBusinessHour.adminUpdate(requestDto);
    }

    public void deleteTemporaryBusinessHour(Long hourId) {
        temporaryBusinessHourRepository.deleteById(hourId);
    }

    public WeeklyBusinessHourDto getFixedBusinessHour(Long placeId) {
        List<FixedBusinessHour> fixedBusinessHourList = fixedBusinessHourRepository.findAllByPlaceId(
            placeId);

        WeeklyBusinessHourDto fixedBusinessHourResponseDto = new WeeklyBusinessHourDto();
        for (FixedBusinessHour fixedBusinessHour : fixedBusinessHourList) {
            fixedBusinessHourResponseDto.updateBusinessHour(
                fixedBusinessHour.getDayOfWeek(),
                BusinessHour.fromFixedBusinessHour(fixedBusinessHour));
        }
        return fixedBusinessHourResponseDto;
    }

    public List<TemporaryBusinessHourDto> getTemporaryBusinessHour(Long placeId,
        Integer month) {
        List<TemporaryBusinessHour> tempBusinessHourList = temporaryBusinessHourRepository.findAllByPlaceIdAndMonth(
            placeId, month);

        List<TemporaryBusinessHourDto> tempBusinessHourResponseDto = new ArrayList<>();
        for (TemporaryBusinessHour temporaryBusinessHour : tempBusinessHourList) {
            tempBusinessHourResponseDto.add(
                TemporaryBusinessHourDto.builder()
                    .id(temporaryBusinessHour.getId())
                    .date(temporaryBusinessHour.getDate())
                    .businessHour(BusinessHour.fromTemporaryBusinessHour(temporaryBusinessHour))
                    .build()
            );
        }
        return tempBusinessHourResponseDto;
    }

    @Transactional
    public AdminCreatePlaceResponseDto createPlace(AdminCreatePlaceRequestDto requestDto,
        Member member) {
        PositionDto position = requestDto.getPosition();

        Place place = Place.builder()
            .name(requestDto.getName())
            .region(requestDto.getRegion())
            .district(requestDto.getDistrict())
            .address(requestDto.getAddress())
            .contact(requestDto.getContact())
            .additionalInfo(requestDto.getAdditionalInfo())
            .placeUrl(requestDto.getPlaceUrl())
            .longitude(position.getLongitude())
            .latitude(position.getLatitude())
            .registrantComment(requestDto.getRegistrantComment())
            .isApproved(false)
            .kakaoPlaceId(requestDto.getKakaoPlaceId())
            .isFromAdmin(true)
            .member(member)
            .build();

        List<Long> subwayStationIdList = requestDto.getSubwayStationIdList();
        List<SubwayStation> subwayStationList = subwayStationRepository.findAllById(
                subwayStationIdList)
            .stream()
            .sorted(
                Comparator.comparingInt(station -> subwayStationIdList.indexOf(station.getId())))
            .toList();

        List<SubwayStationPlace> subwayStationPlaceList = IntStream.range(0,
                subwayStationList.size())
            .mapToObj(i -> SubwayStationPlace.builder()
                .subwayStation(subwayStationList.get(i))
                .place(place)
                .isMain(i == 0)
                .build()
            )
            .collect(Collectors.toList());

        List<PlaceCategoryMapping> categoryMappingList = new ArrayList<>();
        for (PlaceCategoryDto categoryRequestDto : requestDto.getCategoryList()) {
            categoryMappingList.add(
                PlaceCategoryMapping.builder()
                    .category(categoryRequestDto.getCategory())
                    .categoryGroup(categoryRequestDto.getCategoryGroup())
                    .place(place)
                    .build()
            );
        }

        place.addCategoryAndSubwayStation(categoryMappingList, subwayStationPlaceList);

        placeRepository.save(place);
        return AdminCreatePlaceResponseDto.builder().placeId(place.getId()).build();
    }

    @Transactional(readOnly = true)
    public void validatePlaceApprovalIfNeeded(Long placeId, boolean approve) {
        if (!approve) {
            return;
        }

        AdminPlaceBasicResponseDto placeBasic = getPlaceBasic(placeId);
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

        List<ImageDto> placeImage = getPlaceImage(placeId);
        if (placeImage.isEmpty()) {
            throw new CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "이미지 누락");
        }

        List<MenuDto> menuDtoList = getMenu(placeId);
        if (menuDtoList.isEmpty()) {
            throw new CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "메뉴 누락");
        }

        WeeklyBusinessHourDto fixedBusinessHour = getFixedBusinessHour(placeId);
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            if (fixedBusinessHour.getBusinessHour(dayOfWeek) == null) {
                throw new CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "영업 시간 누락");
            }
        }
    }

    @Transactional
    public void togglePlaceApprove(Long placeId, boolean approve) {
        Place place = placeRepository.findById(placeId)
            .orElseThrow(() -> new CustomException((CommonErrorCode.RESOURCE_NOT_FOUND)));

        place.adminUpdateIsApproved(approve);
    }

    public void deletePlace(Long placeId) {
        placeRepository.deleteById(placeId);
    }

    public void deleteMenuImageFileList(Long placeId) {
        try {
            List<Menu> menuList = menuRepository.findAllByPlaceId(placeId);
            List<ObjectIdentifier> objectIdentifierList = menuList.stream().map(
                m -> ObjectIdentifier.builder().key(m.getImageS3Key()).build()
            ).toList();

            s3Manager.deleteObjects(s3Manager.bucket, objectIdentifierList);
        } catch (SdkException e) {
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public List<DistrictResponseDto> getDistrictListOrderByName(Region region) {
        return region.getDistrictList().stream().sorted(Comparator.comparing(District::getTitle))
            .map(DistrictResponseDto::fromDistrict).toList();
    }

    public AdminS3PutPresignedUrlResponseDto createMenuImageS3PutPresignedUrl(long placeId) {
        String filename = FileUtils.generateUniqueFilename();
        String s3Key = S3Manager.MENU_IMAGE_S3_PREFIX + placeId + "/" + filename;

        String url = s3Manager.generatePutObjectPresignedUrl(s3Manager.bucket, s3Key);
        return AdminS3PutPresignedUrlResponseDto.builder()
            .url(url)
            .filename(filename)
            .build();
    }

    public AdminS3DeletePresignedUrlResponseDto createMenuImageS3DeletePresignedUrl(
        @NotBlank String s3Key) {
        String url = s3Manager.generateDeleteObjectPresignedUrl(s3Manager.bucket, s3Key);
        return AdminS3DeletePresignedUrlResponseDto.builder()
            .url(url)
            .build();
    }

    public void validatePlaceModification(Long placeId) {
        Place place = placeRepository.findById(placeId)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));

        if (place.getIsApproved()) {
            throw new CustomException(AdminErrorCode.INVALID_PLACE_MODIFICATION);
        }
    }
}
