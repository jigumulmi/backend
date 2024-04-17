package com.jigumulmi.place;

import com.jigumulmi.place.dto.request.CreatePlaceRequestDto;
import com.jigumulmi.place.dto.response.RestaurantDetailResponseDto;
import com.jigumulmi.place.dto.response.RestaurantResponseDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "지하철역 조회")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = SubwayStationResponseDto.class)))})}
    )
    @GetMapping("/subway")
    public ResponseEntity<?> getSubwayStations(@RequestParam(name = "stationName") String stationName) {
        List<SubwayStationResponseDto> subwayStationList = placeService.getSubwayStationList(stationName);
        return ResponseEntity.ok().body(subwayStationList);
    }

    @Operation(summary = "장소 등록 신청")
    @ApiResponse(responseCode = "201")
    @PostMapping("")
    public ResponseEntity<?> registerPlace(@Valid @RequestBody CreatePlaceRequestDto requestDto) {
        placeService.registerPlace(requestDto);
        return new ResponseEntity<>("Register success", HttpStatus.CREATED);
    }

    @Operation(summary = "장소 리스트 조회")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = RestaurantResponseDto.class)))})}
    )
    @GetMapping("")
    public ResponseEntity<?> getPlaceList(
            @RequestParam(name = "subwayStationId", required = false) Long subwayStationId,
            @RequestParam(name = "placeId", required = false) Long placeId
    ) {
        List<RestaurantResponseDto> placeList = placeService.getPlaceList(subwayStationId, placeId);
        return ResponseEntity.ok().body(placeList);
    }

    @Operation(summary = "장소 상세 조회")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = RestaurantDetailResponseDto.class))})}
    )
    @GetMapping("/detail/{placeId}")
    public ResponseEntity<?> getPlaceDetail(@PathVariable(name = "placeId") Long placeId) {
        RestaurantDetailResponseDto placeDetail = placeService.getPlaceDetail(placeId);
        return ResponseEntity.ok().body(placeDetail);
    }
}
