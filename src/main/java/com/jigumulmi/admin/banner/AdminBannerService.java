package com.jigumulmi.admin.banner;

import com.jigumulmi.admin.banner.dto.request.BannerPlaceMappingRequestDto;
import com.jigumulmi.admin.banner.dto.request.CreateBannerRequestDto;
import com.jigumulmi.admin.banner.dto.response.AdminBannerResponseDto;
import com.jigumulmi.aws.S3Service;
import com.jigumulmi.banner.domain.Banner;
import com.jigumulmi.banner.repository.BannerRepository;
import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

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
                String fileExtension = StringUtils.getFilenameExtension(
                    outerImage.getOriginalFilename());
                outerImageS3Key = BANNER_S3_PREFIX + UUID.randomUUID() + "." + fileExtension;
                s3Service.putObject(s3Service.bucket, outerImageS3Key, outerImage);
            }

            MultipartFile innerImage = requestDto.getInnerImage();
            if (innerImage != null) {
                String fileExtension = StringUtils.getFilenameExtension(
                    innerImage.getOriginalFilename());
                innerImageS3Key = BANNER_S3_PREFIX + UUID.randomUUID() + "." + fileExtension;
                s3Service.putObject(s3Service.bucket, innerImageS3Key, innerImage);
            }
        } catch (IOException e) {
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
}
