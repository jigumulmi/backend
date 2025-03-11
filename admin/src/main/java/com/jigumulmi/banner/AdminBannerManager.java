package com.jigumulmi.banner;

import com.jigumulmi.aws.S3Manager;
import com.jigumulmi.banner.domain.Banner;
import com.jigumulmi.banner.dto.AdminCreateBannerImageS3KeyDto;
import com.jigumulmi.banner.dto.AdminUpdateBannerImageS3KeyDto;
import com.jigumulmi.banner.dto.request.CreateBannerRequestDto;
import com.jigumulmi.banner.dto.request.GetCandidatePlaceListRequestDto;
import com.jigumulmi.banner.dto.request.UpdateBannerRequestDto;
import com.jigumulmi.banner.dto.response.AdminBannerDetailResponseDto;
import com.jigumulmi.banner.dto.response.AdminBannerPlaceListResponseDto;
import com.jigumulmi.banner.dto.response.AdminBannerPlaceListResponseDto.BannerPlaceDto;
import com.jigumulmi.banner.dto.response.AdminBannerResponseDto;
import com.jigumulmi.banner.dto.response.CreateBannerResponseDto;
import com.jigumulmi.banner.repository.BannerRepository;
import com.jigumulmi.common.FileUtils;
import com.jigumulmi.common.PagedResponseDto;
import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;

@Component
@RequiredArgsConstructor
public class AdminBannerManager {

    private final S3Manager s3Manager;

    private final BannerRepository bannerRepository;
    private final AdminCustomBannerRepository adminCustomBannerRepository;

    private static String makeBannerImageS3Key() {
        return S3Manager.BANNER_IMAGE_S3_PREFIX + FileUtils.generateUniqueFilename();
    }

    public void saveBannerImageFile(MultipartFile image, String s3Key) {
        if (image == null) {
            return;
        }

        try {
            s3Manager.putObject(s3Manager.bucket, s3Key, image);
        } catch (IOException | SdkException e) {
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteBannerImageFile(String s3Key) {
        if (s3Key == null) {
            return;
        }

        try {
            s3Manager.deleteObject(s3Manager.bucket, s3Key);
        } catch (SdkException e) {
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteBannerImageFileList(List<String> s3KeyList) {
        if (s3KeyList.isEmpty()) {
            return;
        }

        try {
            List<ObjectIdentifier> objectIdentifierList = s3KeyList.stream()
                .filter(Objects::nonNull)
                .map(key -> ObjectIdentifier.builder().key(key).build())
                .collect(Collectors.toList());

            s3Manager.deleteObjects(s3Manager.bucket, objectIdentifierList);
        } catch (SdkException e) {
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public CreateBannerResponseDto saveBanner(CreateBannerRequestDto requestDto) {
        String outerImageS3Key = requestDto.getOuterImage() != null ? makeBannerImageS3Key() : null;
        String innerImageS3Key = requestDto.getInnerImage() != null ? makeBannerImageS3Key() : null;

        Banner banner = Banner.builder()
            .title(requestDto.getTitle())
            .outerImageS3Key(outerImageS3Key)
            .innerImageS3Key(innerImageS3Key)
            .isActive(requestDto.getIsActive())
            .build();

        bannerRepository.save(banner);

        return CreateBannerResponseDto.builder()
            .bannerId(banner.getId())
            .s3KeyDto(
                AdminCreateBannerImageS3KeyDto.builder()
                    .outerImage(outerImageS3Key)
                    .innerImage(innerImageS3Key)
                    .build()
            )
            .build();
    }

    public List<AdminBannerResponseDto> getBannerList() {
        return bannerRepository.findAll().stream().map(AdminBannerResponseDto::from).toList();
    }

    public void addBannerPlace(Long bannerId, List<Long> placeIdList) {
        adminCustomBannerRepository.batchInsertBannerPlace(bannerId, placeIdList);
    }

    public void removeBannerPlace(Long bannerId, @NotEmpty List<Long> placeIdList) {
        adminCustomBannerRepository.deleteBannerPlaceByBannerIdAndPlaceIdList(bannerId,
            placeIdList);
    }

    public AdminBannerDetailResponseDto getBannerDetail(Long bannerId) {
        Banner banner = getBannerEntity(bannerId);

        return AdminBannerDetailResponseDto.from(banner);
    }

    @Transactional(readOnly = true)
    public PagedResponseDto<BannerPlaceDto> getMappedPlaceList(Pageable pageable, Long bannerId) {
        Page<BannerPlaceDto> placePage = adminCustomBannerRepository.getAllMappedPlaceByBannerId(pageable,
            bannerId).map(BannerPlaceDto::from);

        return AdminBannerPlaceListResponseDto.of(placePage, pageable);
    }

    public Banner getBannerEntity(Long bannerId) {
        return bannerRepository.findById(bannerId)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));
    }

    @Transactional
    public void updateBannerBasic(Long bannerId, UpdateBannerRequestDto requestDto) {
        Banner banner = getBannerEntity(bannerId);

        banner.updateBasic(requestDto.getTitle(), requestDto.getIsActive());
    }

    @Transactional
    public AdminUpdateBannerImageS3KeyDto updateBannerOuterImage(Long bannerId) {
        Banner banner = getBannerEntity(bannerId);
        String oldS3Key = banner.getOuterImageS3Key();

        String newS3Key = makeBannerImageS3Key();
        banner.updateOuterS3ImageKey(newS3Key);

        return AdminUpdateBannerImageS3KeyDto.builder()
            .newKey(newS3Key)
            .oldKey(oldS3Key)
            .build();
    }

    @Transactional
    public AdminUpdateBannerImageS3KeyDto updateBannerInnerImage(Long bannerId) {
        Banner banner = getBannerEntity(bannerId);
        String oldS3Key = banner.getInnerImageS3Key();

        String newS3Key = makeBannerImageS3Key();
        banner.updateInnerS3ImageKey(newS3Key);

        return AdminUpdateBannerImageS3KeyDto.builder()
            .newKey(newS3Key)
            .oldKey(oldS3Key)
            .build();
    }

    @Transactional
    public List<String> deleteBanner(Long bannerId) {
        Banner banner = getBannerEntity(bannerId);

        String outerImageS3Key = banner.getOuterImageS3Key();
        String innerImageS3Key = banner.getInnerImageS3Key();

        adminCustomBannerRepository.deleteBannerPlaceByBannerId(bannerId);
        bannerRepository.delete(banner);

        return List.of(outerImageS3Key, innerImageS3Key);
    }

    @Transactional(readOnly = true)
    public PagedResponseDto<BannerPlaceDto> getCandidatePlaceList(Pageable pageable,
        GetCandidatePlaceListRequestDto requestDto) {
        Page<BannerPlaceDto> placePage = adminCustomBannerRepository.getAllUnmappedPlaceByBannerIdAndFilters(
            pageable, requestDto).map(BannerPlaceDto::from);

        return AdminBannerPlaceListResponseDto.of(placePage, pageable);
    }

}
