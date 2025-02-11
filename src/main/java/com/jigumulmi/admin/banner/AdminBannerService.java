package com.jigumulmi.admin.banner;

import com.jigumulmi.admin.banner.dto.request.BannerPlaceMappingRequestDto;
import com.jigumulmi.admin.banner.dto.request.CreateBannerRequestDto;
import com.jigumulmi.admin.banner.dto.request.GetCandidatePlaceListRequestDto;
import com.jigumulmi.admin.banner.dto.request.UpdateBannerRequestDto;
import com.jigumulmi.admin.banner.dto.response.AdminBannerDetailResponseDto;
import com.jigumulmi.admin.banner.dto.response.AdminBannerPlaceListResponseDto;
import com.jigumulmi.admin.banner.dto.response.AdminBannerPlaceListResponseDto.BannerPlaceDto;
import com.jigumulmi.admin.banner.dto.response.AdminBannerResponseDto;
import com.jigumulmi.admin.banner.dto.response.CreateBannerResponseDto;
import com.jigumulmi.aws.S3Manager;
import com.jigumulmi.banner.domain.Banner;
import com.jigumulmi.banner.repository.BannerRepository;
import com.jigumulmi.common.FileUtils;
import com.jigumulmi.common.PagedResponseDto;
import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;

@Service
@RequiredArgsConstructor
public class AdminBannerService {

    private final String BANNER_S3_PREFIX = "banner/";
    private final S3Manager s3Manager;

    private final BannerRepository bannerRepository;
    private final AdminCustomBannerRepository adminCustomBannerRepository;

    public CreateBannerResponseDto createBanner(CreateBannerRequestDto requestDto) {
        String outerImageS3Key = null;
        String innerImageS3Key = null;
        try {
            MultipartFile outerImage = requestDto.getOuterImage();
            if (outerImage != null) {
                outerImageS3Key = BANNER_S3_PREFIX + FileUtils.generateUniqueFilename(outerImage);
                s3Manager.putObject(s3Manager.bucket, outerImageS3Key, outerImage);
            }

            MultipartFile innerImage = requestDto.getInnerImage();
            if (innerImage != null) {
                innerImageS3Key = BANNER_S3_PREFIX + FileUtils.generateUniqueFilename(innerImage);
                s3Manager.putObject(s3Manager.bucket, innerImageS3Key, innerImage);
            }
        } catch (IOException | SdkException e) {
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

        Banner banner = Banner.builder()
            .title(requestDto.getTitle())
            .outerImageS3Key(outerImageS3Key)
            .innerImageS3Key(innerImageS3Key)
            .isActive(requestDto.getIsActive())
            .build();

        Banner savedBanner = bannerRepository.save(banner);

        return CreateBannerResponseDto.builder().bannerId(savedBanner.getId()).build();
    }

    public List<AdminBannerResponseDto> getBannerList() {
        return bannerRepository.findAll().stream().map(AdminBannerResponseDto::from).toList();
    }

    public void addBannerPlace(Long bannerId, BannerPlaceMappingRequestDto requestDto) {
        adminCustomBannerRepository.batchInsertBannerPlace(bannerId, requestDto.getPlaceIdList());
    }

    public void removeBannerPlace(Long bannerId, BannerPlaceMappingRequestDto requestDto) {
        adminCustomBannerRepository.deleteBannerPlaceByBannerIdAndPlaceIdList(bannerId,
            requestDto.getPlaceIdList());
    }

    public AdminBannerDetailResponseDto getBannerDetail(Long bannerId) {
        Banner banner = bannerRepository.findById(bannerId)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));

        return AdminBannerDetailResponseDto.from(banner);
    }

    @Transactional(readOnly = true)
    public PagedResponseDto<BannerPlaceDto> getMappedPlaceList(Pageable pageable, Long bannerId) {
        Page<BannerPlaceDto> placePage = adminCustomBannerRepository.getAllMappedPlaceByBannerId(pageable,
            bannerId).map(BannerPlaceDto::from);

        return AdminBannerPlaceListResponseDto.of(placePage, pageable);
    }

    @Transactional
    public void updateBanner(Long bannerId, UpdateBannerRequestDto requestDto) {
        Banner banner = bannerRepository.findById(bannerId)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));

        banner.updateDetail(requestDto.getTitle(), requestDto.getIsActive());

        bannerRepository.save(banner);
    }

    @Transactional
    public void updateBannerOuterImage(Long bannerId, MultipartFile image) {
        Banner banner = bannerRepository.findById(bannerId)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));
        String oldS3Key = banner.getOuterImageS3Key();

        try {
            String newS3Key = BANNER_S3_PREFIX + FileUtils.generateUniqueFilename(image);
            s3Manager.putObject(s3Manager.bucket, newS3Key, image);

            banner.updateOuterS3ImageKey(newS3Key);
            bannerRepository.save(banner);

            s3Manager.deleteObject(s3Manager.bucket, oldS3Key);
        } catch (IOException | SdkException e) {
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void updateBannerInnerImage(Long bannerId, MultipartFile image) {
        Banner banner = bannerRepository.findById(bannerId)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));
        String oldS3Key = banner.getInnerImageS3Key();

        try {
            String newS3Key = BANNER_S3_PREFIX + FileUtils.generateUniqueFilename(image);
            s3Manager.putObject(s3Manager.bucket, newS3Key, image);

            banner.updateInnerS3ImageKey(newS3Key);
            bannerRepository.save(banner);

            s3Manager.deleteObject(s3Manager.bucket, oldS3Key);
        } catch (IOException | SdkException e) {
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void deleteBanner(Long bannerId) {
        Banner banner = bannerRepository.findById(bannerId)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));
        String outerImageS3Key = banner.getOuterImageS3Key();
        String innerImageS3Key = banner.getInnerImageS3Key();

        adminCustomBannerRepository.deleteBannerPlaceByBannerId(bannerId);
        bannerRepository.delete(banner);

        try {
            List<ObjectIdentifier> objectIdentifierList = new ArrayList<>();
            if (outerImageS3Key != null) {
                objectIdentifierList.add(
                    ObjectIdentifier.builder().key(outerImageS3Key).build());
            }
            if (innerImageS3Key != null) {
                objectIdentifierList.add(
                    ObjectIdentifier.builder().key(innerImageS3Key).build());
            }

            s3Manager.deleteObjects(s3Manager.bucket, objectIdentifierList);
        } catch (SdkException e) {
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public PagedResponseDto<BannerPlaceDto> getCandidatePlaceList(Pageable pageable,
        GetCandidatePlaceListRequestDto requestDto) {
        Page<BannerPlaceDto> placePage = adminCustomBannerRepository.getAllUnmappedPlaceByBannerIdAndFilters(
            pageable, requestDto).map(BannerPlaceDto::from);

        return AdminBannerPlaceListResponseDto.of(placePage, pageable);
    }
}
