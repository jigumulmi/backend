package com.jigumulmi.banner;

import com.jigumulmi.banner.dto.response.BannerResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/banner")
public class BannerController {

    private final BannerService bannerService;

    @GetMapping("")
    public ResponseEntity<List<BannerResponseDto>> getBannerList() {
        List<BannerResponseDto> response = bannerService.getBannerList();
        return ResponseEntity.ok(response);
    }

}
