package com.jigumulmi.place.manager

import com.jigumulmi.config.exception.CustomException
import com.jigumulmi.config.exception.errorCode.AdminErrorCode
import com.jigumulmi.config.exception.errorCode.CommonErrorCode
import com.jigumulmi.place.dto.BusinessHour
import com.jigumulmi.place.dto.ImageDto
import com.jigumulmi.place.dto.MenuDto
import com.jigumulmi.place.dto.PositionDto
import com.jigumulmi.place.dto.request.AdminCreatePlaceRequestDto
import com.jigumulmi.place.dto.response.DistrictResponseDto
import com.jigumulmi.place.dto.response.PlaceCategoryDto
import com.jigumulmi.place.repository.PlaceRepository
import com.jigumulmi.place.vo.District
import com.jigumulmi.place.vo.Region
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.DayOfWeek

@Component
class AdminPlaceValidator(
    private val adminPlaceManager: AdminPlaceManager,

    private val placeRepository: PlaceRepository
) {

    fun validatePlaceRemoval(placeId: Long) {
        val place = placeRepository.findById(placeId)
            .orElseThrow { CustomException(CommonErrorCode.RESOURCE_NOT_FOUND) }

        if (place.isApproved) {
            throw CustomException(AdminErrorCode.INVALID_PLACE_REMOVAL)
        }
    }

    @Transactional(readOnly = true)
    fun validatePlaceApprovalIfNeeded(placeId: Long, approve: Boolean) {
        if (!approve) {
            return
        }

        val placeBasic = adminPlaceManager.getPlaceBasic(placeId)
        checkPlaceEssential(
            placeBasic.name, placeBasic.address,
            placeBasic.region, DistrictResponseDto.toDistrict(placeBasic.district)
        )

        checkPlacePosition(placeBasic.position)

        val subwayStationList = placeBasic.subwayStationList
        if (subwayStationList.isEmpty()) {
            throw CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "지하철 누락")
        }

        val categoryList = placeBasic.categoryList
        checkCategory(categoryList)

        val placeImage = adminPlaceManager.getPlaceImage(placeId)
        if (placeImage.isEmpty()) {
            throw CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "이미지 누락")
        }

        val menuDtoList = adminPlaceManager.getMenu(placeId)
        checkMenu(menuDtoList)

        val fixedBusinessHour = adminPlaceManager.getFixedBusinessHour(placeId)
        for (dayOfWeek in DayOfWeek.entries) {
            if (fixedBusinessHour.getBusinessHour(dayOfWeek) == BusinessHour()) {
                throw CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "영업 시간 누락")
            }
        }
    }

    private fun checkMenu(menuList: List<MenuDto>) {
        if (menuList.isEmpty() || menuList.stream()
                .allMatch { menu: MenuDto -> menu.name.isBlank() }
        ) {
            throw CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "메뉴 누락")
        }
    }

    private fun checkCategory(categoryList: List<PlaceCategoryDto>) {
        if (categoryList.isEmpty() || categoryList.stream()
                .allMatch { dto: PlaceCategoryDto -> dto.categoryGroup == null }
        ) {
            throw CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "카테고리 누락")
        }
    }

    private fun checkPlacePosition(position: PositionDto) {
        if (position.latitude == null || (position.latitude < 33
                    || position.latitude > 39) || position.longitude == null || (position.longitude < 124
                    || position.longitude > 132)
        ) {
            throw CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "좌표 오류")
        }
    }

    private fun checkPlaceEssential(
        name: String?, address: String?, region: Region?,
        district: District?
    ) {
        if (name.isNullOrBlank() || address.isNullOrBlank() || region == null || district == null) {
            throw CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "기본 정보 누락")
        }
    }

    fun validatePlaceBasicUpdate(placeId: Long, requestDto: AdminCreatePlaceRequestDto) {
        val place = placeRepository.findById(placeId)
            .orElseThrow { CustomException(CommonErrorCode.RESOURCE_NOT_FOUND) }

        if (!place.isApproved) {
            return
        }

        checkPlaceEssential(
            requestDto.name, requestDto.address,
            requestDto.region, requestDto.district
        )

        if (requestDto.subwayStationIdList.isEmpty()) {
            throw CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "지하철 누락")
        }

        checkPlacePosition(requestDto.position)

        checkCategory(requestDto.categoryList)
    }

    fun validatePlaceImageUpdate(placeId: Long, imageDtoList: List<ImageDto>?) {
        val place = placeRepository.findById(placeId)
            .orElseThrow { CustomException(CommonErrorCode.RESOURCE_NOT_FOUND) }

        if (!place.isApproved) {
            return
        }

        if (imageDtoList.isNullOrEmpty()) {
            throw CustomException(AdminErrorCode.INVALID_PLACE_APPROVAL, "이미지 누락")
        }
    }

    fun validateMenuUpdate(placeId: Long, menuDtoList: List<MenuDto>) {
        val place = placeRepository.findById(placeId)
            .orElseThrow { CustomException(CommonErrorCode.RESOURCE_NOT_FOUND) }

        if (!place.isApproved) {
            return
        }

        checkMenu(menuDtoList)
    }
}
