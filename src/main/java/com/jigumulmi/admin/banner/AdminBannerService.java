package com.jigumulmi.admin.banner;

import com.jigumulmi.admin.banner.dto.request.BannerPlaceMappingRequestDto;
import com.jigumulmi.admin.banner.dto.request.CreateBannerRequestDto;
import com.jigumulmi.admin.banner.dto.request.GetCandidatePlaceListRequestDto;
import com.jigumulmi.admin.banner.dto.request.UpdateBannerRequestDto;
import com.jigumulmi.admin.banner.dto.response.AdminBannerDetailResponseDto;
import com.jigumulmi.admin.banner.dto.response.AdminBannerPlaceListResponseDto.BannerPlaceDto;
import com.jigumulmi.admin.banner.dto.response.AdminBannerResponseDto;
import com.jigumulmi.admin.banner.dto.response.CreateBannerResponseDto;
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
        String outerImageS3Key = adminBannerManager.saveBannerImageFile(requestDto.getOuterImage());
        String innerImageS3Key = adminBannerManager.saveBannerImageFile(requestDto.getOuterImage());

        return adminBannerManager.saveBanner(requestDto, outerImageS3Key, innerImageS3Key);
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
        String newS3Key = adminBannerManager.saveBannerImageFile(image);
        String oldS3Key = adminBannerManager.updateBannerOuterImage(bannerId, newS3Key);
        adminBannerManager.deleteBannerImageFile(oldS3Key);
    }

    public void updateBannerInnerImage(Long bannerId, MultipartFile image) {
        String newS3Key = adminBannerManager.saveBannerImageFile(image);
        String oldS3Key = adminBannerManager.updateBannerInnerImage(bannerId, newS3Key);
        adminBannerManager.deleteBannerImageFile(oldS3Key);
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
