package com.jigumulmi.admin.banner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jigumulmi.admin.banner.dto.request.BannerPlaceMappingRequestDto;
import com.jigumulmi.admin.banner.dto.request.CreateBannerRequestDto;
import com.jigumulmi.admin.banner.dto.request.DeleteBannerRequestDto;
import com.jigumulmi.admin.banner.dto.request.UpdateBannerRequestDto;
import com.jigumulmi.admin.banner.dto.response.AdminBannerDetailResponseDto;
import com.jigumulmi.admin.banner.dto.response.AdminBannerPlaceListResponseDto;
import com.jigumulmi.admin.banner.dto.response.AdminBannerPlaceListResponseDto.BannerPlaceDto;
import com.jigumulmi.admin.banner.dto.response.AdminBannerResponseDto;
import com.jigumulmi.common.MultipartTestUtils;
import com.jigumulmi.config.common.PageDto;
import com.jigumulmi.config.security.MockMember;
import com.jigumulmi.config.security.SecurityConfig;
import com.jigumulmi.place.dto.response.PlaceCategoryDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import com.jigumulmi.place.vo.PlaceCategory;
import com.jigumulmi.place.vo.PlaceCategoryGroup;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(AdminBannerController.class)
@Import(SecurityConfig.class)
@MockBean(JpaMetamodelMappingContext.class)
class AdminBannerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminBannerService adminBannerService;

    @Test
    @DisplayName("배너 생성")
    @MockMember(isAdmin = true)
    public void testCreateBanner() throws Exception {
        // given
        CreateBannerRequestDto createBannerRequestDto = new CreateBannerRequestDto(
            "testTitle",
            MultipartTestUtils.createMockFile("outerImage"),
            MultipartTestUtils.createMockFile("innerImage"),
            false
        );

        willDoNothing().given(adminBannerService).createBanner(createBannerRequestDto);

        // when
        ResultActions perform = mockMvc.perform(
            multipart("/admin/banner")
                .file((MockMultipartFile) createBannerRequestDto.getOuterImage())
                .file((MockMultipartFile) createBannerRequestDto.getInnerImage())
                .param("title", createBannerRequestDto.getTitle())
                .param("isActive", createBannerRequestDto.getIsActive().toString())
                .with(csrf())
        );

        // then
        perform
            .andExpect(status().isCreated())
            .andDo(print());
    }

    private static Stream<Arguments> getParams() {
        return Stream.of(
            Arguments.of(null, true),
            Arguments.of("", true),
            Arguments.of(" ", true),
            Arguments.of("testtesttesttesttesttesttesttesttesttesttesttesttest", true),
            Arguments.of("test", null)
        );
    }

    @ParameterizedTest
    @MethodSource("getParams")
    @DisplayName("배너 생성 요청 에러")
    @MockMember(isAdmin = true)
    public void testCreateBanner_422(String title, Boolean isActive) throws Exception {
        // given
        CreateBannerRequestDto createBannerRequestDto = new CreateBannerRequestDto(
            title,
            MultipartTestUtils.createMockFile("outerImage"),
            MultipartTestUtils.createMockFile("innerImage"),
            isActive
        );

        willDoNothing().given(adminBannerService).createBanner(createBannerRequestDto);

        // when
        MockHttpServletRequestBuilder requestBuilder = multipart("/admin/banner")
            .file((MockMultipartFile) createBannerRequestDto.getOuterImage())
            .file((MockMultipartFile) createBannerRequestDto.getInnerImage())
            .with(csrf());

        if (createBannerRequestDto.getTitle() != null) {
            requestBuilder = requestBuilder.param("title", createBannerRequestDto.getTitle());
        }
        if (createBannerRequestDto.getIsActive() != null) {
            requestBuilder = requestBuilder.param("isActive",
                createBannerRequestDto.getIsActive().toString());
        }

        ResultActions perform = mockMvc.perform(requestBuilder);

        // then
        perform
            .andExpect(status().isUnprocessableEntity())
            .andDo(print());
    }

    @Test
    @DisplayName("배너 목록 조회")
    @MockMember(isAdmin = true)
    public void testGetBannerList() throws Exception {
        // given
        AdminBannerResponseDto responseDto = AdminBannerResponseDto.builder()
            .id(1L)
            .title("testTitle")
            .modifiedAt(LocalDateTime.now())
            .isActive(false)
            .build();
        List<AdminBannerResponseDto> responseDtoList = List.of(responseDto);
        given(adminBannerService.getBannerList()).willReturn(responseDtoList);

        // when
        ResultActions perform = mockMvc.perform(
            get("/admin/banner")
                .with(csrf())
        );

        // then
        perform
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(responseDtoList)))
            .andDo(print());
    }

    @Test
    @DisplayName("장소 할당")
    @MockMember(isAdmin = true)
    public void testCreateBannerPlace() throws Exception {
        // given
        Long bannerId = 1L;

        BannerPlaceMappingRequestDto bannerPlaceMappingRequestDto = new BannerPlaceMappingRequestDto();
        ReflectionTestUtils.setField(bannerPlaceMappingRequestDto, "placeIdList", List.of(1L, 2L));

        willDoNothing().given(adminBannerService)
            .addBannerPlace(bannerId, bannerPlaceMappingRequestDto);

        // when
        ResultActions perform = mockMvc.perform(
            post("/admin/banner/{bannerId}/place", bannerId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bannerPlaceMappingRequestDto))
        );

        // then
        perform
            .andExpect(status().isCreated())
            .andDo(print());

    }

    @Test
    @DisplayName("장소 할당 해제")
    @MockMember(isAdmin = true)
    public void testDeleteBannerPlace() throws Exception {
        // given
        Long bannerId = 1L;

        BannerPlaceMappingRequestDto bannerPlaceMappingRequestDto = new BannerPlaceMappingRequestDto();
        ReflectionTestUtils.setField(bannerPlaceMappingRequestDto, "placeIdList", List.of(1L, 2L));

        willDoNothing().given(adminBannerService)
            .removeBannerPlace(bannerId, bannerPlaceMappingRequestDto);

        // when
        ResultActions perform = mockMvc.perform(
            delete("/admin/banner/{bannerId}/place", bannerId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bannerPlaceMappingRequestDto))
        );

        // then
        perform
            .andExpect(status().isNoContent())
            .andDo(print());

    }

    @Test
    @DisplayName("배너 상세 조회")
    @MockMember(isAdmin = true)
    public void testGetBannerDetail() throws Exception {
        // given
        Long bannerId = 1L;

        AdminBannerDetailResponseDto responseDto = AdminBannerDetailResponseDto.builder()
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .id(1L)
            .title("testTitle")
            .outerImageS3Key(UUID.randomUUID().toString())
            .innerImageS3Key(UUID.randomUUID().toString())
            .isActive(true)
            .build();

        given(adminBannerService.getBannerDetail(bannerId)).willReturn(responseDto);

        // when
        ResultActions perform = mockMvc.perform(
            get("/admin/banner/{bannerId}", bannerId)
                .with(csrf())
        );

        // then
        perform
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(responseDto)))
            .andDo(print());

    }

    @Test
    @DisplayName("연관 장소 목록 조회")
    @MockMember(isAdmin = true)
    public void testBannerPlaceList() throws Exception {
        // given
        Long bannerId = 1L;

        BannerPlaceDto bannerPlaceDto = BannerPlaceDto.builder()
            .id(1L)
            .name("testTitle")
            .subwayStation(
                SubwayStationResponseDto.builder().id(1L).stationName("홍대입구").build()
            )
            .categoryList(
                List.of(PlaceCategoryDto.builder().categoryGroup(PlaceCategoryGroup.CAFE)
                    .category(PlaceCategory.BEVERAGE).build())
            )
            .build();
        AdminBannerPlaceListResponseDto responseDto = AdminBannerPlaceListResponseDto.builder()
            .data(List.of(bannerPlaceDto))
            .page(
                PageDto.builder()
                    .currentPage(1)
                    .totalPage(1)
                    .totalCount(1L)
                    .build()
            )
            .build();

        given(adminBannerService.getMappedPlaceList(any(PageRequest.class), eq(bannerId)))
            .willReturn(responseDto);

        // when
        ResultActions perform = mockMvc.perform(
            get("/admin/banner/{bannerId}/place", bannerId)
                .with(csrf())
        );

        // then
        perform
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(responseDto)))
            .andDo(print());

    }

    @Test
    @DisplayName("배너 정보 수정")
    @MockMember(isAdmin = true)
    public void testUpdateBanner() throws Exception {
        // given
        Long bannerId = 1L;

        UpdateBannerRequestDto updateBannerRequestDto = new UpdateBannerRequestDto();
        ReflectionTestUtils.setField(updateBannerRequestDto, "title", "testTitle");
        ReflectionTestUtils.setField(updateBannerRequestDto, "isActive", false);

        willDoNothing().given(adminBannerService).updateBanner(bannerId, updateBannerRequestDto);

        // when
        ResultActions perform = mockMvc.perform(
            put("/admin/banner/{bannerId}", bannerId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateBannerRequestDto))
        );

        // then
        perform
            .andExpect(status().isCreated())
            .andDo(print());

    }

    @Test
    @DisplayName("배너 외부 이미지 수정")
    @MockMember(isAdmin = true)
    public void testUpdateBannerOuterImage() throws Exception {
        // given
        Long bannerId = 1L;

        MockMultipartFile outerImage = MultipartTestUtils.createMockFile("outerImage");

        willDoNothing().given(adminBannerService).updateBannerOuterImage(bannerId, outerImage);

        // when
        ResultActions perform = mockMvc.perform(
            multipart(HttpMethod.PUT, "/admin/banner/{bannerId}/outerImage", bannerId)
                .file(outerImage)
                .with(csrf())
        );

        // then
        perform
            .andExpect(status().isCreated())
            .andDo(print());

    }

    @Test
    @DisplayName("배너 내부 이미지 수정")
    @MockMember(isAdmin = true)
    public void testUpdateBannerInnerImage() throws Exception {
        // given
        Long bannerId = 1L;

        MockMultipartFile innerImage = MultipartTestUtils.createMockFile("innerImage");

        willDoNothing().given(adminBannerService).updateBannerInnerImage(bannerId, innerImage);

        // when
        ResultActions perform = mockMvc.perform(
            multipart(HttpMethod.PUT, "/admin/banner/{bannerId}/innerImage", bannerId)
                .file(innerImage)
                .with(csrf())
        );

        // then
        perform
            .andExpect(status().isCreated())
            .andDo(print());

    }

    @Test
    @DisplayName("배너 삭제")
    @MockMember(isAdmin = true)
    public void testDeleteBannerList() throws Exception {
        // given
        DeleteBannerRequestDto deleteBannerRequestDto = new DeleteBannerRequestDto();
        ReflectionTestUtils.setField(deleteBannerRequestDto, "bannerIdList", List.of(1L, 2L));

        willDoNothing().given(adminBannerService).deleteBannerList(deleteBannerRequestDto);

        // when
        ResultActions perform = mockMvc.perform(
            delete("/admin/banner")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteBannerRequestDto))
        );

        // then
        perform
            .andExpect(status().isNoContent())
            .andDo(print());

    }

}