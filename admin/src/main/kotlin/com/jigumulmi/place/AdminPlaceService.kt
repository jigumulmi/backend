package com.jigumulmi.place

import com.jigumulmi.common.AdminPagedResponseDto
import com.jigumulmi.member.domain.Member
import com.jigumulmi.place.dto.ImageDto
import com.jigumulmi.place.dto.MenuDto
import com.jigumulmi.place.dto.WeeklyBusinessHourDto
import com.jigumulmi.place.dto.request.*
import com.jigumulmi.place.dto.response.*
import com.jigumulmi.place.dto.response.AdminPlaceListResponseDto.PlaceDto
import com.jigumulmi.place.manager.AdminPlaceManager
import com.jigumulmi.place.manager.AdminPlaceValidator
import com.jigumulmi.place.vo.Region
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class AdminPlaceService (
    private val adminPlaceManager: AdminPlaceManager,
    private val adminPlaceValidator: AdminPlaceValidator,
){

    fun getPlaceList(
        pageable: Pageable,
        requestDto: AdminGetPlaceListRequestDto
    ): AdminPagedResponseDto<PlaceDto> {
        return adminPlaceManager.getPlaceList(pageable, requestDto)
    }

    fun getPlaceBasic(placeId: Long): AdminPlaceBasicResponseDto {
        return adminPlaceManager.getPlaceBasic(placeId)
    }

    @Transactional
    fun updatePlaceBasic(placeId: Long, requestDto: AdminCreatePlaceRequestDto) {
        adminPlaceValidator.validatePlaceBasicUpdate(placeId, requestDto)
        adminPlaceManager.updatePlaceBasic(placeId, requestDto)
    }

    fun getPlaceImage(placeId: Long): List<ImageDto> {
        return adminPlaceManager.getPlaceImage(placeId)
    }

    @Transactional
    fun updatePlaceImage(placeId: Long, imageDtoList: List<ImageDto>) {
        adminPlaceValidator.validatePlaceImageUpdate(placeId, imageDtoList)
        adminPlaceManager.updatePlaceImage(placeId, imageDtoList)
    }

    fun getMenu(placeId: Long): List<MenuDto> {
        return adminPlaceManager.getMenu(placeId)
    }

    @Transactional
    fun updateMenu(placeId: Long, menuDtoList: List<MenuDto>) {
        adminPlaceValidator.validateMenuUpdate(placeId, menuDtoList)
        adminPlaceManager.updateMenu(placeId, menuDtoList)
    }

    fun updateFixedBusinessHour(placeId: Long, requestDto: WeeklyBusinessHourDto) {
        adminPlaceManager.updateFixedBusinessHour(placeId, requestDto)
    }

    fun createTemporaryBusinessHour(
        placeId: Long,
        requestDto: AdminCreateTemporaryBusinessHourRequestDto
    ) {
        adminPlaceManager.createTemporaryBusinessHour(placeId, requestDto)
    }

    fun updateTemporaryBusinessHour(
        hourId: Long,
        requestDto: AdminCreateTemporaryBusinessHourRequestDto
    ) {
        adminPlaceManager.updateTemporaryBusinessHour(hourId, requestDto)
    }

    fun deleteTemporaryBusinessHour(hourId: Long) {
        adminPlaceManager.deleteTemporaryBusinessHour(hourId)
    }

    fun getPlaceBusinessHour(placeId: Long, month: Int): AdminPlaceBusinessHourResponseDto {
        val fixedBusinessHourResponseDto = adminPlaceManager.getFixedBusinessHour(
            placeId
        )
        val tempBusinessHourResponseDto = adminPlaceManager.getTemporaryBusinessHour(
            placeId, month
        )

        return AdminPlaceBusinessHourResponseDto.from(
            fixedBusinessHourResponseDto,
            tempBusinessHourResponseDto
        )
    }

    fun createPlace(
        requestDto: AdminCreatePlaceRequestDto,
        member: Member
    ): AdminCreatePlaceResponseDto {
        return adminPlaceManager.createPlace(requestDto, member)
    }

    @Transactional
    fun togglePlaceApprove(placeId: Long, approve: Boolean) {
        adminPlaceValidator.validatePlaceApprovalIfNeeded(placeId, approve)
        adminPlaceManager.togglePlaceApprove(placeId, approve)
    }

    fun deletePlace(placeId: Long) {
        adminPlaceValidator.validatePlaceRemoval(placeId)
        adminPlaceManager.deletePlace(placeId)
        adminPlaceManager.deleteMenuImageFileList(placeId)
    }

    fun getRegionList(): List<Region> = Region.entries.toList()

    fun getDistrictList(region: Region): List<DistrictResponseDto> {
        return adminPlaceManager.getDistrictListOrderByName(region)
    }

    fun createMenuImageS3PutPresignedUrl(
        requestDto: MenuImageS3PutPresignedUrlRequestDto
    ): AdminS3PutPresignedUrlResponseDto {
        return adminPlaceManager.createMenuImageS3PutPresignedUrl(requestDto.placeId)
    }

    fun createMenuImageS3DeletePresignedUrl(
        requestDto: MenuImageS3DeletePresignedUrlRequestDto
    ): AdminS3DeletePresignedUrlResponseDto {
        return adminPlaceManager.createMenuImageS3DeletePresignedUrl(requestDto.s3Key)
    }
}
