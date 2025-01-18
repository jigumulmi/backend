package com.jigumulmi.admin.place;

import com.jigumulmi.admin.place.dto.request.AdminCreatePlaceRequestDto;
import com.jigumulmi.admin.place.dto.request.AdminCreateTemporaryBusinessHourRequestDto;
import com.jigumulmi.admin.place.dto.request.AdminGetPlaceListRequestDto;
import com.jigumulmi.admin.place.dto.request.AdminUpdateFixedBusinessHourRequestDto;
import com.jigumulmi.admin.place.dto.response.AdminPlaceBasicResponseDto;
import com.jigumulmi.admin.place.dto.response.AdminPlaceBusinessHourResponseDto;
import com.jigumulmi.admin.place.dto.response.AdminPlaceListResponseDto;
import com.jigumulmi.admin.place.dto.response.CreatePlaceResponseDto;
import com.jigumulmi.config.common.PageableParams;
import com.jigumulmi.config.security.RequiredAuthUser;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.place.dto.ImageDto;
import com.jigumulmi.place.dto.MenuDto;
import com.jigumulmi.place.dto.response.DistrictResponseDto;
import com.jigumulmi.place.vo.Region;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "장소 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/place")
public class AdminPlaceController {

    private final AdminPlaceService adminPlaceService;


    @Operation(summary = "장소 리스트 조회")
    @ApiResponses(
        value = {@ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = AdminPlaceListResponseDto.class))})}
    )
    @PageableParams
    @GetMapping("")
    public ResponseEntity<?> getPlaceList(
        @ParameterObject Pageable pageable,
        @ModelAttribute AdminGetPlaceListRequestDto requestDto) {
        AdminPlaceListResponseDto placeList = adminPlaceService.getPlaceList(pageable, requestDto);
        return ResponseEntity.ok().body(placeList);
    }

    @Operation(summary = "장소 기본 정보 조회")
    @GetMapping("/{placeId}/basic")
    public ResponseEntity<AdminPlaceBasicResponseDto> getPlaceBasic(@PathVariable Long placeId) {
        AdminPlaceBasicResponseDto responseDto = adminPlaceService.getPlaceBasic(placeId);
        return ResponseEntity.ok().body(responseDto);
    }

    @Operation(summary = "장소 기본 정보 수정", description = "덮어쓰는 로직이므로 수정되지 않은 기존 데이터도 필요")
    @ApiResponse(responseCode = "201")
    @PutMapping("/{placeId}/basic")
    public ResponseEntity<?> updatePlaceBasic(@PathVariable Long placeId,
        @RequestBody AdminCreatePlaceRequestDto requestDto) {
        adminPlaceService.updatePlaceBasic(placeId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "장소 이미지 조회")
    @GetMapping("/{placeId}/image")
    public ResponseEntity<List<ImageDto>> getPlaceImage(@PathVariable Long placeId) {
        List<ImageDto> responseDto = adminPlaceService.getPlaceImage(placeId);
        return ResponseEntity.ok().body(responseDto);
    }

    @Operation(summary = "장소 이미지 수정", description = "덮어쓰는 로직이므로 수정되지 않은 기존 데이터도 필요")
    @ApiResponse(responseCode = "201")
    @PutMapping("/{placeId}/image")
    public ResponseEntity<?> updatePlaceImage(@PathVariable Long placeId,
        @RequestBody List<ImageDto> requestDto) {
        adminPlaceService.updatePlaceImage(placeId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "장소 메뉴 조회")
    @GetMapping("/{placeId}/menu")
    public ResponseEntity<List<MenuDto>> getMenu(@PathVariable Long placeId) {
        List<MenuDto> responseDto = adminPlaceService.getMenu(placeId);
        return ResponseEntity.ok().body(responseDto);
    }

    @Operation(summary = "장소 메뉴 수정", description = "덮어쓰는 로직이므로 수정되지 않은 기존 데이터도 필요")
    @ApiResponse(responseCode = "201")
    @PutMapping("/{placeId}/menu")
    public ResponseEntity<?> updateMenu(@PathVariable Long placeId,
        @RequestBody List<MenuDto> requestDto) {
        adminPlaceService.updateMenu(placeId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "장소 고정 영업시간 수정", description = "덮어쓰는 로직이므로 수정되지 않은 기존 데이터도 필요")
    @ApiResponse(responseCode = "201")
    @PutMapping("/{placeId}/business-hour/fixed")
    public ResponseEntity<?> updateFixedBusinessHour(@PathVariable Long placeId,
        @Valid @RequestBody AdminUpdateFixedBusinessHourRequestDto requestDto) {
        adminPlaceService.updateFixedBusinessHour(placeId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "장소 변동 영업시간 생성")
    @ApiResponse(responseCode = "201")
    @PostMapping("/{placeId}/business-hour/temporary")
    public ResponseEntity<?> createTemporaryBusinessHour(@PathVariable Long placeId,
        @Valid @RequestBody AdminCreateTemporaryBusinessHourRequestDto requestDto) {
        adminPlaceService.createTemporaryBusinessHour(placeId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "장소 변동 영업시간 수정", description = "덮어쓰는 로직이므로 수정되지 않은 기존 데이터도 필요, 날짜만 변경도 가능")
    @ApiResponse(responseCode = "201")
    @PutMapping("/{placeId}/business-hour/temporary/{hourId}")
    public ResponseEntity<?> updateTemporaryBusinessHour(@PathVariable Long placeId,
        @PathVariable Long hourId,
        @Valid @RequestBody AdminCreateTemporaryBusinessHourRequestDto requestDto) {
        adminPlaceService.updateTemporaryBusinessHour(hourId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "장소 변동 영업시간 삭제")
    @ApiResponse(responseCode = "204")
    @DeleteMapping("/{placeId}/business-hour/temporary/{hourId}")
    public ResponseEntity<?> deleteTemporaryBusinessHour(@PathVariable Long placeId,
        @PathVariable Long hourId) {
        adminPlaceService.deleteTemporaryBusinessHour(hourId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "장소 영업시간 조회")
    @GetMapping("/{placeId}/business-hour")
    public ResponseEntity<AdminPlaceBusinessHourResponseDto> getPlaceBusinessHour(
        @PathVariable Long placeId, @RequestParam(required = false) Integer month) {
        month = (month == null) ? LocalDate.now().getMonthValue() : month;
        AdminPlaceBusinessHourResponseDto responseDto = adminPlaceService.getPlaceBusinessHour(
            placeId, month);
        return ResponseEntity.ok().body(responseDto);
    }

    @Operation(summary = "장소 생성")
    @ApiResponse(responseCode = "201")
    @PostMapping("")
    public ResponseEntity<CreatePlaceResponseDto> createPlace(
        @RequestBody AdminCreatePlaceRequestDto requestDto,
        @RequiredAuthUser Member member
    ) {
        CreatePlaceResponseDto responseDto = adminPlaceService.createPlace(requestDto, member);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(summary = "장소 삭제")
    @ApiResponse(responseCode = "204")
    @DeleteMapping("/{placeId}")
    public ResponseEntity<?> deletePlace(@PathVariable Long placeId) {
        adminPlaceService.deletePlace(placeId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "광역시도 조회")
    @ApiResponses(
        value = {@ApiResponse(responseCode = "200", content = {
            @Content(array = @ArraySchema(schema = @Schema(implementation = Region.class)))})}
    )
    @GetMapping("/region")
    public ResponseEntity<?> getRegionList() {
        List<Region> regionList = adminPlaceService.getRegionList();
        return ResponseEntity.ok().body(regionList);
    }

    @Operation(summary = "시군구 조회")
    @ApiResponses(
        value = {@ApiResponse(responseCode = "200", content = {
            @Content(array = @ArraySchema(schema = @Schema(implementation = DistrictResponseDto.class)))})}
    )
    @GetMapping("/district")
    public ResponseEntity<?> getDistrictList(@RequestParam Region region) {
        List<DistrictResponseDto> districtList = adminPlaceService.getDistrictList(region);
        return ResponseEntity.ok().body(districtList);
    }
}
