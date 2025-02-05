package com.jigumulmi.banner;

import com.jigumulmi.banner.dto.response.BannerPlaceListResponseDto;
import com.jigumulmi.banner.dto.response.BannerPlaceListResponseDto.BannerPlaceDto;
import com.jigumulmi.banner.dto.response.BannerResponseDto;
import com.jigumulmi.banner.repository.BannerRepository;
import com.jigumulmi.banner.repository.CustomBannerRepository;
import com.jigumulmi.common.PageDto;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.dto.response.SurroundingDateBusinessHour;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

        LocalDateTime now = LocalDateTime.now();
        LocalTime localTime = now.toLocalTime();
        LocalDate localDate = now.toLocalDate();

        List<Long> placeIdList = placeDtoList.stream().map(BannerPlaceDto::getId).toList();
        Map<Long, SurroundingDateBusinessHour> surroundingBusinessHourMap = customBannerRepository.getSurroundingBusinessHourByPlaceIdIn(
            placeIdList, localDate);
        placeDtoList.forEach(
            place -> place.setCurrentOpeningStatus(surroundingBusinessHourMap.get(place.getId()), localTime));

        return BannerPlaceListResponseDto.builder()
            .data(placeDtoList)
            .page(PageDto.of(placePage, pageable))
            .build();
    }
}
