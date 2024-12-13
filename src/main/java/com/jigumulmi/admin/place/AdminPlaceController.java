package com.jigumulmi.admin.place;

import com.jigumulmi.admin.place.dto.request.AdminCreatePlaceRequestDto;
import com.jigumulmi.admin.place.dto.request.AdminDeletePlaceRequestDto;
import com.jigumulmi.admin.place.dto.request.AdminGetPlaceListRequestDto;
import com.jigumulmi.admin.place.dto.request.AdminUpdatePlaceRequestDto;
import com.jigumulmi.admin.place.dto.response.AdminPlaceDetailResponseDto;
import com.jigumulmi.admin.place.dto.response.AdminPlaceListResponseDto;
import com.jigumulmi.config.common.PageableParams;
import com.jigumulmi.config.security.RequiredAuthUser;
import com.jigumulmi.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Operation(summary = "장소 상세 조회")
    @ApiResponses(
        value = {@ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = AdminPlaceDetailResponseDto.class))})}
    )
    @GetMapping("/{placeId}")
    public ResponseEntity<?> getPlaceDetail(@PathVariable(name = "placeId") Long placeId) {
        AdminPlaceDetailResponseDto placeDetail = adminPlaceService.getPlaceDetail(placeId);
        return ResponseEntity.ok().body(placeDetail);
    }

    @Operation(summary = "장소 등록")
    @ApiResponse(responseCode = "204")
    @PostMapping("")
    public ResponseEntity<?> createPlace(
        @RequestBody AdminCreatePlaceRequestDto requestDto,
        @RequiredAuthUser Member member
    ) {
        adminPlaceService.createPlace(requestDto, member);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "장소 수정", description = "덮어쓰는 로직이므로 수정하지 않은 항목도 기존 조회된 데이터를 꼭 담아주세요")
    @ApiResponse(responseCode = "204")
    @PutMapping("")
    public ResponseEntity<?> updatePlaceDetail(
        @RequestBody AdminUpdatePlaceRequestDto requestDto) {
        adminPlaceService.updatePlaceDetail(requestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "장소 삭제")
    @ApiResponse(responseCode = "204")
    @DeleteMapping("")
    public ResponseEntity<?> deletePlace(
        @RequestBody AdminDeletePlaceRequestDto requestDto) {
        adminPlaceService.deletePlace(requestDto);
        return ResponseEntity.noContent().build();
    }
}
