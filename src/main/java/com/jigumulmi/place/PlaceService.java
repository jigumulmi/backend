package com.jigumulmi.place;

import com.jigumulmi.place.domain.Restaurant;
import com.jigumulmi.place.domain.SubwayStation;
import com.jigumulmi.place.dto.request.CreatePlaceRequestDto;
import com.jigumulmi.place.dto.response.RestaurantResponseDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
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

    public List<SubwayStationResponseDto> getSubwayStationList(String name) {
        List<SubwayStation> subwayStationList = subwayStationRepository.findAllByNameStartsWith(name, Sort.by(Sort.Direction.ASC, "name"));

        ArrayList<SubwayStationResponseDto> responseDtoList = new ArrayList<>();
        for (SubwayStation subwayStation : subwayStationList) {
            SubwayStationResponseDto responseDto = SubwayStationResponseDto.builder()
                    .id(subwayStation.getId())
                    .name(subwayStation.getName())
                    .line(subwayStation.getLine()).build();
            responseDtoList.add(responseDto);
        }

        return responseDtoList;
    }

    @Transactional
    public void registerPlace(CreatePlaceRequestDto requestDto) {
        SubwayStation subwayStation = subwayStationRepository.findById(requestDto.getSubway_station_id()).orElseThrow(IllegalArgumentException::new);
        Restaurant newRestaurant = Restaurant.builder().name(requestDto.getName()).subwayStation(subwayStation).menuList(requestDto.getMenuList()).registrantComment(requestDto.getRegistrantComment()).isApproved(false).build();

        restaurantRepository.save(newRestaurant);
    }

    public List<RestaurantResponseDto> getPlaceList(Long subwayStationId) {
        List<Restaurant> restaurantList = restaurantRepository.findAllBySubwayStationIdAndIsApprovedTrue(subwayStationId);

        ArrayList<RestaurantResponseDto> responseDtoList = new ArrayList<>();
        for (Restaurant restaurant : restaurantList) {
            RestaurantResponseDto responseDto = RestaurantResponseDto.builder()
                    .id(restaurant.getId())
                    .name(restaurant.getName())
                    .category(restaurant.getCategory())
                    .address(restaurant.getAddress())
                    .contact(restaurant.getContact())
                    .menuList(restaurant.getMenuList())
                    .openingHourSun(restaurant.getOpeningHourSun())
                    .openingHourMon(restaurant.getOpeningHourMon())
                    .openingHourTue(restaurant.getOpeningHourTue())
                    .openingHourWed(restaurant.getOpeningHourWed())
                    .openingHourThu(restaurant.getOpeningHourThu())
                    .openingHourFri(restaurant.getOpeningHourFri())
                    .openingHourSat(restaurant.getOpeningHourSat())
                    .additionalInfo(restaurant.getAdditionalInfo())
                    .mainImageUrl(restaurant.getMainImageUrl())
                    .placeUrl(restaurant.getPlaceUrl())
                    .longitude(restaurant.getLongitude())
                    .latitude(restaurant.getLatitude())
                    .subwayStation(restaurant.getSubwayStation())
                    .build();
            responseDtoList.add(responseDto);
        }

        return responseDtoList;
    }
}
