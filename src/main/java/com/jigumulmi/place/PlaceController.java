package com.jigumulmi.place;

import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("/subway")
    public ResponseEntity<?> getSubwayStations(@RequestParam(name = "name") String name) {
        ArrayList<SubwayStationResponseDto> subwayStationList = placeService.getSubwayStationList(name);
        return ResponseEntity.ok().body(subwayStationList);
    }
}
