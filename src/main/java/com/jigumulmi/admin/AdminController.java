package com.jigumulmi.admin;

import com.jigumulmi.admin.dto.request.AdminCreatePlaceRequestDto;
import com.jigumulmi.admin.dto.request.AdminDeletePlaceRequestDto;
import com.jigumulmi.admin.dto.request.AdminGetMemberListRequestDto;
import com.jigumulmi.admin.dto.request.AdminGetPlaceListRequestDto;
import com.jigumulmi.admin.dto.request.AdminSavePlaceBasicRequestDto;
import com.jigumulmi.admin.dto.request.AdminUpdatePlaceRequestDto;
import com.jigumulmi.admin.dto.response.AdminMemberListResponseDto;
import com.jigumulmi.admin.dto.response.AdminPlaceDetailResponseDto;
import com.jigumulmi.admin.dto.response.AdminPlaceListResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "멤버 리스트 조회")
    @ApiResponses(
        value = {@ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = AdminMemberListResponseDto.class))})}
    )
    @GetMapping("/member")
    public ResponseEntity<?> getMemberList(
        @ModelAttribute AdminGetMemberListRequestDto requestDto) {
        AdminMemberListResponseDto memberList = adminService.getMemberList(requestDto);
        return ResponseEntity.ok().body(memberList);
    }


    @Operation(summary = "장소 리스트 조회")
    @ApiResponses(
        value = {@ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = AdminPlaceListResponseDto.class))})}
    )
    @GetMapping("/place")
    public ResponseEntity<?> getPlaceList(@ModelAttribute AdminGetPlaceListRequestDto requestDto) {
        AdminPlaceListResponseDto placeList = adminService.getPlaceList(requestDto);
        return ResponseEntity.ok().body(placeList);
    }

    @Operation(summary = "장소 상세 조회")
    @ApiResponses(
        value = {@ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = AdminPlaceDetailResponseDto.class))})}
    )
    @GetMapping("/place/detail/{placeId}")
    public ResponseEntity<?> getPlaceDetail(@PathVariable(name = "placeId") Long placeId) {
        AdminPlaceDetailResponseDto placeDetail = adminService.getPlaceDetail(placeId);
        return ResponseEntity.ok().body(placeDetail);
    }

    @Operation(summary = "장소 등록")
    @ApiResponse(responseCode = "204")
    @PostMapping("/place")
    public ResponseEntity<?> createPlace(@RequestBody AdminCreatePlaceRequestDto requestDto) {
        adminService.createPlace(requestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "장소 수정", description = "덮어쓰는 로직이므로 수정하지 않은 항목도 기존 조회된 데이터를 꼭 담아주세요")
    @ApiResponse(responseCode = "204")
    @PutMapping("/place")
    public ResponseEntity<?> updatePlaceDetail(
        @RequestBody AdminUpdatePlaceRequestDto requestDto) {
        adminService.updatePlaceDetail(requestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "장소 기본 정보 저장하기")
    @ApiResponse(responseCode = "204")
    @PatchMapping("/place")
    public ResponseEntity<?> savePlaceBasic(
        @RequestBody AdminSavePlaceBasicRequestDto requestDto) {
        adminService.savePlaceBasic(requestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "장소 삭제")
    @ApiResponse(responseCode = "204")
    @DeleteMapping("/place")
    public ResponseEntity<?> deletePlace(
        @RequestBody AdminDeletePlaceRequestDto requestDto) {
        adminService.deletePlace(requestDto);
        return ResponseEntity.noContent().build();
    }
}
