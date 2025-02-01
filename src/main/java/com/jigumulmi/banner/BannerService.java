package com.jigumulmi.banner;

import com.jigumulmi.banner.dto.response.BannerPlaceListResponseDto;
import com.jigumulmi.banner.dto.response.BannerPlaceListResponseDto.BannerPlaceDto;
import com.jigumulmi.banner.dto.response.BannerResponseDto;
import com.jigumulmi.banner.repository.BannerRepository;
import com.jigumulmi.banner.repository.CustomBannerRepository;
import com.jigumulmi.common.PageDto;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.dto.response.SurroundingDateBusinessHour;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    @Transactional(readOnly = true)
    public BannerPlaceListResponseDto getMappedPlaceList(Pageable pageable, Long bannerId) {
        Page<Place> placePage = customBannerRepository.getAllMappedPlaceByBannerId(pageable,
            bannerId);

        List<BannerPlaceDto> placeDtoList = placePage.getContent().stream()
            .map(BannerPlaceDto::from).collect(Collectors.toList());

        List<Long> placeIdList = placeDtoList.stream().map(BannerPlaceDto::getId).toList();
        Map<Long, SurroundingDateBusinessHour> surroundingBusinessHourMap = customBannerRepository.getSurroundingBusinessHourByPlaceIdIn(
            placeIdList);
        placeDtoList.forEach(
            place -> place.setLiveOpeningStatus(surroundingBusinessHourMap.get(place.getId())));

        return BannerPlaceListResponseDto.builder()
            .data(placeDtoList)
            .page(PageDto.builder()
                .totalCount(placePage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .totalPage(placePage.getTotalPages())
                .build()
            )
            .build();
    }
}
