package com.jigumulmi.admin;


import com.jigumulmi.admin.dto.request.AdminCreatePlaceRequestDto;
import com.jigumulmi.admin.dto.request.AdminCreatePlaceRequestDto.ImageRequestDto;
import com.jigumulmi.admin.dto.request.AdminGetMemberListRequestDto;
import com.jigumulmi.admin.dto.request.AdminGetPlaceListRequestDto;
import com.jigumulmi.admin.dto.request.AdminSavePlaceBasicRequestDto;
import com.jigumulmi.admin.dto.request.AdminUpdatePlaceRequestDto;
import com.jigumulmi.admin.dto.response.AdminMemberListResponseDto;
import com.jigumulmi.admin.dto.response.AdminMemberListResponseDto.MemberDto;
import com.jigumulmi.admin.dto.response.AdminPlaceDetailResponseDto;
import com.jigumulmi.admin.dto.response.AdminPlaceListResponseDto;
import com.jigumulmi.admin.dto.response.AdminPlaceListResponseDto.PlaceDto;
import com.jigumulmi.admin.dto.response.GooglePlaceApiResponseDto;
import com.jigumulmi.admin.dto.response.GooglePlaceApiResponseDto.Location;
import com.jigumulmi.admin.dto.response.KakaoPlaceApiResponseDto;
import com.jigumulmi.admin.dto.response.PageDto;
import com.jigumulmi.admin.repository.CustomAdminRepository;
import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import com.jigumulmi.member.MemberRepository;
import com.jigumulmi.place.domain.Menu;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.domain.PlaceImage;
import com.jigumulmi.place.domain.SubwayStation;
import com.jigumulmi.place.domain.SubwayStationPlace;
import com.jigumulmi.place.dto.response.PlaceDetailResponseDto.OpeningHourDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto.PositionDto;
import com.jigumulmi.place.repository.MenuRepository;
import com.jigumulmi.place.repository.PlaceImageRepository;
import com.jigumulmi.place.repository.PlaceRepository;
import com.jigumulmi.place.repository.SubwayStationPlaceRepository;
import com.jigumulmi.place.repository.SubwayStationRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final int DEFAULT_PAGE_SIZE = 15;

    @Value("${google.api.key}")
    private String GOOGLE_API_KEY;

    @Value("${kakao.admin.key}")
    private String KAKAO_ADMIN_KEY;

    private final CustomAdminRepository customAdminRepository;
    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;
    private final SubwayStationPlaceRepository subwayStationPlaceRepository;
    private final MenuRepository menuRepository;
    private final SubwayStationRepository subwayStationRepository;
    private final PlaceImageRepository placeImageRepository;

    public AdminMemberListResponseDto getMemberList(AdminGetMemberListRequestDto requestDto) {
        Pageable pageable = PageRequest.of(requestDto.getPage() - 1, DEFAULT_PAGE_SIZE,
            Sort.by(requestDto.getDirection(), "id"));

        Page<MemberDto> memberPage = memberRepository.findAll(pageable).map(MemberDto::from);

        return AdminMemberListResponseDto.builder()
            .data(memberPage.getContent())
            .page(PageDto.builder()
                .totalCount(memberPage.getTotalElements())
                .currentPage(requestDto.getPage())
                .totalPage(memberPage.getTotalPages())
                .build())
            .build();
    }

    @Transactional(readOnly = true)
    public AdminPlaceListResponseDto getPlaceList(AdminGetPlaceListRequestDto requestDto) {
        Pageable pageable = PageRequest.of(requestDto.getPage() - 1, DEFAULT_PAGE_SIZE,
            Sort.by(requestDto.getDirection(), "id"));

        Page<PlaceDto> placePage = customAdminRepository.getPlaceList(pageable);

        return AdminPlaceListResponseDto.builder()
            .data(placePage.getContent())
            .page(PageDto.builder()
                .totalCount(placePage.getTotalElements())
                .currentPage(requestDto.getPage())
                .totalPage(placePage.getTotalPages())
                .build())
            .build();
    }

    @Transactional(readOnly = true)
    public AdminPlaceDetailResponseDto getPlaceDetail(Long placeId) {
        return placeRepository.findById(placeId).map(AdminPlaceDetailResponseDto::from)
            .orElseThrow(() -> new CustomException(
                CommonErrorCode.RESOURCE_NOT_FOUND));

    }

    @Transactional
    public void createPlace(AdminCreatePlaceRequestDto requestDto) {
        OpeningHourDto openingHour = requestDto.getOpeningHour();
        PositionDto position = requestDto.getPosition();

        Place place = Place.builder()
            .name(requestDto.getName())
            .category(requestDto.getCategory())
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
        for (String menuName : requestDto.getMenuList()) {
            Menu menu = Menu.builder().name(menuName).place(place).build();
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

        placeRepository.save(place);
        menuRepository.saveAll(menuList);
        placeImageRepository.saveAll(imageList);
        subwayStationPlaceRepository.saveAll(subwayStationPlaceList);
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
        for (String menuName : requestDto.getMenuList()) {
            Menu menu = Menu.builder().name(menuName).place(place).build();
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

        place.adminUpdate(requestDto, subwayStationPlaceList, menuList, imageList);

        subwayStationPlaceRepository.deleteAllByPlaceId(requestDto.getPlaceId());
        subwayStationPlaceRepository.saveAll(subwayStationPlaceList);
        placeRepository.save(place);
    }

    public void savePlaceBasic(AdminSavePlaceBasicRequestDto requestDto) {
        GooglePlaceApiResponseDto googlePlaceApiResponseDto = getDataFromGoogle(requestDto);
        String placeName = googlePlaceApiResponseDto.getDisplayName().getText();
        Location location = googlePlaceApiResponseDto.getLocation();

        KakaoPlaceApiResponseDto kakaoPlaceApiResponseDto = getDataFromKakao(placeName, location);

        Place place = placeRepository.findById(requestDto.getPlaceId())
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));
        place.saveBasic(googlePlaceApiResponseDto, kakaoPlaceApiResponseDto);
        placeRepository.save(place);
    }

    private KakaoPlaceApiResponseDto getDataFromKakao(String placeName, Location location) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + KAKAO_ADMIN_KEY);
        HttpEntity<MultiValueMap<String, String>> requestHeader = new HttpEntity<>(headers);

        String uri = UriComponentsBuilder
            .fromUriString("https://dapi.kakao.com/v2/local/search/keyword.json")
            .queryParam("query", placeName)
            .queryParam("x", location.getLongitude())
            .queryParam("y", location.getLatitude())
            .queryParam("radius", 20) // 단위: 미터
            .queryParam("size", 1)
            .build()
            .toUriString();

        RestTemplate rt = new RestTemplate();
        ResponseEntity<KakaoPlaceApiResponseDto> response = rt.exchange(uri, HttpMethod.GET,
            requestHeader,
            KakaoPlaceApiResponseDto.class);

        return response.getBody();
    }

    private GooglePlaceApiResponseDto getDataFromGoogle(AdminSavePlaceBasicRequestDto requestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Goog-Api-Key", GOOGLE_API_KEY);
        HttpEntity<MultiValueMap<String, String>> requestHeader = new HttpEntity<>(headers);

        String uri = UriComponentsBuilder
            .fromUriString("https://places.googleapis.com")
            .path("/v1/places/" + requestDto.getGooglePlaceId())
            .queryParam("fields",
                "location,regularOpeningHours,displayName")
            .queryParam("languageCode", "ko")
            .queryParam("regionCode", "KR")
            .build()
            .toUriString();

        RestTemplate rt = new RestTemplate();
        ResponseEntity<GooglePlaceApiResponseDto> response = rt.exchange(uri, HttpMethod.GET,
            requestHeader,
            GooglePlaceApiResponseDto.class);

        return response.getBody();
    }
}
