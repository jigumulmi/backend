package com.jigumulmi.banner;

import com.jigumulmi.banner.domain.Banner;
import com.jigumulmi.banner.dto.response.BannerPlaceListResponseDto;
import com.jigumulmi.banner.dto.response.BannerPlaceListResponseDto.BannerPlaceDto;
import com.jigumulmi.banner.dto.response.BannerResponseDto;
import com.jigumulmi.banner.repository.BannerRepository;
import com.jigumulmi.banner.repository.CustomBannerRepository;
import com.jigumulmi.common.PagedResponseDto;
import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import com.jigumulmi.place.dto.response.SurroundingDateBusinessHour;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;
    private final CustomBannerRepository customBannerRepository;

    public List<BannerResponseDto> getBannerList() {
        return bannerRepository.findAllByIsActiveTrue().stream()
            .map(BannerResponseDto::from).toList();
    }

    public BannerResponseDto getBanner(Long bannerId) {
        Banner banner = bannerRepository.findById(bannerId)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));
        return BannerResponseDto.from(banner);
    }

    @Transactional(readOnly = true)
    public PagedResponseDto<BannerPlaceDto> getMappedPlaceList(Pageable pageable, Long bannerId) {
        Page<BannerPlaceDto> placePage = customBannerRepository.getAllMappedPlaceByBannerId(pageable,
            bannerId).map(BannerPlaceDto::from);

        List<BannerPlaceDto> placeDtoList = placePage.getContent();

        LocalDateTime now = LocalDateTime.now();
        LocalTime localTime = now.toLocalTime();
        LocalDate localDate = now.toLocalDate();

        List<Long> placeIdList = placeDtoList.stream().map(BannerPlaceDto::getId).toList();
        Map<Long, SurroundingDateBusinessHour> surroundingBusinessHourMap = customBannerRepository.getSurroundingBusinessHourByPlaceIdIn(
            placeIdList, localDate);
        placeDtoList.forEach(
            place -> place.setCurrentOpeningStatus(surroundingBusinessHourMap.get(place.getId()), localTime));

        return BannerPlaceListResponseDto.of(placePage, pageable);
    }
}
