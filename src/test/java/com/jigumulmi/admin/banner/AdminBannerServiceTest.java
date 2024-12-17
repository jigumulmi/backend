package com.jigumulmi.admin.banner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.jigumulmi.admin.banner.dto.request.CreateBannerRequestDto;
import com.jigumulmi.admin.banner.dto.request.DeleteBannerRequestDto;
import com.jigumulmi.admin.banner.dto.request.UpdateBannerRequestDto;
import com.jigumulmi.aws.S3Service;
import com.jigumulmi.banner.domain.Banner;
import com.jigumulmi.banner.repository.BannerRepository;
import com.jigumulmi.common.MultipartTestUtils;
import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.security.MockMember;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class AdminBannerServiceTest {

    @InjectMocks
    private AdminBannerService adminBannerService;

    @Mock
    private S3Service s3Service;

    @Mock
    private AdminCustomBannerRepository adminCustomBannerRepository;
    @Mock
    private BannerRepository bannerRepository;

    @BeforeEach
    void beforeEach() {
        ReflectionTestUtils.setField(s3Service, "bucket", "testBucket");
    }

    private static Stream<Arguments> getImageParams() {
        return Stream.of(
            Arguments.of(MultipartTestUtils.createMockFile("outerImage"), null, 1),
            Arguments.of(null, MultipartTestUtils.createMockFile("innerImage"), 1)
        );
    }

    @ParameterizedTest
    @MethodSource("getImageParams")
    @DisplayName("배너 생성")
    public void testCreateBanner(MockMultipartFile outerImage, MockMultipartFile innerImage,
        int invokeCount) throws IOException {
        // given
        CreateBannerRequestDto createBannerRequestDto = new CreateBannerRequestDto(
            "testTitle", outerImage, innerImage, false
        );

        // when
        adminBannerService.createBanner(createBannerRequestDto);

        // then
        verify(s3Service, times(invokeCount)).putObject(
            eq(s3Service.bucket), any(String.class), any(MultipartFile.class)
        );
        verify(bannerRepository, times(1)).save(any(Banner.class));

    }

    @Test
    @DisplayName("배너 생성 실패 예외처리")
    public void testCreateBanner_S3_Exception() throws IOException {
        // given
        CreateBannerRequestDto createBannerRequestDto = new CreateBannerRequestDto(
            "testTitle", MultipartTestUtils.createMockFile("outerImage"), null, false
        );

        willThrow(new IOException()).given(s3Service)
            .putObject(any(String.class), any(String.class), any(MultipartFile.class));

        // when

        // then
        Assertions.assertThrows(CustomException.class,
            () -> adminBannerService.createBanner(createBannerRequestDto));
        verify(bannerRepository, never()).save(any(Banner.class));

    }

    @Test
    @DisplayName("배너 정보 수정")
    @MockMember(isAdmin = true)
    public void testUpdateBanner() {
        // given
        Long bannerId = 1L;

        Banner banner = Banner.builder()
            .title("title")
            .isActive(false)
            .build();
        ReflectionTestUtils.setField(banner, "id", bannerId);

        UpdateBannerRequestDto updateBannerRequestDto = new UpdateBannerRequestDto();
        String newTitle = "newTitle";
        Boolean newIsActive = true;
        ReflectionTestUtils.setField(updateBannerRequestDto, "title", newTitle);
        ReflectionTestUtils.setField(updateBannerRequestDto, "isActive", newIsActive);

        given(bannerRepository.findById(bannerId)).willReturn(Optional.of(banner));
        given(bannerRepository.save(banner)).willReturn(banner);

        // when
        adminBannerService.updateBanner(bannerId, updateBannerRequestDto);

        // then
        Assertions.assertEquals(bannerId, banner.getId());
        Assertions.assertEquals(newTitle, banner.getTitle());
        Assertions.assertEquals(newIsActive, banner.getIsActive());

    }

    @Test
    @DisplayName("배너 외부 이미지 수정")
    @MockMember(isAdmin = true)
    public void testUpdateBannerOuterImage() {
        // given
        Long bannerId = 1L;

        Banner banner = Banner.builder()
            .title("title")
            .isActive(false)
            .build();
        ReflectionTestUtils.setField(banner, "id", bannerId);
        String oldKey = banner.getOuterImageS3Key();

        MockMultipartFile outerImage = MultipartTestUtils.createMockFile("outerImage");

        given(bannerRepository.findById(bannerId)).willReturn(Optional.of(banner));
        given(bannerRepository.save(banner)).willReturn(banner);

        // when
        adminBannerService.updateBannerOuterImage(bannerId, outerImage);

        // then
        Assertions.assertEquals(bannerId, banner.getId());

        String newKey = banner.getOuterImageS3Key();
        Assertions.assertNotEquals(oldKey, newKey);

    }

    @Test
    @DisplayName("배너 내부 이미지 수정")
    @MockMember(isAdmin = true)
    public void testUpdateBannerInnerImage() {
        // given
        Long bannerId = 1L;

        Banner banner = Banner.builder()
            .title("title")
            .isActive(false)
            .build();
        ReflectionTestUtils.setField(banner, "id", bannerId);
        String oldKey = banner.getInnerImageS3Key();

        MockMultipartFile innerImage = MultipartTestUtils.createMockFile("innerImage");

        given(bannerRepository.findById(bannerId)).willReturn(Optional.of(banner));
        given(bannerRepository.save(banner)).willReturn(banner);

        // when
        adminBannerService.updateBannerInnerImage(bannerId, innerImage);

        // then
        Assertions.assertEquals(bannerId, banner.getId());

        String newKey = banner.getInnerImageS3Key();
        Assertions.assertNotEquals(oldKey, newKey);

    }

    private static Stream<Arguments> getImageS3KeyParams() {
        return Stream.of(
            Arguments.of(null, null, 0),
            Arguments.of("outerImageKey", null, 1),
            Arguments.of(null, "innerImageKey", 1),
            Arguments.of("outerImageKey", "innerImageKey", 2)
        );
    }

    @ParameterizedTest
    @MethodSource("getImageS3KeyParams")
    @DisplayName("배너 목록 삭제")
    public void testDeleteBannerList(String outerImageKey, String innerImageKey, int deleteCount) {
        // given
        DeleteBannerRequestDto deleteBannerRequestDto = new DeleteBannerRequestDto();
        ReflectionTestUtils.setField(deleteBannerRequestDto, "bannerIdList", List.of(1L));

        Banner banner = Banner.builder()
            .title("title")
            .outerImageS3Key(outerImageKey)
            .innerImageS3Key(innerImageKey)
            .isActive(false)
            .build();
        ReflectionTestUtils.setField(banner, "id", 1L);

        List<Banner> bannerList = List.of(banner);
        given(bannerRepository.findAllById(deleteBannerRequestDto.getBannerIdList())).willReturn(
            bannerList);
        willDoNothing().given(adminCustomBannerRepository)
            .deleteBannerPlace(deleteBannerRequestDto);
        willDoNothing().given(bannerRepository).deleteAllInBatch(bannerList);

        // when
        adminBannerService.deleteBannerList(deleteBannerRequestDto);

        // then
        verify(s3Service, times(1)).deleteObjects(eq(s3Service.bucket),
            argThat(list -> list.size() == deleteCount));

    }

}