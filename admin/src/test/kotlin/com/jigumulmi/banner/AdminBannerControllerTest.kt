package com.jigumulmi.banner

import com.fasterxml.jackson.databind.ObjectMapper
import com.jigumulmi.annotation.ControllerTest
import com.jigumulmi.banner.dto.AdminCreateBannerImageS3KeyDto
import com.jigumulmi.banner.dto.request.BannerPlaceMappingRequestDto
import com.jigumulmi.banner.dto.request.CreateBannerRequestDto
import com.jigumulmi.banner.dto.request.GetCandidatePlaceListRequestDto
import com.jigumulmi.banner.dto.request.UpdateBannerRequestDto
import com.jigumulmi.banner.dto.response.AdminBannerDetailResponseDto
import com.jigumulmi.banner.dto.response.AdminBannerPlaceListResponseDto
import com.jigumulmi.banner.dto.response.AdminBannerResponseDto
import com.jigumulmi.banner.dto.response.CreateBannerResponseDto
import com.jigumulmi.common.AdminPagedResponseDto
import com.jigumulmi.place.dto.response.PlaceCategoryDto
import com.jigumulmi.place.dto.response.SubwayStationResponseDto
import com.jigumulmi.place.vo.PlaceCategory
import com.jigumulmi.place.vo.PlaceCategoryGroup
import com.jigumulmi.security.MockMember
import com.jigumulmi.utils.MultipartTestUtils
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalDateTime
import java.util.*

@ControllerTest(AdminBannerController::class)
internal class AdminBannerControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var adminBannerService: AdminBannerService

    @Test
    @DisplayName("배너 생성")
    @MockMember(isAdmin = true)
    @Throws(
        Exception::class
    )
    fun testCreateBanner() {
        // given
        val createBannerRequestDto = CreateBannerRequestDto(
            "testTitle",
            MultipartTestUtils.createMockFile("outerImage"),
            MultipartTestUtils.createMockFile("innerImage"),
            false
        )

        val createBannerResponseDto =
            CreateBannerResponseDto(bannerId = 1L, s3KeyDto = AdminCreateBannerImageS3KeyDto())

        BDDMockito.given(
            adminBannerService.createBanner(any<CreateBannerRequestDto>())
        ).willReturn(
            createBannerResponseDto
        )

        // when
        val perform = mockMvc.perform(
            MockMvcRequestBuilders.multipart("/admin/banner")
                .file(createBannerRequestDto.outerImage as MockMultipartFile)
                .file(createBannerRequestDto.innerImage as MockMultipartFile)
                .param("title", createBannerRequestDto.title)
                .param("isActive", createBannerRequestDto.isActive.toString())
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(
                MockMvcResultMatchers.content()
                    .json(objectMapper.writeValueAsString(createBannerResponseDto))
            )
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    @DisplayName("배너 목록 조회")
    @MockMember(isAdmin = true)
    @Throws(
        Exception::class
    )
    fun testGetBannerList() {
        // given
        val responseDto = AdminBannerResponseDto(
            id = 1L,
            title = "testTitle",
            modifiedAt = LocalDateTime.now(),
            isActive = false
        )
        val responseDtoList = listOf(responseDto)
        BDDMockito.given(adminBannerService.getBannerList()).willReturn(responseDtoList)

        // when
        val perform = mockMvc.perform(
            MockMvcRequestBuilders.get("/admin/banner")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(
                MockMvcResultMatchers.content()
                    .json(objectMapper.writeValueAsString(responseDtoList))
            )
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    @DisplayName("장소 할당")
    @MockMember(isAdmin = true)
    @Throws(
        Exception::class
    )
    fun testCreateBannerPlace() {
        // given
        val bannerId = 1L

        val bannerPlaceMappingRequestDto =
            BannerPlaceMappingRequestDto(placeIdList = listOf(1L, 2L))

        BDDMockito.willDoNothing().given(adminBannerService)
            .addBannerPlace(bannerId, bannerPlaceMappingRequestDto)

        // when
        val perform = mockMvc.perform(
            MockMvcRequestBuilders.post("/admin/banner/{bannerId}/place", bannerId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bannerPlaceMappingRequestDto))
        )

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    @DisplayName("장소 할당 해제")
    @MockMember(isAdmin = true)
    @Throws(
        Exception::class
    )
    fun testDeleteBannerPlace() {
        // given
        val bannerId = 1L

        val bannerPlaceMappingRequestDto =
            BannerPlaceMappingRequestDto(placeIdList = listOf(1L, 2L))

        BDDMockito.willDoNothing().given(adminBannerService)
            .removeBannerPlace(bannerId, bannerPlaceMappingRequestDto)

        // when
        val perform = mockMvc.perform(
            MockMvcRequestBuilders.delete("/admin/banner/{bannerId}/place", bannerId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bannerPlaceMappingRequestDto))
        )

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    @DisplayName("배너 상세 조회")
    @MockMember(isAdmin = true)
    @Throws(
        Exception::class
    )
    fun testGetBannerDetail() {
        // given
        val bannerId = 1L

        val responseDto = AdminBannerDetailResponseDto(
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now(),
            id = 1L,
            title = "testTitle",
            outerImageS3Key = UUID.randomUUID().toString(),
            innerImageS3Key = UUID.randomUUID().toString(),
            isActive = true
        )
        BDDMockito.given(adminBannerService.getBannerDetail(bannerId)).willReturn(responseDto)

        // when
        val perform = mockMvc.perform(
            MockMvcRequestBuilders.get("/admin/banner/{bannerId}", bannerId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(
                MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(responseDto))
            )
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    @DisplayName("연관 장소 목록 조회")
    @MockMember(isAdmin = true)
    @Throws(
        Exception::class
    )
    fun testBannerPlaceList() {
        // given
        val bannerId = 1L

        val bannerPlaceDto = AdminBannerPlaceListResponseDto.BannerPlaceDto(
            id = 1L,
            name = "testTitle",
            subwayStation = SubwayStationResponseDto.builder().id(1L).stationName("홍대입구").build(),
            categoryList = listOf(
                PlaceCategoryDto.builder().categoryGroup(PlaceCategoryGroup.CAFE)
                    .category(PlaceCategory.BEVERAGE).build()
            )
        )
        val responseDto = AdminBannerPlaceListResponseDto(
            data = listOf(bannerPlaceDto),
            page = AdminPagedResponseDto.PageDto(
                currentPage = 1,
                totalPage = 1,
                totalCount = 1L
            )
        )

        BDDMockito.given(
            adminBannerService.getMappedPlaceList(
                any<PageRequest>(), eq(bannerId)
            )
        )
            .willReturn(responseDto)

        // when
        val perform = mockMvc.perform(
            MockMvcRequestBuilders.get("/admin/banner/{bannerId}/place", bannerId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(
                MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(responseDto))
            )
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    @DisplayName("배너 정보 수정")
    @MockMember(isAdmin = true)
    @Throws(
        Exception::class
    )
    fun testUpdateBannerBasic() {
        // given
        val bannerId = 1L

        val updateBannerRequestDto = UpdateBannerRequestDto(title = "testTitle", isActive = false)
        BDDMockito.willDoNothing().given(adminBannerService)
            .updateBannerBasic(bannerId, updateBannerRequestDto)

        // when
        val perform = mockMvc.perform(
            MockMvcRequestBuilders.put("/admin/banner/{bannerId}", bannerId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateBannerRequestDto))
        )

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    @DisplayName("배너 외부 이미지 수정")
    @MockMember(isAdmin = true)
    @Throws(
        Exception::class
    )
    fun testUpdateBannerOuterImage() {
        // given
        val bannerId = 1L

        val outerImage = MultipartTestUtils.createMockFile("outerImage")

        BDDMockito.willDoNothing().given(adminBannerService)
            .updateBannerOuterImage(bannerId, outerImage)

        // when
        val perform = mockMvc.perform(
            MockMvcRequestBuilders.multipart(
                HttpMethod.PUT,
                "/admin/banner/{bannerId}/outerImage",
                bannerId
            )
                .file(outerImage)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    @DisplayName("배너 내부 이미지 수정")
    @MockMember(isAdmin = true)
    @Throws(
        Exception::class
    )
    fun testUpdateBannerInnerImage() {
        // given
        val bannerId = 1L

        val innerImage = MultipartTestUtils.createMockFile("innerImage")

        BDDMockito.willDoNothing().given(adminBannerService)
            .updateBannerInnerImage(bannerId, innerImage)

        // when
        val perform = mockMvc.perform(
            MockMvcRequestBuilders.multipart(
                HttpMethod.PUT,
                "/admin/banner/{bannerId}/innerImage",
                bannerId
            )
                .file(innerImage)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    @DisplayName("배너 삭제")
    @MockMember(isAdmin = true)
    @Throws(
        Exception::class
    )
    fun testDeleteBannerList() {
        // given
        val bannerId = 1L
        BDDMockito.willDoNothing().given(adminBannerService).deleteBanner(bannerId)

        // when
        val perform = mockMvc.perform(
            MockMvcRequestBuilders.delete("/admin/banner/{bannerId}", bannerId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    @DisplayName("할당 가능한 장소 목록 조회")
    @MockMember(isAdmin = true)
    @Throws(
        Exception::class
    )
    fun testGetCandidatePlaceList() {
        // given
        val bannerId = 1L

        val bannerPlaceDto = AdminBannerPlaceListResponseDto.BannerPlaceDto(
            id = 1L,
            name = "testTitle",
            subwayStation = SubwayStationResponseDto.builder().id(1L).stationName("홍대입구").build(),
            categoryList = listOf(
                PlaceCategoryDto.builder().categoryGroup(PlaceCategoryGroup.CAFE)
                    .category(PlaceCategory.BEVERAGE).build()
            )
        )
        val responseDto = AdminBannerPlaceListResponseDto(
            data = listOf(bannerPlaceDto),
            page = AdminPagedResponseDto.PageDto(
                currentPage = 1,
                totalPage = 1,
                totalCount = 1L
            )
        )

        BDDMockito.given(
            adminBannerService.getCandidatePlaceList(
                any<PageRequest>(),
                any<GetCandidatePlaceListRequestDto>()
            )
        )
            .willReturn(responseDto)

        // when
        val perform = mockMvc.perform(
            MockMvcRequestBuilders.get("/admin/banner/place")
                .queryParam("bannerId", bannerId.toString())
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(
                MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(responseDto))
            )
            .andDo(MockMvcResultHandlers.print())
    }

}