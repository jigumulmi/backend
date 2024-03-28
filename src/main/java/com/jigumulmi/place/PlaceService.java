package com.jigumulmi.place;

import com.jigumulmi.place.domain.SubwayStation;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import com.jigumulmi.place.repository.RestaurantRepository;
import com.jigumulmi.place.repository.SubwayStationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
}
