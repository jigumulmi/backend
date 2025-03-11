package com.jigumulmi.banner;

import com.jigumulmi.banner.dto.AdminUpdateBannerImageS3KeyDto;
import com.jigumulmi.banner.dto.request.BannerPlaceMappingRequestDto;
import com.jigumulmi.banner.dto.request.CreateBannerRequestDto;
import com.jigumulmi.banner.dto.request.GetCandidatePlaceListRequestDto;
import com.jigumulmi.banner.dto.request.UpdateBannerRequestDto;
import com.jigumulmi.banner.dto.response.AdminBannerDetailResponseDto;
import com.jigumulmi.banner.dto.response.AdminBannerPlaceListResponseDto.BannerPlaceDto;
import com.jigumulmi.banner.dto.response.AdminBannerResponseDto;
import com.jigumulmi.banner.dto.response.CreateBannerResponseDto;
import com.jigumulmi.common.PagedResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AdminBannerService {

    private final AdminBannerManager adminBannerManager;

    public CreateBannerResponseDto createBanner(CreateBannerRequestDto requestDto) {
        CreateBannerResponseDto createBannerResponseDto = adminBannerManager.saveBanner(requestDto);

        adminBannerManager.saveBannerImageFile(requestDto.getOuterImage(),
            createBannerResponseDto.getS3KeyDto().getOuterImage());
        adminBannerManager.saveBannerImageFile(requestDto.getInnerImage(),
            createBannerResponseDto.getS3KeyDto().getInnerImage());

        return createBannerResponseDto;
    }

    public List<AdminBannerResponseDto> getBannerList() {
        return adminBannerManager.getBannerList();
    }

    public void addBannerPlace(Long bannerId, BannerPlaceMappingRequestDto requestDto) {
        adminBannerManager.addBannerPlace(bannerId, requestDto.getPlaceIdList());
    }

    public void removeBannerPlace(Long bannerId, BannerPlaceMappingRequestDto requestDto) {
        adminBannerManager.removeBannerPlace(bannerId, requestDto.getPlaceIdList());
    }

    public AdminBannerDetailResponseDto getBannerDetail(Long bannerId) {
        return adminBannerManager.getBannerDetail(bannerId);
    }

    public PagedResponseDto<BannerPlaceDto> getMappedPlaceList(Pageable pageable, Long bannerId) {
        return adminBannerManager.getMappedPlaceList(pageable, bannerId);
    }

    public void updateBannerBasic(Long bannerId, UpdateBannerRequestDto requestDto) {
        adminBannerManager.updateBannerBasic(bannerId, requestDto);
    }

    public void updateBannerOuterImage(Long bannerId, MultipartFile image) {
        AdminUpdateBannerImageS3KeyDto s3KeyDto = adminBannerManager.updateBannerOuterImage(bannerId);

        adminBannerManager.saveBannerImageFile(image, s3KeyDto.getNewKey());
        adminBannerManager.deleteBannerImageFile(s3KeyDto.getOldKey());
    }

    public void updateBannerInnerImage(Long bannerId, MultipartFile image) {
        AdminUpdateBannerImageS3KeyDto s3KeyDto = adminBannerManager.updateBannerInnerImage(bannerId);

        adminBannerManager.saveBannerImageFile(image, s3KeyDto.getNewKey());
        adminBannerManager.deleteBannerImageFile(s3KeyDto.getOldKey());
    }

    public void deleteBanner(Long bannerId) {
        List<String> s3KeyList = adminBannerManager.deleteBanner(bannerId);

        adminBannerManager.deleteBannerImageFileList(s3KeyList);
    }

    public PagedResponseDto<BannerPlaceDto> getCandidatePlaceList(Pageable pageable,
        GetCandidatePlaceListRequestDto requestDto) {
        return adminBannerManager.getCandidatePlaceList(pageable, requestDto);
    }
}
