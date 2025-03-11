package banner;

import banner.dto.request.BannerPlaceMappingRequestDto;
import banner.dto.request.CreateBannerRequestDto;
import banner.dto.request.GetCandidatePlaceListRequestDto;
import banner.dto.request.UpdateBannerRequestDto;
import banner.dto.response.AdminBannerDetailResponseDto;
import banner.dto.response.AdminBannerPlaceListResponseDto;
import banner.dto.response.AdminBannerPlaceListResponseDto.BannerPlaceDto;
import banner.dto.response.AdminBannerResponseDto;
import banner.dto.response.CreateBannerResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jigumulmi.common.MultipartTestUtils;
import com.jigumulmi.common.PagedResponseDto.PageDto;
import com.jigumulmi.common.annotation.ControllerTest;
import com.jigumulmi.config.security.MockMember;
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
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ControllerTest(AdminBannerController.class)
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

        CreateBannerResponseDto createBannerResponseDto = CreateBannerResponseDto.builder()
            .bannerId(1L).build();

        BDDMockito.given(adminBannerService.createBanner(ArgumentMatchers.any(CreateBannerRequestDto.class))).willReturn(
            createBannerResponseDto);

        // when
        ResultActions perform = mockMvc.perform(
            MockMvcRequestBuilders.multipart("/admin/banner")
                .file((MockMultipartFile) createBannerRequestDto.getOuterImage())
                .file((MockMultipartFile) createBannerRequestDto.getInnerImage())
                .param("title", createBannerRequestDto.getTitle())
                .param("isActive", createBannerRequestDto.getIsActive().toString())
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        );

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(createBannerResponseDto)))
            .andDo(MockMvcResultHandlers.print());
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

        CreateBannerResponseDto createBannerResponseDto = CreateBannerResponseDto.builder()
            .bannerId(1L).build();

        BDDMockito.given(adminBannerService.createBanner(ArgumentMatchers.any(CreateBannerRequestDto.class))).willReturn(
            createBannerResponseDto);

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart("/admin/banner")
            .file((MockMultipartFile) createBannerRequestDto.getOuterImage())
            .file((MockMultipartFile) createBannerRequestDto.getInnerImage())
            .with(SecurityMockMvcRequestPostProcessors.csrf());

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
            .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
            .andDo(MockMvcResultHandlers.print());
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
        BDDMockito.given(adminBannerService.getBannerList()).willReturn(responseDtoList);

        // when
        ResultActions perform = mockMvc.perform(
            MockMvcRequestBuilders.get("/admin/banner")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        );

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(responseDtoList)))
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("장소 할당")
    @MockMember(isAdmin = true)
    public void testCreateBannerPlace() throws Exception {
        // given
        Long bannerId = 1L;

        BannerPlaceMappingRequestDto bannerPlaceMappingRequestDto = new BannerPlaceMappingRequestDto();
        ReflectionTestUtils.setField(bannerPlaceMappingRequestDto, "placeIdList", List.of(1L, 2L));

        BDDMockito.willDoNothing().given(adminBannerService)
            .addBannerPlace(bannerId, bannerPlaceMappingRequestDto);

        // when
        ResultActions perform = mockMvc.perform(
            MockMvcRequestBuilders.post("/admin/banner/{bannerId}/place", bannerId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bannerPlaceMappingRequestDto))
        );

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("장소 할당 해제")
    @MockMember(isAdmin = true)
    public void testDeleteBannerPlace() throws Exception {
        // given
        Long bannerId = 1L;

        BannerPlaceMappingRequestDto bannerPlaceMappingRequestDto = new BannerPlaceMappingRequestDto();
        ReflectionTestUtils.setField(bannerPlaceMappingRequestDto, "placeIdList", List.of(1L, 2L));

        BDDMockito.willDoNothing().given(adminBannerService)
            .removeBannerPlace(bannerId, bannerPlaceMappingRequestDto);

        // when
        ResultActions perform = mockMvc.perform(
            MockMvcRequestBuilders.delete("/admin/banner/{bannerId}/place", bannerId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bannerPlaceMappingRequestDto))
        );

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(MockMvcResultHandlers.print());

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

        BDDMockito.given(adminBannerService.getBannerDetail(bannerId)).willReturn(responseDto);

        // when
        ResultActions perform = mockMvc.perform(
            MockMvcRequestBuilders.get("/admin/banner/{bannerId}", bannerId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        );

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(responseDto)))
            .andDo(MockMvcResultHandlers.print());

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

        BDDMockito.given(adminBannerService.getMappedPlaceList(ArgumentMatchers.any(PageRequest.class), ArgumentMatchers.eq(bannerId)))
            .willReturn(responseDto);

        // when
        ResultActions perform = mockMvc.perform(
            MockMvcRequestBuilders.get("/admin/banner/{bannerId}/place", bannerId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        );

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(responseDto)))
            .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("배너 정보 수정")
    @MockMember(isAdmin = true)
    public void testUpdateBannerBasic() throws Exception {
        // given
        Long bannerId = 1L;

        UpdateBannerRequestDto updateBannerRequestDto = new UpdateBannerRequestDto();
        ReflectionTestUtils.setField(updateBannerRequestDto, "title", "testTitle");
        ReflectionTestUtils.setField(updateBannerRequestDto, "isActive", false);

        BDDMockito.willDoNothing().given(adminBannerService).updateBannerBasic(bannerId, updateBannerRequestDto);

        // when
        ResultActions perform = mockMvc.perform(
            MockMvcRequestBuilders.put("/admin/banner/{bannerId}", bannerId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateBannerRequestDto))
        );

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("배너 외부 이미지 수정")
    @MockMember(isAdmin = true)
    public void testUpdateBannerOuterImage() throws Exception {
        // given
        Long bannerId = 1L;

        MockMultipartFile outerImage = MultipartTestUtils.createMockFile("outerImage");

        BDDMockito.willDoNothing().given(adminBannerService).updateBannerOuterImage(bannerId, outerImage);

        // when
        ResultActions perform = mockMvc.perform(
            MockMvcRequestBuilders.multipart(HttpMethod.PUT, "/admin/banner/{bannerId}/outerImage", bannerId)
                .file(outerImage)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        );

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("배너 내부 이미지 수정")
    @MockMember(isAdmin = true)
    public void testUpdateBannerInnerImage() throws Exception {
        // given
        Long bannerId = 1L;

        MockMultipartFile innerImage = MultipartTestUtils.createMockFile("innerImage");

        BDDMockito.willDoNothing().given(adminBannerService).updateBannerInnerImage(bannerId, innerImage);

        // when
        ResultActions perform = mockMvc.perform(
            MockMvcRequestBuilders.multipart(HttpMethod.PUT, "/admin/banner/{bannerId}/innerImage", bannerId)
                .file(innerImage)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        );

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("배너 삭제")
    @MockMember(isAdmin = true)
    public void testDeleteBannerList() throws Exception {
        // given
        long bannerId = 1L;
        BDDMockito.willDoNothing().given(adminBannerService).deleteBanner(bannerId);

        // when
        ResultActions perform = mockMvc.perform(
            MockMvcRequestBuilders.delete("/admin/banner/{bannerId}", bannerId)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        );

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("할당 가능한 장소 목록 조회")
    @MockMember(isAdmin = true)
    public void testGetCandidatePlaceList() throws Exception {
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

        BDDMockito.given(adminBannerService.getCandidatePlaceList(ArgumentMatchers.any(PageRequest.class),
            ArgumentMatchers.any(GetCandidatePlaceListRequestDto.class)))
            .willReturn(responseDto);

        // when
        ResultActions perform = mockMvc.perform(
            MockMvcRequestBuilders.get("/admin/banner/place")
                .queryParam("bannerId", String.valueOf(bannerId))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        );

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(responseDto)))
            .andDo(MockMvcResultHandlers.print());

    }

}