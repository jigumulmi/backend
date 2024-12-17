package com.jigumulmi.admin.banner;

import com.jigumulmi.admin.banner.dto.request.BannerPlaceMappingRequestDto;
import com.jigumulmi.admin.banner.dto.request.CreateBannerRequestDto;
import com.jigumulmi.admin.banner.dto.request.DeleteBannerRequestDto;
import com.jigumulmi.admin.banner.dto.request.UpdateBannerRequestDto;
import com.jigumulmi.admin.banner.dto.response.AdminBannerDetailResponseDto;
import com.jigumulmi.admin.banner.dto.response.AdminBannerPlaceListResponseDto;
import com.jigumulmi.admin.banner.dto.response.AdminBannerPlaceListResponseDto.BannerPlaceDto;
import com.jigumulmi.admin.banner.dto.response.AdminBannerResponseDto;
import com.jigumulmi.aws.S3Service;
import com.jigumulmi.banner.domain.Banner;
import com.jigumulmi.banner.repository.BannerRepository;
import com.jigumulmi.config.common.FileUtils;
import com.jigumulmi.config.common.PageDto;
import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import com.jigumulmi.place.domain.Place;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
    private final S3Service s3Service;

    private final BannerRepository bannerRepository;
    private final AdminCustomBannerRepository adminCustomBannerRepository;

    public void createBanner(CreateBannerRequestDto requestDto) {
        String outerImageS3Key = null;
        String innerImageS3Key = null;
        try {
            MultipartFile outerImage = requestDto.getOuterImage();
            if (outerImage != null) {
                outerImageS3Key = BANNER_S3_PREFIX + FileUtils.generateUniqueFilename(outerImage);
                s3Service.putObject(s3Service.bucket, outerImageS3Key, outerImage);
            }

            MultipartFile innerImage = requestDto.getInnerImage();
            if (innerImage != null) {
                innerImageS3Key = BANNER_S3_PREFIX + FileUtils.generateUniqueFilename(innerImage);
                s3Service.putObject(s3Service.bucket, innerImageS3Key, innerImage);
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

        bannerRepository.save(banner);
    }

    public List<AdminBannerResponseDto> getBannerList() {
        return bannerRepository.findAll().stream().map(AdminBannerResponseDto::from).toList();
    }

    public void addBannerPlace(Long bannerId, BannerPlaceMappingRequestDto requestDto) {
        adminCustomBannerRepository.batchInsertBannerPlace(bannerId, requestDto);
    }

    public void removeBannerPlace(Long bannerId, BannerPlaceMappingRequestDto requestDto) {
        adminCustomBannerRepository.deleteBannerPlace(bannerId, requestDto);
    }

    public AdminBannerDetailResponseDto getBannerDetail(Long bannerId) {
        Banner banner = bannerRepository.findById(bannerId)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));

        return AdminBannerDetailResponseDto.from(banner);
    }

    @Transactional(readOnly = true)
    public AdminBannerPlaceListResponseDto getMappedPlaceList(Pageable pageable, Long bannerId) {
        Page<Place> placePage = adminCustomBannerRepository.getPlaceList(pageable,
            bannerId);

        List<BannerPlaceDto> placeDtoList = placePage.getContent().stream()
            .map(BannerPlaceDto::from).collect(Collectors.toList());

        return AdminBannerPlaceListResponseDto.builder()
            .data(placeDtoList)
            .page(PageDto.builder()
                .totalCount(placePage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .totalPage(placePage.getTotalPages())
                .build()
            )
            .build();
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
            s3Service.putObject(s3Service.bucket, newS3Key, image);

            banner.updateOuterS3ImageKey(newS3Key);
            bannerRepository.save(banner);

            s3Service.deleteObject(s3Service.bucket, oldS3Key);
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
            s3Service.putObject(s3Service.bucket, newS3Key, image);

            banner.updateInnerS3ImageKey(newS3Key);
            bannerRepository.save(banner);

            s3Service.deleteObject(s3Service.bucket, oldS3Key);
        } catch (IOException | SdkException e) {
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void deleteBannerList(DeleteBannerRequestDto requestDto) {
        List<Banner> bannerList = bannerRepository.findAllById(requestDto.getBannerIdList());

        adminCustomBannerRepository.deleteBannerPlace(requestDto);
        bannerRepository.deleteAllInBatch(bannerList);

        try {
            List<ObjectIdentifier> objectIdentifierList = new ArrayList<>();
            for (Banner banner : bannerList) {
                String outerImageS3Key = banner.getOuterImageS3Key();
                String innerImageS3Key = banner.getInnerImageS3Key();

                if (outerImageS3Key != null) {
                    objectIdentifierList.add(
                        ObjectIdentifier.builder().key(outerImageS3Key).build());
                }
                if (innerImageS3Key != null) {
                    objectIdentifierList.add(
                        ObjectIdentifier.builder().key(innerImageS3Key).build());
                }
            }

            s3Service.deleteObjects(s3Service.bucket, objectIdentifierList);
        } catch (SdkException e) {
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
