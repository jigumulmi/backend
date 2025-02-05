package com.jigumulmi.banner;

import com.jigumulmi.banner.dto.response.BannerPlaceListResponseDto.BannerPlaceDto;
import com.jigumulmi.banner.dto.response.BannerResponseDto;
import com.jigumulmi.common.PagedResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/banner")
public class BannerController {

    private final BannerService bannerService;

    @Operation(summary = "배너 목록 조회")
    @GetMapping("")
    public ResponseEntity<List<BannerResponseDto>> getBannerList() {
        List<BannerResponseDto> response = bannerService.getBannerList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "배너에 연관된 장소 목록 조회")
    @GetMapping("/{bannerId}/place")
    public ResponseEntity<PagedResponseDto<BannerPlaceDto>> getMappedPlaceList(
        @ParameterObject Pageable pageable,
        @PathVariable Long bannerId) {
        PagedResponseDto<BannerPlaceDto> responseDtoList = bannerService.getMappedPlaceList(pageable,
            bannerId);
        return ResponseEntity.ok(responseDtoList);
    }
}
