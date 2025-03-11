package banner;

import banner.dto.request.BannerPlaceMappingRequestDto;
import banner.dto.request.GetCandidatePlaceListRequestDto;
import com.jigumulmi.banner.domain.Banner;
import com.jigumulmi.banner.domain.BannerPlaceMapping;
import com.jigumulmi.banner.repository.BannerRepository;
import com.jigumulmi.common.annotation.RepositoryTest;
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
        adminCustomBannerRepository.batchInsertBannerPlace(savedBannerId, savedPlaceIdList);

        // then
        List<BannerPlaceMapping> bannerPlaceMappingList = bannerPlaceMappingRepository.findAllByBannerId(
            savedBannerId);
        Assertions.assertEquals(bannerPlaceMappingList.size(), 2);

    }

    @Test
    @DisplayName("장소 할당 해제")
    public void testDeleteBannerPlaceByBannerIdAndPlaceIdList() {
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
        adminCustomBannerRepository.deleteBannerPlaceByBannerIdAndPlaceIdList(savedBannerId,
            savedPlaceIdList);

        // then
        List<BannerPlaceMapping> bannerPlaceMappingList = bannerPlaceMappingRepository.findAllByBannerId(
            savedBannerId);
        Assertions.assertEquals(bannerPlaceMappingList.size(), 0);

    }

    @Test
    @DisplayName("연관 장소 목록 조회 (페이지네이션 적용)")
    public void testGetAllMappedPlaceByBannerId() {
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
        Page<Place> placePage = adminCustomBannerRepository.getAllMappedPlaceByBannerId(pageRequest,
            savedBannerId);

        // then
        Assertions.assertEquals(placePage.getTotalElements(), 2L);
        Assertions.assertEquals(placePage.getTotalPages(), 2);

    }

    @Test
    @DisplayName("장소 할당 해제 - 배너 삭제")
    public void testDeleteBannerPlaceByBannerId() {
        // given
        Banner banner = Banner.builder().build();
        Banner savedBanner = bannerRepository.save(banner);
        Long bannerId = savedBanner.getId();

        Place place = Place.builder().build();
        Place savedPlace = placeRepository.save(place);

        bannerPlaceMappingRepository.save(
            BannerPlaceMapping.builder()
                .banner(savedBanner)
                .place(savedPlace)
                .build()
        );

        // when
        adminCustomBannerRepository.deleteBannerPlaceByBannerId(bannerId);

        // then
        List<BannerPlaceMapping> bannerPlaceMappingList = bannerPlaceMappingRepository.findAllByBannerId(
            bannerId);
        Assertions.assertEquals(bannerPlaceMappingList.size(), 0);

    }

    @Test
    @DisplayName("할당 가능 장소 목록 조회 (페이지네이션 적용)")
    public void testGetAllUnmappedPlaceByBannerIdAndFilters() {
        // given
        Banner banner = Banner.builder().build();
        Banner savedBanner = bannerRepository.save(banner);
        Long savedBannerId = savedBanner.getId();

        Place place1 = Place.builder().isApproved(true).build();
        Place place2 = Place.builder().isApproved(true).build();
        List<Place> savedPlaceListForMapping = placeRepository.saveAll(
            List.of(place1, place2)
        );
        Place place3 = Place.builder().isApproved(true).build();
        Place place4 = Place.builder().isApproved(true).build();
        placeRepository.saveAll(List.of(place3, place4));

        for (Place savedPlace : savedPlaceListForMapping) {
            bannerPlaceMappingRepository.save(
                BannerPlaceMapping.builder()
                    .banner(banner)
                    .place(savedPlace)
                    .build()
            );
        }

        GetCandidatePlaceListRequestDto getCandidatePlaceListRequestDto = new GetCandidatePlaceListRequestDto(
            savedBannerId, null, null, null, null, null, null);

        // when
        PageRequest pageRequest = PageRequest.ofSize(1);
        Page<Place> placePage = adminCustomBannerRepository.getAllUnmappedPlaceByBannerIdAndFilters(
            pageRequest, getCandidatePlaceListRequestDto);

        // then
        Assertions.assertEquals(placePage.getTotalElements(), 2L);
        Assertions.assertEquals(placePage.getTotalPages(), 2);

    }

}