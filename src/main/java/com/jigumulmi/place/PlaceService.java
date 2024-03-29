package com.jigumulmi.place;

import com.jigumulmi.place.domain.Restaurant;
import com.jigumulmi.place.domain.SubwayStation;
import com.jigumulmi.place.dto.request.CreatePlaceRequestDto;
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

    public ArrayList<SubwayStationResponseDto> getSubwayStationList(String name) {
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
        Restaurant newRestaurant = Restaurant.builder().name(requestDto.getName()).subwayStation(subwayStation).menuList(requestDto.getMenuList()).registrantComment(requestDto.getRegistrantComment()).build();

        restaurantRepository.save(newRestaurant);
    }
}
