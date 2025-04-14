package com.jigumulmi.banner

import com.jigumulmi.banner.dto.request.BannerPlaceMappingRequestDto
import com.jigumulmi.banner.dto.request.CreateBannerRequestDto
import com.jigumulmi.banner.dto.request.GetCandidatePlaceListRequestDto
import com.jigumulmi.banner.dto.request.UpdateBannerRequestDto
import com.jigumulmi.banner.dto.response.AdminBannerDetailResponseDto
import com.jigumulmi.banner.dto.response.AdminBannerPlaceListResponseDto
import com.jigumulmi.banner.dto.response.AdminBannerResponseDto
import com.jigumulmi.banner.dto.response.CreateBannerResponseDto
import com.jigumulmi.common.PagedResponseDto
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class AdminBannerService(
    private val adminBannerManager: AdminBannerManager
) {

    fun createBanner(requestDto: CreateBannerRequestDto): CreateBannerResponseDto {
        val createBannerResponseDto = adminBannerManager.saveBanner(requestDto)

        adminBannerManager.saveBannerImageFile(
            requestDto.outerImage,
            createBannerResponseDto.s3KeyDto.outerImage
        )
        adminBannerManager.saveBannerImageFile(
            requestDto.innerImage,
            createBannerResponseDto.s3KeyDto.innerImage
        )

        return createBannerResponseDto
    }

    fun getBannerList(): List<AdminBannerResponseDto> = adminBannerManager.getBannerList()

    fun addBannerPlace(bannerId: Long, requestDto: BannerPlaceMappingRequestDto) {
        adminBannerManager.addBannerPlace(bannerId, requestDto.placeIdList)
    }

    fun removeBannerPlace(bannerId: Long, requestDto: BannerPlaceMappingRequestDto) {
        adminBannerManager.removeBannerPlace(bannerId, requestDto.placeIdList)
    }

    fun getBannerDetail(bannerId: Long): AdminBannerDetailResponseDto {
        return adminBannerManager.getBannerDetail(bannerId)
    }

    fun getMappedPlaceList(
        pageable: Pageable,
        bannerId: Long
    ): PagedResponseDto<AdminBannerPlaceListResponseDto.BannerPlaceDto> {
        return adminBannerManager.getMappedPlaceList(pageable, bannerId)
    }

    fun updateBannerBasic(bannerId: Long, requestDto: UpdateBannerRequestDto) {
        adminBannerManager.updateBannerBasic(bannerId, requestDto)
    }

    fun updateBannerOuterImage(bannerId: Long, image: MultipartFile) {
        val s3KeyDto = adminBannerManager.updateBannerOuterImage(bannerId)

        adminBannerManager.saveBannerImageFile(image, s3KeyDto.newKey)
        adminBannerManager.deleteBannerImageFile(s3KeyDto.oldKey)
    }

    fun updateBannerInnerImage(bannerId: Long, image: MultipartFile) {
        val s3KeyDto = adminBannerManager.updateBannerInnerImage(bannerId)

        adminBannerManager.saveBannerImageFile(image, s3KeyDto.newKey)
        adminBannerManager.deleteBannerImageFile(s3KeyDto.oldKey)
    }

    fun deleteBanner(bannerId: Long) {
        val s3KeyList = adminBannerManager.deleteBanner(bannerId)

        adminBannerManager.deleteBannerImageFileList(s3KeyList)
    }

    fun getCandidatePlaceList(
        pageable: Pageable,
        requestDto: GetCandidatePlaceListRequestDto
    ): PagedResponseDto<AdminBannerPlaceListResponseDto.BannerPlaceDto> {
        return adminBannerManager.getCandidatePlaceList(pageable, requestDto)
    }
}
