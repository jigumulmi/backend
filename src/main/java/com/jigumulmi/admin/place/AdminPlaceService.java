package com.jigumulmi.admin.place;


import com.jigumulmi.admin.place.dto.request.AdminCreatePlaceRequestDto;
import com.jigumulmi.admin.place.dto.request.AdminDeletePlaceRequestDto;
import com.jigumulmi.admin.place.dto.request.AdminGetPlaceListRequestDto;
import com.jigumulmi.admin.place.dto.response.AdminPlaceBasicResponseDto;
import com.jigumulmi.admin.place.dto.response.AdminPlaceListResponseDto;
import com.jigumulmi.admin.place.dto.response.AdminPlaceListResponseDto.PlaceDto;
import com.jigumulmi.admin.place.dto.response.CreatePlaceResponseDto;
import com.jigumulmi.aws.S3Service;
import com.jigumulmi.config.common.PageDto;
import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.place.PlaceService;
import com.jigumulmi.place.domain.Menu;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.domain.PlaceCategoryMapping;
import com.jigumulmi.place.domain.PlaceImage;
import com.jigumulmi.place.domain.ReviewImage;
import com.jigumulmi.place.domain.SubwayStation;
import com.jigumulmi.place.domain.SubwayStationPlace;
import com.jigumulmi.place.dto.ImageDto;
import com.jigumulmi.place.dto.MenuDto;
import com.jigumulmi.place.dto.response.DistrictResponseDto;
import com.jigumulmi.place.dto.response.PlaceCategoryDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto.PositionDto;
import com.jigumulmi.place.repository.MenuRepository;
import com.jigumulmi.place.repository.PlaceImageRepository;
import com.jigumulmi.place.repository.PlaceRepository;
import com.jigumulmi.place.repository.ReviewImageRepository;
import com.jigumulmi.place.repository.SubwayStationRepository;
import com.jigumulmi.place.vo.District;
import com.jigumulmi.place.vo.Region;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;

@Service
@RequiredArgsConstructor
public class AdminPlaceService {

    private final S3Service s3Service;
    private final PlaceService placeService;

    private final AdminCustomPlaceRepository adminCustomPlaceRepository;
    private final PlaceRepository placeRepository;
    private final SubwayStationRepository subwayStationRepository;
    private final MenuRepository menuRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final PlaceImageRepository placeImageRepository;

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
    public AdminPlaceBasicResponseDto getPlaceBasic(Long placeId) {
        return placeRepository.findById(placeId).map(AdminPlaceBasicResponseDto::from)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));
    }

    @Transactional
    public void updatePlaceBasic(Long placeId, AdminCreatePlaceRequestDto requestDto) {

        Place place = placeRepository.findById(placeId)
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

        place.adminBasicUpdate(requestDto, categoryMappingList, subwayStationPlaceList);

        placeRepository.save(place);
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

    @Transactional
    public void updateMenu(Long placeId, List<MenuDto> menuDtoList) {
        Place place = placeRepository.findById(placeId)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));
        menuRepository.deleteAllByPlace(place);

        List<Menu> menuList = new ArrayList<>();
        for (MenuDto menuDto : menuDtoList) {
            String imageS3Key = placeService.MENU_IMAGE_S3_PREFIX + menuDto.getFullFilename();
            menuList.add(
                Menu.builder()
                    .name(menuDto.getName())
                    .place(place)
                    .isMain(menuDto.getIsMain())
                    .price(menuDto.getPrice())
                    .description(menuDto.getDescription())
                    .imageS3Key(imageS3Key)
                    .build()
            );
        }

        menuRepository.saveAll(menuList);
    }

    @Transactional
    public CreatePlaceResponseDto createPlace(AdminCreatePlaceRequestDto requestDto,
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
            .isApproved(requestDto.getIsApproved())
            .kakaoPlaceId(requestDto.getKakaoPlaceId())
            .isFromAdmin(true)
            .member(member)
            .build();

        List<SubwayStation> subwayStationList = subwayStationRepository.findAllById(
            requestDto.getSubwayStationIdList());
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

        Place savedPlace = placeRepository.save(place);
        return CreatePlaceResponseDto.builder().placeId(savedPlace.getId()).build();
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

    public List<Region> getRegionList() {
        return Arrays.stream(Region.values()).toList();
    }

    public List<DistrictResponseDto> getDistrictList(Region region) {
        return region.getDistrictList().stream().sorted(Comparator.comparing(District::getTitle))
            .map(DistrictResponseDto::fromDistrict).toList();
    }
}
