package com.jigumulmi.banner

import com.jigumulmi.annotation.RepositoryTest
import com.jigumulmi.banner.domain.Banner
import com.jigumulmi.banner.domain.BannerPlaceMapping
import com.jigumulmi.banner.dto.request.GetCandidatePlaceListRequestDto
import com.jigumulmi.banner.repository.BannerRepository
import com.jigumulmi.place.domain.Place
import com.jigumulmi.place.repository.PlaceRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest

@RepositoryTest
internal class AdminCustomBannerRepositoryTest {
    @Autowired
    private lateinit var adminCustomBannerRepository: AdminCustomBannerRepository

    @Autowired
    private lateinit var bannerRepository: BannerRepository

    @Autowired
    private lateinit var placeRepository: PlaceRepository

    @Autowired
    private lateinit var bannerPlaceMappingRepository: BannerPlaceMappingRepository

    @Test
    @DisplayName("배너에 장소 할당")
    fun testBatchInsertBannerPlace() {
        // given
        val banner = Banner.builder().build()
        val savedBanner = bannerRepository.save(banner)
        val savedBannerId = savedBanner.id

        val place1 = Place.builder().build()
        val place2 = Place.builder().build()
        val savedPlaceList = placeRepository.saveAll(
            listOf(place1, place2)
        )
        val savedPlaceIdList = savedPlaceList.stream().map { obj: Place -> obj.id }.toList()

        // when
        adminCustomBannerRepository.batchInsertBannerPlace(savedBannerId, savedPlaceIdList)

        // then
        val bannerPlaceMappingList = bannerPlaceMappingRepository.findAllByBannerId(
            savedBannerId
        )
        Assertions.assertEquals(bannerPlaceMappingList!!.size, 2)
    }

    @Test
    @DisplayName("장소 할당 해제")
    fun testDeleteBannerPlaceByBannerIdAndPlaceIdList() {
        // given
        val banner = Banner.builder().build()
        val savedBanner = bannerRepository.save(banner)
        val savedBannerId = savedBanner.id

        val place1 = Place.builder().build()
        val place2 = Place.builder().build()
        val savedPlaceList = placeRepository.saveAll(
            listOf(place1, place2)
        )
        val savedPlaceIdList = savedPlaceList.stream().map { obj: Place -> obj.id }.toList()

        for (savedPlace in savedPlaceList) {
            bannerPlaceMappingRepository.save(
                BannerPlaceMapping.builder()
                    .banner(banner)
                    .place(savedPlace)
                    .build()
            )
        }

        // when
        adminCustomBannerRepository.deleteBannerPlaceByBannerIdAndPlaceIdList(
            savedBannerId,
            savedPlaceIdList
        )

        // then
        val bannerPlaceMappingList = bannerPlaceMappingRepository.findAllByBannerId(
            savedBannerId
        )
        Assertions.assertEquals(bannerPlaceMappingList!!.size, 0)
    }

    @Test
    @DisplayName("연관 장소 목록 조회 (페이지네이션 적용)")
    fun testGetAllMappedPlaceByBannerId() {
        // given
        val banner = Banner.builder().build()
        val savedBanner = bannerRepository.save(banner)
        val savedBannerId = savedBanner.id

        val place1 = Place.builder().build()
        val place2 = Place.builder().build()
        val savedPlaceList = placeRepository.saveAll(
            listOf(place1, place2)
        )

        for (savedPlace in savedPlaceList) {
            bannerPlaceMappingRepository.save(
                BannerPlaceMapping.builder()
                    .banner(banner)
                    .place(savedPlace)
                    .build()
            )
        }

        // when
        val pageRequest = PageRequest.ofSize(1)
        val placePage = adminCustomBannerRepository.getAllMappedPlaceByBannerId(
            pageRequest,
            savedBannerId
        )

        // then
        Assertions.assertEquals(placePage.totalElements, 2L)
        Assertions.assertEquals(placePage.totalPages, 2)
    }

    @Test
    @DisplayName("장소 할당 해제 - 배너 삭제")
    fun testDeleteBannerPlaceByBannerId() {
        // given
        val banner = Banner.builder().build()
        val savedBanner = bannerRepository.save(banner)
        val bannerId = savedBanner.id

        val place = Place.builder().build()
        val savedPlace = placeRepository.save(place)

        bannerPlaceMappingRepository.save(
            BannerPlaceMapping.builder()
                .banner(savedBanner)
                .place(savedPlace)
                .build()
        )

        // when
        adminCustomBannerRepository.deleteBannerPlaceByBannerId(bannerId)

        // then
        val bannerPlaceMappingList = bannerPlaceMappingRepository.findAllByBannerId(
            bannerId
        )
        Assertions.assertEquals(bannerPlaceMappingList!!.size, 0)
    }

    @Test
    @DisplayName("할당 가능 장소 목록 조회 (페이지네이션 적용)")
    fun testGetAllUnmappedPlaceByBannerIdAndFilters() {
        // given
        val banner = Banner.builder().build()
        val savedBanner = bannerRepository.save(banner)
        val savedBannerId = savedBanner.id

        val place1 = Place.builder().isApproved(true).build()
        val place2 = Place.builder().isApproved(true).build()
        val savedPlaceListForMapping = placeRepository.saveAll(
            listOf(place1, place2)
        )
        val place3 = Place.builder().isApproved(true).build()
        val place4 = Place.builder().isApproved(true).build()
        placeRepository.saveAll(listOf(place3, place4))

        for (savedPlace in savedPlaceListForMapping) {
            bannerPlaceMappingRepository.save(
                BannerPlaceMapping.builder()
                    .banner(banner)
                    .place(savedPlace)
                    .build()
            )
        }

        val getCandidatePlaceListRequestDto = GetCandidatePlaceListRequestDto(
            savedBannerId, null, null, null, null, null, null
        )

        // when
        val pageRequest = PageRequest.ofSize(1)
        val placePage = adminCustomBannerRepository.getAllUnmappedPlaceByBannerIdAndFilters(
            pageRequest, getCandidatePlaceListRequestDto
        )

        // then
        Assertions.assertEquals(placePage.totalElements, 2L)
        Assertions.assertEquals(placePage.totalPages, 2)
    }
}