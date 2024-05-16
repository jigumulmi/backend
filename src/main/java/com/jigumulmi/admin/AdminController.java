package com.jigumulmi.admin;

import com.jigumulmi.admin.dto.request.GetMemberListRequestDto;
import com.jigumulmi.admin.dto.request.GetPlaceListRequestDto;
import com.jigumulmi.admin.dto.response.MemberListResponseDto;
import com.jigumulmi.admin.dto.response.PlaceListResponseDto;
import com.jigumulmi.admin.dto.response.PlaceListResponseDto.PlaceDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "멤버 리스트 조회")
    @ApiResponses(
        value = {@ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = MemberListResponseDto.class))})}
    )
    @GetMapping("/member")
    public ResponseEntity<?> getMemberList(@ModelAttribute GetMemberListRequestDto requestDto) {
        MemberListResponseDto memberList = adminService.getMemberList(requestDto);
        return ResponseEntity.ok().body(memberList);
    }


    @Operation(summary = "장소 리스트 조회")
    @ApiResponses(
        value = {@ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = PlaceListResponseDto.class))})}
    )
    @GetMapping("/place")
    public ResponseEntity<?> getPlaceList(@ModelAttribute GetPlaceListRequestDto requestDto) {
        PlaceListResponseDto placeList = adminService.getPlaceList(requestDto);
        return ResponseEntity.ok().body(placeList);
    }

    @Operation(summary = "장소 상세 조회")
    @ApiResponses(
        value = {@ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = PlaceDto.class))})}
    )
    @GetMapping("/place/detail")
    public ResponseEntity<?> getPlaceDetail(@RequestParam(name = "placeId") Long placeId) {
        PlaceDto placeDetail = adminService.getPlaceDetail(placeId);
        return ResponseEntity.ok().body(placeDetail);
    }
}
