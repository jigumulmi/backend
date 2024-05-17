package com.jigumulmi.admin;

import com.jigumulmi.admin.dto.request.AdminCreatePlaceRequestDto;
import com.jigumulmi.admin.dto.request.AdminGetMemberListRequestDto;
import com.jigumulmi.admin.dto.request.AdminGetPlaceListRequestDto;
import com.jigumulmi.admin.dto.request.AdminUpdatePlaceRequestDtoAdmin;
import com.jigumulmi.admin.dto.response.AdminMemberListResponseDto;
import com.jigumulmi.admin.dto.response.AdminMemberListResponseDto.MemberDto;
import com.jigumulmi.admin.dto.response.PageDto;
import com.jigumulmi.admin.dto.response.AdminPlaceListResponseDto;
import com.jigumulmi.admin.dto.response.AdminPlaceListResponseDto.PlaceDto;
import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import com.jigumulmi.member.MemberRepository;
import com.jigumulmi.place.domain.Menu;
import com.jigumulmi.place.domain.Restaurant;
import com.jigumulmi.place.domain.SubwayStation;
import com.jigumulmi.place.repository.MenuRepository;
import com.jigumulmi.place.repository.RestaurantRepository;
import com.jigumulmi.place.repository.SubwayStationRepository;
import java.util.ArrayList;
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
        SubwayStation subwayStation = subwayStationRepository.findById(
                requestDto.getSubwayStationId())
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));

        Restaurant restaurant = Restaurant.builder()
            .name(requestDto.getName())
            .category(requestDto.getCategory())
            .address(requestDto.getAddress())
            .contact(requestDto.getContact())
            .contact(requestDto.getContact())
            .openingHourSun(requestDto.getOpeningHourSun())
            .openingHourMon(requestDto.getOpeningHourMon())
            .openingHourTue(requestDto.getOpeningHourTue())
            .openingHourWed(requestDto.getOpeningHourWed())
            .openingHourThu(requestDto.getOpeningHourThu())
            .openingHourFri(requestDto.getOpeningHourFri())
            .openingHourSat(requestDto.getOpeningHourSat())
            .mainImageUrl(requestDto.getMainImageUrl())
            .placeUrl(requestDto.getPlaceUrl())
            .longitude(requestDto.getLongitude())
            .latitude(requestDto.getLatitude())
            .registrantComment(requestDto.getRegistrantComment())
            .isApproved(requestDto.getIsApproved())
            .subwayStation(subwayStation)
            .build();

        ArrayList<Menu> menuList = new ArrayList<>();
        for (String menuName : requestDto.getMenuList()) {
            Menu menu = Menu.builder().name(menuName).restaurant(restaurant).build();
            menuList.add(menu);
        }

        restaurantRepository.save(restaurant);
        menuRepository.saveAll(menuList);
    }

    @Transactional
    public void updatePlaceDetail(AdminUpdatePlaceRequestDtoAdmin requestDto) {

        Restaurant restaurant = restaurantRepository.findById(requestDto.getPlaceId())
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));

        SubwayStation subwayStation = subwayStationRepository.findById(
                requestDto.getSubwayStationId())
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));

        ArrayList<Menu> menuList = new ArrayList<>();
        for (String menuName : requestDto.getMenuList()) {
            Menu menu = Menu.builder().name(menuName).restaurant(restaurant).build();
            menuList.add(menu);
        }

        restaurant.updatePlace(requestDto, subwayStation, menuList);

        menuRepository.deleteAllByRestaurantId(requestDto.getPlaceId());
        menuRepository.saveAll(menuList);
        restaurantRepository.save(restaurant);
    }

}
