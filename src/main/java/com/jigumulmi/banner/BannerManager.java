package com.jigumulmi.banner;

import com.jigumulmi.banner.domain.Banner;
import com.jigumulmi.banner.dto.response.BannerResponseDto;
import com.jigumulmi.banner.repository.BannerRepository;
import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BannerManager {

    private final BannerRepository bannerRepository;

    public List<BannerResponseDto> getBannerList() {
        return bannerRepository.findAllByIsActiveTrue().stream()
            .map(BannerResponseDto::from).toList();
    }

    public BannerResponseDto getBanner(Long bannerId) {
        Banner banner = bannerRepository.findById(bannerId)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));
        return BannerResponseDto.from(banner);
    }
}
