package com.jigumulmi.place;

import com.jigumulmi.place.dto.request.CreatePlaceRequestDto;
import com.jigumulmi.place.dto.response.RestaurantResponseDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("/subway")
    public ResponseEntity<?> getSubwayStations(@RequestParam(name = "stationName") String stationName) {
        List<SubwayStationResponseDto> subwayStationList = placeService.getSubwayStationList(stationName);
        return ResponseEntity.ok().body(subwayStationList);
    }

    @PostMapping("")
    public ResponseEntity<?> registerPlace(@Valid @RequestBody CreatePlaceRequestDto requestDto) {
        placeService.registerPlace(requestDto);
        return new ResponseEntity<>("Register success", HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<?> getPlaceList(@RequestParam(name = "subwayStationId") Long subwayStationId) {
        List<RestaurantResponseDto> placeList = placeService.getPlaceList(subwayStationId);
        return ResponseEntity.ok().body(placeList);
    }
}
