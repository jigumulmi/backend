package com.jigumulmi.admin.banner;

import com.jigumulmi.admin.banner.dto.request.BannerPlaceMappingRequestDto;
import com.jigumulmi.admin.banner.dto.request.CreateBannerRequestDto;
import com.jigumulmi.admin.banner.dto.request.DeleteBannerRequestDto;
import com.jigumulmi.admin.banner.dto.request.GetCandidatePlaceListRequestDto;
import com.jigumulmi.admin.banner.dto.request.UpdateBannerRequestDto;
import com.jigumulmi.admin.banner.dto.response.AdminBannerDetailResponseDto;
import com.jigumulmi.admin.banner.dto.response.AdminBannerPlaceListResponseDto;
import com.jigumulmi.admin.banner.dto.response.AdminBannerResponseDto;
import com.jigumulmi.admin.banner.dto.response.CreateBannerResponseDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "배너 관리")
@RestController
@RequestMapping("/admin/banner")
@RequiredArgsConstructor
public class AdminBannerController {

    private final AdminBannerService adminBannerService;

    @Operation(summary = "배너 생성")
    @ApiResponse(responseCode = "201", content = {
        @Content(schema = @Schema(implementation = CreateBannerResponseDto.class))})
    @PostMapping(path = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createBanner(
        @Valid @ModelAttribute CreateBannerRequestDto requestDto) {
        CreateBannerResponseDto responseDto = adminBannerService.createBanner(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
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
    @ApiResponse(responseCode = "201")
    @PostMapping(path = "/{bannerId}/place")
    public ResponseEntity<?> createBannerPlace(@PathVariable Long bannerId,
        @RequestBody BannerPlaceMappingRequestDto requestDto) {
        adminBannerService.addBannerPlace(bannerId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
    public ResponseEntity<?> getMappedPlaceList(@ParameterObject Pageable pageable,
        @PathVariable Long bannerId) {
        AdminBannerPlaceListResponseDto responseDto = adminBannerService.getMappedPlaceList(
            pageable, bannerId);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "배너 정보 수정", description = "이미지 제외한 정보 수정")
    @ApiResponse(responseCode = "201")
    @PutMapping("/{bannerId}")
    public ResponseEntity<?> updateBanner(@PathVariable Long bannerId,
        @Valid @RequestBody UpdateBannerRequestDto requestDto) {
        adminBannerService.updateBanner(bannerId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "배너 외부 이미지 수정")
    @ApiResponse(responseCode = "201")
    @PutMapping(path = "/{bannerId}/outerImage", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateBannerOuterImage(@PathVariable Long bannerId,
        @RequestParam MultipartFile outerImage) {
        adminBannerService.updateBannerOuterImage(bannerId, outerImage);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "배너 내부 이미지 수정")
    @ApiResponse(responseCode = "201")
    @PutMapping(path = "/{bannerId}/innerImage", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateBannerInnerImage(@PathVariable Long bannerId,
        @RequestParam MultipartFile innerImage) {
        adminBannerService.updateBannerInnerImage(bannerId, innerImage);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "배너 삭제")
    @ApiResponse(responseCode = "204")
    @DeleteMapping("")
    public ResponseEntity<?> deleteBannerList(
        @Valid @RequestBody DeleteBannerRequestDto requestDto) {
        adminBannerService.deleteBannerList(requestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "할당 가능한 장소 목록 조회")
    @ApiResponse(responseCode = "200", content = {
        @Content(schema = @Schema(implementation = AdminBannerPlaceListResponseDto.class))})
    @PageableParams
    @GetMapping("/place")
    public ResponseEntity<?> getCandidatePlaceList(@ParameterObject Pageable pageable,
        @Valid @ModelAttribute GetCandidatePlaceListRequestDto requestDto) {
        AdminBannerPlaceListResponseDto responseDto = adminBannerService.getCandidatePlaceList(
            pageable, requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
