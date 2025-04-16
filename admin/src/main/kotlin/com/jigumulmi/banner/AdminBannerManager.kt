package com.jigumulmi.banner

import com.jigumulmi.aws.S3Manager
import com.jigumulmi.banner.domain.Banner
import com.jigumulmi.banner.dto.AdminCreateBannerImageS3KeyDto
import com.jigumulmi.banner.dto.AdminUpdateBannerImageS3KeyDto
import com.jigumulmi.banner.dto.request.CreateBannerRequestDto
import com.jigumulmi.banner.dto.request.GetCandidatePlaceListRequestDto
import com.jigumulmi.banner.dto.request.UpdateBannerRequestDto
import com.jigumulmi.banner.dto.response.AdminBannerDetailResponseDto
import com.jigumulmi.banner.dto.response.AdminBannerPlaceListResponseDto
import com.jigumulmi.banner.dto.response.AdminBannerResponseDto
import com.jigumulmi.banner.dto.response.CreateBannerResponseDto
import com.jigumulmi.banner.repository.BannerRepository
import com.jigumulmi.common.AdminPagedResponseDto
import com.jigumulmi.common.FileUtils
import com.jigumulmi.config.exception.CustomException
import com.jigumulmi.config.exception.errorCode.CommonErrorCode
import jakarta.validation.constraints.NotEmpty
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.exception.SdkException
import software.amazon.awssdk.services.s3.model.ObjectIdentifier
import java.io.IOException
import java.util.*
import java.util.stream.Collectors

@Component
class AdminBannerManager (
    private val s3Manager: S3Manager,

    private val bannerRepository: BannerRepository,
    private val adminCustomBannerRepository: AdminCustomBannerRepository
){

    fun saveBannerImageFile(image: MultipartFile?, s3Key: String?) {
        if (image == null) {
            return
        }

        try {
            s3Manager.putObject(s3Manager.bucket, s3Key, image)
        } catch (e: IOException) {
            throw CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR)
        } catch (e: SdkException) {
            throw CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR)
        }
    }

    fun deleteBannerImageFile(s3Key: String?) {
        if (s3Key == null) {
            return
        }

        try {
            s3Manager.deleteObject(s3Manager.bucket, s3Key)
        } catch (e: SdkException) {
            throw CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR)
        }
    }

    fun deleteBannerImageFileList(s3KeyList: List<String?>) {
        if (s3KeyList.isEmpty()) {
            return
        }

        try {
            val objectIdentifierList = s3KeyList.stream()
                .filter { obj: String? -> Objects.nonNull(obj) }
                .map { key: String? -> ObjectIdentifier.builder().key(key).build() }
                .collect(Collectors.toList())

            s3Manager.deleteObjects(s3Manager.bucket, objectIdentifierList)
        } catch (e: SdkException) {
            throw CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR)
        }
    }

    fun saveBanner(requestDto: CreateBannerRequestDto): CreateBannerResponseDto {
        val outerImageS3Key =
            if (requestDto.outerImage != null) makeBannerImageS3Key() else null
        val innerImageS3Key =
            if (requestDto.innerImage != null) makeBannerImageS3Key() else null

        val banner = Banner.builder()
            .title(requestDto.title)
            .outerImageS3Key(outerImageS3Key)
            .innerImageS3Key(innerImageS3Key)
            .isActive(requestDto.isActive)
            .build()

        bannerRepository.save(banner)

        return CreateBannerResponseDto(
            bannerId = banner.id,
            s3KeyDto = AdminCreateBannerImageS3KeyDto(
                outerImage = outerImageS3Key,
                innerImage = innerImageS3Key
            )
        )
    }

    fun getBannerList(): List<AdminBannerResponseDto> = bannerRepository.findAll().stream()
        .map { banner: Banner -> AdminBannerResponseDto.from(banner) }.toList()

    fun addBannerPlace(bannerId: Long, placeIdList: List<Long>) {
        adminCustomBannerRepository.batchInsertBannerPlace(bannerId, placeIdList)
    }

    fun removeBannerPlace(bannerId: Long, placeIdList: @NotEmpty List<Long>) {
        adminCustomBannerRepository.deleteBannerPlaceByBannerIdAndPlaceIdList(
            bannerId,
            placeIdList
        )
    }

    fun getBannerDetail(bannerId: Long): AdminBannerDetailResponseDto {
        val banner = getBannerEntity(bannerId)

        return AdminBannerDetailResponseDto.from(banner)
    }

    @Transactional(readOnly = true)
    fun getMappedPlaceList(
        pageable: Pageable,
        bannerId: Long
    ): AdminPagedResponseDto<AdminBannerPlaceListResponseDto.BannerPlaceDto> {
        val placePage = adminCustomBannerRepository.getAllMappedPlaceByBannerId(
            pageable,
            bannerId
        ).map(AdminBannerPlaceListResponseDto.BannerPlaceDto::from)

        return AdminPagedResponseDto.of(placePage, pageable)
    }

    private fun getBannerEntity(bannerId: Long): Banner {
        return bannerRepository.findById(bannerId)
            .orElseThrow { CustomException(CommonErrorCode.RESOURCE_NOT_FOUND) }
    }

    @Transactional
    fun updateBannerBasic(bannerId: Long, requestDto: UpdateBannerRequestDto) {
        val banner = getBannerEntity(bannerId)

        banner.updateBasic(requestDto.title, requestDto.isActive)
    }

    @Transactional
    fun updateBannerOuterImage(bannerId: Long): AdminUpdateBannerImageS3KeyDto {
        val banner = getBannerEntity(bannerId)
        val oldS3Key = banner.outerImageS3Key

        val newS3Key = makeBannerImageS3Key()
        banner.updateOuterS3ImageKey(newS3Key)

        return AdminUpdateBannerImageS3KeyDto(
            newKey = newS3Key,
            oldKey = oldS3Key
        )
    }

    @Transactional
    fun updateBannerInnerImage(bannerId: Long): AdminUpdateBannerImageS3KeyDto {
        val banner = getBannerEntity(bannerId)
        val oldS3Key = banner.innerImageS3Key

        val newS3Key = makeBannerImageS3Key()
        banner.updateInnerS3ImageKey(newS3Key)

        return AdminUpdateBannerImageS3KeyDto(
            newKey = newS3Key,
            oldKey = oldS3Key
        )
    }

    @Transactional
    fun deleteBanner(bannerId: Long): List<String?> {
        val banner = getBannerEntity(bannerId)

        val outerImageS3Key = banner.outerImageS3Key
        val innerImageS3Key = banner.innerImageS3Key

        adminCustomBannerRepository.deleteBannerPlaceByBannerId(bannerId)
        bannerRepository.delete(banner)

        return listOf(outerImageS3Key, innerImageS3Key)
    }

    @Transactional(readOnly = true)
    fun getCandidatePlaceList(
        pageable: Pageable,
        requestDto: GetCandidatePlaceListRequestDto
    ): AdminPagedResponseDto<AdminBannerPlaceListResponseDto.BannerPlaceDto> {
        val placePage = adminCustomBannerRepository.getAllUnmappedPlaceByBannerIdAndFilters(
            pageable, requestDto
        ).map(AdminBannerPlaceListResponseDto.BannerPlaceDto::from)

        return AdminPagedResponseDto.of(placePage, pageable)
    }

    companion object {
        private fun makeBannerImageS3Key(): String {
            return S3Manager.BANNER_IMAGE_S3_PREFIX + FileUtils.generateUniqueFilename()
        }
    }
}
