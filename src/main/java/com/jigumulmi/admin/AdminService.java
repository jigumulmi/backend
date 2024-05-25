package com.jigumulmi.admin;


import com.jigumulmi.admin.dto.request.AdminCreatePlaceRequestDto;
import com.jigumulmi.admin.dto.request.AdminGetMemberListRequestDto;
import com.jigumulmi.admin.dto.request.AdminGetPlaceListRequestDto;
import com.jigumulmi.admin.dto.request.AdminUpdatePlaceRequestDto;
import com.jigumulmi.admin.dto.response.AdminMemberListResponseDto;
import com.jigumulmi.admin.dto.response.AdminMemberListResponseDto.MemberDto;
import com.jigumulmi.admin.dto.response.AdminPlaceListResponseDto;
import com.jigumulmi.admin.dto.response.AdminPlaceListResponseDto.PlaceDto;
import com.jigumulmi.admin.dto.response.PageDto;
import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import com.jigumulmi.member.MemberRepository;
import com.jigumulmi.place.domain.Menu;
import com.jigumulmi.place.domain.Restaurant;
import com.jigumulmi.place.domain.SubwayStation;
import com.jigumulmi.place.domain.SubwayStationPlace;
import com.jigumulmi.place.dto.response.RestaurantDetailResponseDto.OpeningHourDto;
import com.jigumulmi.place.dto.response.RestaurantResponseDto.PositionDto;
import com.jigumulmi.place.repository.MenuRepository;
import com.jigumulmi.place.repository.RestaurantRepository;
import com.jigumulmi.place.repository.SubwayStationPlaceRepository;
import com.jigumulmi.place.repository.SubwayStationRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final int DEFAULT_PAGE_SIZE = 15;

    private final MemberRepository memberRepository;
    private final RestaurantRepository restaurantRepository;
    private final SubwayStationPlaceRepository subwayStationPlaceRepository;
    private final MenuRepository menuRepository;
    private final SubwayStationRepository subwayStationRepository;

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

        Page<PlaceDto> placePage = restaurantRepository.findAll(pageable).map(PlaceDto::from);

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
    public PlaceDto getPlaceDetail(Long placeId) {
        return restaurantRepository.findById(placeId).map(PlaceDto::detailedFrom)
            .orElseThrow(() -> new CustomException(
                CommonErrorCode.RESOURCE_NOT_FOUND));

    }

    @Transactional
    public void createPlace(AdminCreatePlaceRequestDto requestDto) {
        OpeningHourDto openingHour = requestDto.getOpeningHour();
        PositionDto position = requestDto.getPosition();

        Restaurant restaurant = Restaurant.builder()
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
            .mainImageUrl(requestDto.getMainImageUrl())
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
                .restaurant(restaurant)
                .build();

            subwayStationPlaceList.add(subwayStationPlace);
        }

        ArrayList<Menu> menuList = new ArrayList<>();
        for (String menuName : requestDto.getMenuList()) {
            Menu menu = Menu.builder().name(menuName).restaurant(restaurant).build();
            menuList.add(menu);
        }

        restaurantRepository.save(restaurant);
        menuRepository.saveAll(menuList);
        subwayStationPlaceRepository.saveAll(subwayStationPlaceList);
    }

    @Transactional
    public void updatePlaceDetail(AdminUpdatePlaceRequestDto requestDto) {

        Restaurant restaurant = restaurantRepository.findById(requestDto.getPlaceId())
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));

        List<Long> subwayStationIdList = requestDto.getSubwayStationIdList();
        List<SubwayStation> subwayStationList = subwayStationRepository.findAllById(
                subwayStationIdList);

        ArrayList<SubwayStationPlace> subwayStationPlaceList = new ArrayList<>();
        for (SubwayStation subwayStation : subwayStationList) {
            SubwayStationPlace subwayStationPlace = SubwayStationPlace.builder()
                .isMain(subwayStation.getId().equals(subwayStationIdList.getFirst())) // 첫 요소가 메인 지하철역
                .subwayStation(subwayStation)
                .restaurant(restaurant)
                .build();

            subwayStationPlaceList.add(subwayStationPlace);
        }

        ArrayList<Menu> menuList = new ArrayList<>();
        for (String menuName : requestDto.getMenuList()) {
            Menu menu = Menu.builder().name(menuName).restaurant(restaurant).build();
            menuList.add(menu);
        }

        restaurant.adminUpdate(requestDto, subwayStationPlaceList, menuList);

        menuRepository.deleteAllByRestaurantId(requestDto.getPlaceId());
        menuRepository.saveAll(menuList);
        subwayStationPlaceRepository.deleteAllByRestaurantId(requestDto.getPlaceId());
        subwayStationPlaceRepository.saveAll(subwayStationPlaceList);
        restaurantRepository.save(restaurant);
    }

}