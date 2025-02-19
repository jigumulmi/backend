package com.jigumulmi.banner;

import com.jigumulmi.banner.dto.response.BannerPlaceListResponseDto.BannerPlaceDto;
import com.jigumulmi.banner.dto.response.BannerResponseDto;
import com.jigumulmi.common.PagedResponseDto;
import com.jigumulmi.place.PlaceManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerManager bannerManager;
    private final PlaceManager placeManager;

    public List<BannerResponseDto> getBannerList() {
        return bannerManager.getBannerList();
    }

    public BannerResponseDto getBanner(Long bannerId) {
        return bannerManager.getBanner(bannerId);
    }

    public PagedResponseDto<BannerPlaceDto> getMappedPlaceList(Pageable pageable, Long bannerId) {
        return placeManager.getApprovedMappedPlaceList(pageable, bannerId);
    }
}
