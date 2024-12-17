package com.jigumulmi.admin.banner;

import com.jigumulmi.admin.banner.dto.request.BannerPlaceMappingRequestDto;
import com.jigumulmi.admin.banner.dto.request.DeleteBannerRequestDto;
import com.jigumulmi.banner.domain.Banner;
import com.jigumulmi.banner.domain.BannerPlaceMapping;
import com.jigumulmi.banner.repository.BannerRepository;
import com.jigumulmi.common.RepositoryTest;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.repository.PlaceRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

@RepositoryTest
class AdminCustomBannerRepositoryTest {

    @Autowired
    private AdminCustomBannerRepository adminCustomBannerRepository;
    @Autowired
    private BannerRepository bannerRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private BannerPlaceMappingRepository bannerPlaceMappingRepository;

    @Test
    @DisplayName("배너에 장소 할당")
    public void testBatchInsertBannerPlace() {
        // given
        Banner banner = Banner.builder().build();
        Banner savedBanner = bannerRepository.save(banner);
        Long savedBannerId = savedBanner.getId();

        Place place1 = Place.builder().build();
        Place place2 = Place.builder().build();
        List<Place> savedPlaceList = placeRepository.saveAll(
            List.of(place1, place2)
        );
        List<Long> savedPlaceIdList = savedPlaceList.stream().map(Place::getId).toList();

        BannerPlaceMappingRequestDto bannerPlaceMappingRequestDto = new BannerPlaceMappingRequestDto();
        ReflectionTestUtils.setField(bannerPlaceMappingRequestDto, "placeIdList", savedPlaceIdList);

        // when
        adminCustomBannerRepository.batchInsertBannerPlace(savedBannerId,
            bannerPlaceMappingRequestDto);

        // then
        List<BannerPlaceMapping> bannerPlaceMappingList = bannerPlaceMappingRepository.findAllByBannerId(
            savedBannerId);
        Assertions.assertEquals(bannerPlaceMappingList.size(), 2);

    }

    @Test
    @DisplayName("장소 할당 해제")
    public void testDeleteBannerPlace() {
        // given
        Banner banner = Banner.builder().build();
        Banner savedBanner = bannerRepository.save(banner);
        Long savedBannerId = savedBanner.getId();

        Place place1 = Place.builder().build();
        Place place2 = Place.builder().build();
        List<Place> savedPlaceList = placeRepository.saveAll(
            List.of(place1, place2)
        );
        List<Long> savedPlaceIdList = savedPlaceList.stream().map(Place::getId).toList();

        for (Place savedPlace : savedPlaceList) {
            bannerPlaceMappingRepository.save(
                BannerPlaceMapping.builder()
                    .banner(banner)
                    .place(savedPlace)
                    .build()
            );
        }

        BannerPlaceMappingRequestDto bannerPlaceMappingRequestDto = new BannerPlaceMappingRequestDto();
        ReflectionTestUtils.setField(bannerPlaceMappingRequestDto, "placeIdList", savedPlaceIdList);

        // when
        adminCustomBannerRepository.deleteBannerPlace(savedBannerId, bannerPlaceMappingRequestDto);

        // then
        List<BannerPlaceMapping> bannerPlaceMappingList = bannerPlaceMappingRepository.findAllByBannerId(
            savedBannerId);
        Assertions.assertEquals(bannerPlaceMappingList.size(), 0);

    }

    @Test
    @DisplayName("장소 목록 조회")
    public void testGetBannerList() {
        // given
        Banner banner = Banner.builder().build();
        Banner savedBanner = bannerRepository.save(banner);
        Long savedBannerId = savedBanner.getId();

        Place place1 = Place.builder().build();
        Place place2 = Place.builder().build();
        List<Place> savedPlaceList = placeRepository.saveAll(
            List.of(place1, place2)
        );

        for (Place savedPlace : savedPlaceList) {
            bannerPlaceMappingRepository.save(
                BannerPlaceMapping.builder()
                    .banner(banner)
                    .place(savedPlace)
                    .build()
            );
        }

        // when
        PageRequest pageRequest = PageRequest.ofSize(1);
        Page<Place> placePage = adminCustomBannerRepository.getPlaceList(pageRequest,
            savedBannerId);

        // then
        Assertions.assertEquals(placePage.getTotalElements(), 2L);
        Assertions.assertEquals(placePage.getTotalPages(), 2);

    }

    @Test
    @DisplayName("장소 할당 해제 - 배너 목록 삭제")
    public void testDeleteBannerPlaceFromBannerRemoval() {
        // given
        Banner banner1 = Banner.builder().build();
        Banner banner2 = Banner.builder().build();
        List<Banner> savedBannerList = bannerRepository.saveAll(List.of(banner1, banner2));
        List<Long> savedBannerIdList = savedBannerList.stream().map(Banner::getId).toList();

        Place place = Place.builder().build();
        Place savedPlace = placeRepository.save(place);

        for (Banner savedBanner : savedBannerList) {
            bannerPlaceMappingRepository.save(
                BannerPlaceMapping.builder()
                    .banner(savedBanner)
                    .place(savedPlace)
                    .build()
            );
        }

        DeleteBannerRequestDto deleteBannerRequestDto = new DeleteBannerRequestDto();
        ReflectionTestUtils.setField(deleteBannerRequestDto, "bannerIdList",
            savedBannerIdList);

        // when
        adminCustomBannerRepository.deleteBannerPlace(deleteBannerRequestDto);

        // then
        List<BannerPlaceMapping> bannerPlaceMappingList = bannerPlaceMappingRepository.findAllByBannerIdIn(
            savedBannerIdList);
        Assertions.assertEquals(bannerPlaceMappingList.size(), 0);

    }

}