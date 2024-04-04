package com.jigumulmi.place;

import com.jigumulmi.place.domain.Menu;
import com.jigumulmi.place.domain.Restaurant;
import com.jigumulmi.place.domain.SubwayStation;
import com.jigumulmi.place.dto.request.CreatePlaceRequestDto;
import com.jigumulmi.place.dto.response.RestaurantResponseDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import com.jigumulmi.place.repository.MenuRepository;
import com.jigumulmi.place.repository.RestaurantRepository;
import com.jigumulmi.place.repository.SubwayStationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final SubwayStationRepository subwayStationRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;

    public List<SubwayStationResponseDto> getSubwayStationList(String stationName) {
        List<SubwayStation> subwayStationList = subwayStationRepository.findAllByStationNameStartsWith(stationName, Sort.by(Sort.Direction.ASC, "stationName"));

        ArrayList<SubwayStationResponseDto> responseDtoList = new ArrayList<>();
        for (SubwayStation subwayStation : subwayStationList) {
            SubwayStationResponseDto responseDto = SubwayStationResponseDto.builder()
                    .id(subwayStation.getId())
                    .stationName(subwayStation.getStationName())
                    .lineNumber(subwayStation.getLineNumber()).build();
            responseDtoList.add(responseDto);
        }

        return responseDtoList;
    }

    @Transactional
    public void registerPlace(CreatePlaceRequestDto requestDto) {
        SubwayStation subwayStation = subwayStationRepository.findById(requestDto.getSubway_station_id()).orElseThrow(IllegalArgumentException::new);
        Restaurant newRestaurant = Restaurant.builder().name(requestDto.getName()).subwayStation(subwayStation).registrantComment(requestDto.getRegistrantComment()).isApproved(false).build();

        ArrayList<Menu> menuList = new ArrayList<>();
        for (String menuName : requestDto.getMenuList()) {
            Menu menu = Menu.builder().name(menuName).restaurant(newRestaurant).build();
            menuList.add(menu);
        }

        restaurantRepository.save(newRestaurant);
        menuRepository.saveAll(menuList);
    }

    public List<RestaurantResponseDto> getPlaceList(Long subwayStationId) {
        List<Restaurant> restaurantList;
        if (subwayStationId != null) {
            restaurantList = restaurantRepository.findAllBySubwayStationIdAndIsApprovedTrue(subwayStationId);
        } else {
            restaurantList = restaurantRepository.findAllByIsApprovedTrue();
        }

        ArrayList<RestaurantResponseDto> responseDtoList = new ArrayList<>();
        for (Restaurant restaurant : restaurantList) {
            SubwayStation subwayStation = restaurant.getSubwayStation();

            RestaurantResponseDto responseDto = RestaurantResponseDto.builder()
                    .id(restaurant.getId())
                    .name(restaurant.getName())
                    .category(restaurant.getCategory())
                    .address(restaurant.getAddress())
                    .contact(restaurant.getContact())
                    .menuList(restaurant.getMenuList())
                    .openingHour(
                            RestaurantResponseDto.OpeningHourDto.builder().openingHourSun(restaurant.getOpeningHourSun())
                                    .openingHourMon(restaurant.getOpeningHourMon())
                                    .openingHourTue(restaurant.getOpeningHourTue())
                                    .openingHourWed(restaurant.getOpeningHourWed())
                                    .openingHourThu(restaurant.getOpeningHourThu())
                                    .openingHourFri(restaurant.getOpeningHourFri())
                                    .openingHourSat(restaurant.getOpeningHourSat()).build()
                    )
                    .additionalInfo(restaurant.getAdditionalInfo())
                    .mainImageUrl(restaurant.getMainImageUrl())
                    .placeUrl(restaurant.getPlaceUrl())
                    .position(
                            RestaurantResponseDto.PositionDto.builder().longitude(restaurant.getLongitude())
                                    .latitude(restaurant.getLatitude()).build()
                    )
                    .subwayStation(
                            SubwayStationResponseDto.builder().id(subwayStation.getId()).stationName(subwayStation.getStationName()).lineNumber(subwayStation.getLineNumber()).build()
                    )
                    .build();

            responseDtoList.add(responseDto);
        }

        return responseDtoList;
    }
}
