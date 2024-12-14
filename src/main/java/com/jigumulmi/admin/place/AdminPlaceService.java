package com.jigumulmi.admin.place;


import com.jigumulmi.admin.place.dto.request.AdminCreatePlaceRequestDto;
import com.jigumulmi.admin.place.dto.request.AdminCreatePlaceRequestDto.ImageRequestDto;
import com.jigumulmi.admin.place.dto.request.AdminDeletePlaceRequestDto;
import com.jigumulmi.admin.place.dto.request.AdminGetPlaceListRequestDto;
import com.jigumulmi.admin.place.dto.request.AdminUpdatePlaceRequestDto;
import com.jigumulmi.admin.place.dto.response.AdminPlaceDetailResponseDto;
import com.jigumulmi.admin.place.dto.response.AdminPlaceListResponseDto;
import com.jigumulmi.admin.place.dto.response.AdminPlaceListResponseDto.PlaceDto;
import com.jigumulmi.aws.S3Service;
import com.jigumulmi.config.common.PageDto;
import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.place.domain.Menu;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.domain.PlaceCategoryMapping;
import com.jigumulmi.place.domain.PlaceImage;
import com.jigumulmi.place.domain.ReviewImage;
import com.jigumulmi.place.domain.SubwayStation;
import com.jigumulmi.place.domain.SubwayStationPlace;
import com.jigumulmi.place.dto.response.PlaceDetailResponseDto.MenuDto;
import com.jigumulmi.place.dto.response.PlaceDetailResponseDto.OpeningHourDto;
import com.jigumulmi.place.dto.response.PlaceCategoryDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto.PositionDto;
import com.jigumulmi.place.repository.MenuRepository;
import com.jigumulmi.place.repository.PlaceRepository;
import com.jigumulmi.place.repository.ReviewImageRepository;
import com.jigumulmi.place.repository.SubwayStationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class AdminPlaceService {

    private final com.jigumulmi.place.PlaceService placeService;
    private final S3Service s3Service;

    private final AdminCustomPlaceRepository adminCustomPlaceRepository;
    private final PlaceRepository placeRepository;
    private final SubwayStationRepository subwayStationRepository;
    private final MenuRepository menuRepository;
    private final ReviewImageRepository reviewImageRepository;

    @Transactional(readOnly = true)
    public AdminPlaceListResponseDto getPlaceList(Pageable pageable,
        AdminGetPlaceListRequestDto requestDto) {
        Page<Place> placePage = adminCustomPlaceRepository.getPlaceList(pageable,
            requestDto);

        List<PlaceDto> placeDtoList = placePage.getContent().stream()
            .map(PlaceDto::from).collect(Collectors.toList());

        return AdminPlaceListResponseDto.builder()
            .data(placeDtoList)
            .page(PageDto.builder()
                .totalCount(placePage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .totalPage(placePage.getTotalPages())
                .build()
            )
            .build();
    }

    @Transactional(readOnly = true)
    public AdminPlaceDetailResponseDto getPlaceDetail(Long placeId) {
        return placeRepository.findById(placeId).map(AdminPlaceDetailResponseDto::from)
            .orElseThrow(() -> new CustomException(
                CommonErrorCode.RESOURCE_NOT_FOUND));

    }

    @Transactional
    public void createPlace(AdminCreatePlaceRequestDto requestDto, Member member) {
        OpeningHourDto openingHour = requestDto.getOpeningHour();
        PositionDto position = requestDto.getPosition();

        Place place = Place.builder()
            .name(requestDto.getName())
            .address(requestDto.getAddress())
            .contact(requestDto.getContact())
            .openingHourSun(openingHour.getOpeningHourSun())
            .openingHourMon(openingHour.getOpeningHourMon())
            .openingHourTue(openingHour.getOpeningHourTue())
            .openingHourWed(openingHour.getOpeningHourWed())
            .openingHourThu(openingHour.getOpeningHourThu())
            .openingHourFri(openingHour.getOpeningHourFri())
            .openingHourSat(openingHour.getOpeningHourSat())
            .additionalInfo(requestDto.getAdditionalInfo())
            .placeUrl(requestDto.getPlaceUrl())
            .longitude(position.getLongitude())
            .latitude(position.getLatitude())
            .registrantComment(requestDto.getRegistrantComment())
            .isApproved(requestDto.getIsApproved())
            .kakaoPlaceId(requestDto.getKakaoPlaceId())
            .isFromAdmin(true)
            .member(member)
            .build();

        List<SubwayStation> subwayStationList = subwayStationRepository.findAllById(
            requestDto.getSubwayStationIdList());
        ArrayList<SubwayStationPlace> subwayStationPlaceList = new ArrayList<>();
        for (int i = 0; i < subwayStationList.size(); i++) {
            SubwayStation subwayStation = subwayStationList.get(i);

            SubwayStationPlace subwayStationPlace = SubwayStationPlace.builder()
                .isMain(i == 0)
                .subwayStation(subwayStation)
                .place(place)
                .build();

            subwayStationPlaceList.add(subwayStationPlace);
        }

        ArrayList<Menu> menuList = new ArrayList<>();
        for (MenuDto menuDto : requestDto.getMenuList()) {
            String imageS3Key = placeService.MENU_IMAGE_S3_PREFIX + menuDto.getFullFilename();
            Menu menu = Menu.builder()
                .name(menuDto.getName())
                .description(menuDto.getDescription())
                .price(menuDto.getPrice())
                .imageS3Key(imageS3Key)
                .isMain(menuDto.getIsMain())
                .place(place)
                .build();
            menuList.add(menu);
        }

        ArrayList<PlaceImage> imageList = new ArrayList<>();
        for (ImageRequestDto image : requestDto.getImageList()) {
            imageList.add(
                PlaceImage.builder()
                    .url(image.getUrl())
                    .isMain(image.getIsMain())
                    .place(place)
                    .build()
            );
        }

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

        place.addChildren(categoryMappingList, subwayStationPlaceList, menuList, imageList);

        placeRepository.save(place);
    }

    @Transactional
    public void updatePlaceDetail(AdminUpdatePlaceRequestDto requestDto) {

        Place place = placeRepository.findById(requestDto.getPlaceId())
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));

        List<Long> subwayStationIdList = requestDto.getSubwayStationIdList();
        List<SubwayStation> subwayStationList = subwayStationRepository.findAllById(
            subwayStationIdList);

        ArrayList<SubwayStationPlace> subwayStationPlaceList = new ArrayList<>();
        for (SubwayStation subwayStation : subwayStationList) {
            SubwayStationPlace subwayStationPlace = SubwayStationPlace.builder()
                .isMain(
                    subwayStation.getId().equals(subwayStationIdList.getFirst())) // 첫 요소가 메인 지하철역
                .subwayStation(subwayStation)
                .place(place)
                .build();

            subwayStationPlaceList.add(subwayStationPlace);
        }

        ArrayList<Menu> menuList = new ArrayList<>();
        for (MenuDto menuDto : requestDto.getMenuList()) {
            Menu menu = Menu.builder()
                .name(menuDto.getName())
                .description(menuDto.getDescription())
                .price(menuDto.getPrice())
                .imageS3Key(menuDto.getImageS3Key())
                .isMain(menuDto.getIsMain())
                .place(place)
                .build();
            menuList.add(menu);
        }

        ArrayList<PlaceImage> imageList = new ArrayList<>();
        for (ImageRequestDto imageRequestDto : requestDto.getImageList()) {
            imageList.add(
                PlaceImage.builder()
                    .url(imageRequestDto.getUrl())
                    .isMain(imageRequestDto.getIsMain())
                    .place(place)
                    .build()
            );
        }

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

        place.adminUpdate(requestDto, categoryMappingList, subwayStationPlaceList, menuList,
            imageList);

        placeRepository.save(place);
    }

    public void deletePlace(AdminDeletePlaceRequestDto requestDto) {
        Long placeId = requestDto.getPlaceId();
        List<Menu> menuList = menuRepository.findAllByPlaceId(placeId);
        List<ReviewImage> reviewImageList = reviewImageRepository.findAllByReview_Place_IdOrderByCreatedAtDesc(
            placeId);
        try {
            Stream<ObjectIdentifier> menuImageObjectIdentifierList = menuList.stream().map(
                m -> ObjectIdentifier.builder().key(m.getImageS3Key()).build()
            );
            Stream<ObjectIdentifier> reviewImageObjectIdentifierList = reviewImageList.stream().map(
                ri -> ObjectIdentifier.builder().key(ri.getS3Key()).build()
            );

            List<ObjectIdentifier> objectIdentifierList = Stream.concat(
                menuImageObjectIdentifierList, reviewImageObjectIdentifierList).toList();

            s3Service.deleteObjects(s3Service.bucket, objectIdentifierList);
        } catch (SdkException e) {
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

        placeRepository.deleteById(placeId);
    }
}
