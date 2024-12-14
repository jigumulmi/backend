package com.jigumulmi.admin.banner;

import com.jigumulmi.admin.banner.dto.request.BannerPlaceMappingRequestDto;
import com.jigumulmi.admin.banner.dto.request.CreateBannerRequestDto;
import com.jigumulmi.admin.banner.dto.response.AdminBannerDetailResponseDto;
import com.jigumulmi.admin.banner.dto.response.AdminBannerPlaceListResponseDto;
import com.jigumulmi.admin.banner.dto.response.AdminBannerResponseDto;
import com.jigumulmi.config.common.PageableParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "배너 관리")
@RestController
@RequestMapping("/admin/banner")
@RequiredArgsConstructor
public class AdminBannerController {

    private final AdminBannerService adminBannerService;

    @Operation(summary = "배너 생성")
    @ApiResponse(responseCode = "204")
    @PostMapping(path = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createBanner(
        @Valid @ModelAttribute CreateBannerRequestDto requestDto) {
        adminBannerService.createBanner(requestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "배너 목록 조회")
    @ApiResponse(responseCode = "200", content = {
        @Content(array = @ArraySchema(schema = @Schema(implementation = AdminBannerResponseDto.class)))})
    @GetMapping("")
    public ResponseEntity<?> getBannerList() {
        List<AdminBannerResponseDto> bannerList = adminBannerService.getBannerList();
        return ResponseEntity.ok(bannerList);
    }

    @Operation(summary = "장소 할당")
    @ApiResponse(responseCode = "204")
    @PostMapping(path = "/{bannerId}/place")
    public ResponseEntity<?> createBannerPlace(@PathVariable Long bannerId,
        @RequestBody BannerPlaceMappingRequestDto requestDto) {
        adminBannerService.addBannerPlace(bannerId, requestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "장소 할당 해제")
    @ApiResponse(responseCode = "204")
    @DeleteMapping(path = "/{bannerId}/place")
    public ResponseEntity<?> deleteBannerPlace(@PathVariable Long bannerId,
        @RequestBody BannerPlaceMappingRequestDto requestDto) {
        adminBannerService.removeBannerPlace(bannerId, requestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "배너 상세 조회")
    @ApiResponse(responseCode = "200", content = {
        @Content(schema = @Schema(implementation = AdminBannerDetailResponseDto.class))})
    @GetMapping("/{bannerId}")
    public ResponseEntity<?> getBannerDetail(@PathVariable Long bannerId) {
        AdminBannerDetailResponseDto responseDto = adminBannerService.getBannerDetail(bannerId);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "배너와 연관된 장소 목록 조회")
    @ApiResponse(responseCode = "200", content = {
        @Content(schema = @Schema(implementation = AdminBannerPlaceListResponseDto.class))})
    @PageableParams
    @GetMapping("/{bannerId}/place")
    public ResponseEntity<?> getBannerPlaceList(@ParameterObject Pageable pageable, @PathVariable Long bannerId) {
        AdminBannerPlaceListResponseDto responseDto = adminBannerService.getBannerPlaceList(
            pageable, bannerId);
        return ResponseEntity.ok(responseDto);
    }
}
