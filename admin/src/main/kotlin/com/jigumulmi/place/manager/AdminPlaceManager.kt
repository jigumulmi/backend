package com.jigumulmi.place.manager

import com.jigumulmi.aws.S3Manager
import com.jigumulmi.common.AdminPagedResponseDto
import com.jigumulmi.common.FileUtils
import com.jigumulmi.common.WeekUtils
import com.jigumulmi.config.exception.CustomException
import com.jigumulmi.config.exception.errorCode.CommonErrorCode
import com.jigumulmi.member.domain.Member
import com.jigumulmi.place.domain.*
import com.jigumulmi.place.dto.BusinessHour
import com.jigumulmi.place.dto.ImageDto
import com.jigumulmi.place.dto.MenuDto
import com.jigumulmi.place.dto.WeeklyBusinessHourDto
import com.jigumulmi.place.dto.request.AdminCreatePlaceRequestDto
import com.jigumulmi.place.dto.request.AdminCreateTemporaryBusinessHourRequestDto
import com.jigumulmi.place.dto.request.AdminGetPlaceListRequestDto
import com.jigumulmi.place.dto.response.*
import com.jigumulmi.place.dto.response.AdminPlaceBusinessHourResponseDto.TemporaryBusinessHourDto
import com.jigumulmi.place.dto.response.AdminPlaceListResponseDto.PlaceDto
import com.jigumulmi.place.repository.*
import com.jigumulmi.place.vo.District
import com.jigumulmi.place.vo.Region
import jakarta.validation.constraints.NotBlank
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.core.exception.SdkException
import software.amazon.awssdk.services.s3.model.ObjectIdentifier
import java.time.DayOfWeek
import java.util.stream.Collectors
import java.util.stream.IntStream


@Component
class AdminPlaceManager(
    private val s3Manager: S3Manager,

    private val adminCustomPlaceRepository: AdminCustomPlaceRepository,
    private val placeRepository: PlaceRepository,
    private val subwayStationRepository: SubwayStationRepository,
    private val menuRepository: MenuRepository,
    private val placeImageRepository: PlaceImageRepository,
    private val fixedBusinessHourRepository: FixedBusinessHourRepository,
    private val temporaryBusinessHourRepository: TemporaryBusinessHourRepository,
    private val adminSubwayStationPlaceRepository: AdminSubwayStationPlaceRepository,
    private val placeCategoryMappingRepository: PlaceCategoryMappingRepository,

    ) {
    @Transactional(readOnly = true)
    fun getPlaceList(
        pageable: Pageable,
        requestDto: AdminGetPlaceListRequestDto
    ): AdminPagedResponseDto<PlaceDto> {
        val placePage = adminCustomPlaceRepository.getPlaceList(
            pageable,
            requestDto
        ).map(PlaceDto::from)

        return AdminPagedResponseDto.of(placePage, pageable)
    }

    @Transactional(readOnly = true)
    fun getPlaceBasic(placeId: Long): AdminPlaceBasicResponseDto {
        return placeRepository.findById(placeId)
            .map(AdminPlaceBasicResponseDto.Companion::from)
            .orElseThrow { CustomException(CommonErrorCode.RESOURCE_NOT_FOUND) }
    }

    @Transactional
    fun updatePlaceBasic(placeId: Long, requestDto: AdminCreatePlaceRequestDto) {
        val place = placeRepository.findById(placeId)
            .orElseThrow { CustomException(CommonErrorCode.RESOURCE_NOT_FOUND) }

        place.adminBasicUpdate(
            requestDto.name, requestDto.region,
            requestDto.district, requestDto.address, requestDto.contact,
            requestDto.additionalInfo, requestDto.placeUrl,
            requestDto.registrantComment, requestDto.kakaoPlaceId,
            requestDto.position
        )

        adminSubwayStationPlaceRepository.deleteAllByPlace(place)
        placeCategoryMappingRepository.deleteAllInBatch(place.categoryMappingList)

        val subwayStationIdList = requestDto.subwayStationIdList
        val subwayStationList = subwayStationRepository.findAllById(subwayStationIdList)
            .stream()
            .sorted(
                Comparator.comparingInt { station: SubwayStation ->
                    subwayStationIdList.indexOf(
                        station.id
                    )
                })
            .toList()

        val subwayStationPlaceList = IntStream.range(0, subwayStationList.size)
            .mapToObj { i: Int ->
                SubwayStationPlace.builder()
                    .subwayStation(subwayStationList[i])
                    .place(place)
                    .isMain(i == 0)
                    .build()
            }
            .collect(Collectors.toList())

        val categoryMappingList = ArrayList<PlaceCategoryMapping>()
        for (categoryRequestDto in requestDto.categoryList) {
            categoryMappingList.add(
                PlaceCategoryMapping.builder()
                    .category(categoryRequestDto.category)
                    .categoryGroup(categoryRequestDto.categoryGroup)
                    .place(place)
                    .build()
            )
        }

        adminSubwayStationPlaceRepository.saveAll(subwayStationPlaceList)
        placeCategoryMappingRepository.saveAll(categoryMappingList)
    }

    fun getPlaceImage(placeId: Long): List<ImageDto> {
        return placeImageRepository.findByPlace_Id(placeId).stream()
            .map(ImageDto::from).toList()
    }

    @Transactional
    fun updatePlaceImage(placeId: Long, imageDtoList: List<ImageDto>) {
        val place = placeRepository.findById(placeId)
            .orElseThrow { CustomException(CommonErrorCode.RESOURCE_NOT_FOUND) }
        placeImageRepository.deleteAllByPlace(place)

        val placeImageList: MutableList<PlaceImage> = ArrayList()
        for (imageDto in imageDtoList) {
            placeImageList.add(
                PlaceImage.builder()
                    .url(imageDto.url)
                    .isMain(imageDto.isMain)
                    .place(place)
                    .build()
            )
        }

        placeImageRepository.saveAll(placeImageList)
    }

    fun getMenu(placeId: Long): List<MenuDto> {
        return menuRepository.findAllByPlaceId(placeId).stream()
            .map(MenuDto::from).toList()
    }

    @Transactional
    fun updateMenu(placeId: Long, menuDtoList: List<MenuDto>) {
        val place = placeRepository.findById(placeId)
            .orElseThrow { CustomException(CommonErrorCode.RESOURCE_NOT_FOUND) }
        menuRepository.deleteAllByPlace(place)

        val menuList: MutableList<Menu> = ArrayList()
        for (menuDto in menuDtoList) {
            menuList.add(MenuDto.toMenu(menuDto, place))
        }

        menuRepository.saveAll(menuList)
    }

    @Transactional
    fun updateFixedBusinessHour(placeId: Long, requestDto: WeeklyBusinessHourDto) {
        val place = placeRepository.findById(placeId)
            .orElseThrow { CustomException(CommonErrorCode.RESOURCE_NOT_FOUND) }
        fixedBusinessHourRepository.deleteAllByPlace(place)

        val businessHourList: MutableList<FixedBusinessHour> = ArrayList()
        for (dayOfWeek in DayOfWeek.entries) {
            val businessHour = requestDto.getBusinessHour(dayOfWeek)
            businessHourList.add(
                FixedBusinessHour.builder()
                    .place(place)
                    .dayOfWeek(dayOfWeek)
                    .openTime(businessHour.openTime)
                    .closeTime(businessHour.closeTime)
                    .breakStart(businessHour.breakStart)
                    .breakEnd(businessHour.breakEnd)
                    .isDayOff(businessHour.isDayOff)
                    .build()
            )
        }

        fixedBusinessHourRepository.saveAll(businessHourList)
    }

    fun createTemporaryBusinessHour(
        placeId: Long,
        requestDto: AdminCreateTemporaryBusinessHourRequestDto
    ) {
        val place = placeRepository.findById(placeId)
            .orElseThrow { CustomException(CommonErrorCode.RESOURCE_NOT_FOUND) }

        val businessHour = requestDto.businessHour

        val date = requestDto.date
        val weekOfYear = WeekUtils.getWeekOfYear(date)

        val temporaryBusinessHour = TemporaryBusinessHour.builder()
            .place(place)
            .month(date.monthValue)
            .weekOfYear(weekOfYear)
            .date(date)
            .dayOfWeek(date.dayOfWeek)
            .openTime(businessHour.openTime)
            .closeTime(businessHour.closeTime)
            .breakStart(businessHour.breakStart)
            .breakEnd(businessHour.breakEnd)
            .isDayOff(businessHour.isDayOff)
            .build()

        temporaryBusinessHourRepository.save(temporaryBusinessHour)
    }

    @Transactional
    fun updateTemporaryBusinessHour(
        hourId: Long,
        requestDto: AdminCreateTemporaryBusinessHourRequestDto
    ) {
        val temporaryBusinessHour = temporaryBusinessHourRepository.findById(
            hourId
        ).orElseThrow { CustomException(CommonErrorCode.RESOURCE_NOT_FOUND) }

        temporaryBusinessHour.adminUpdate(requestDto.date, requestDto.businessHour)
    }

    fun deleteTemporaryBusinessHour(hourId: Long) {
        temporaryBusinessHourRepository.deleteById(hourId)
    }

    fun getFixedBusinessHour(placeId: Long): WeeklyBusinessHourDto {
        val fixedBusinessHourList = fixedBusinessHourRepository.findAllByPlaceId(
            placeId
        )

        val fixedBusinessHourResponseDto = WeeklyBusinessHourDto()
        for (fixedBusinessHour in fixedBusinessHourList) {
            fixedBusinessHourResponseDto.updateBusinessHour(
                fixedBusinessHour.dayOfWeek,
                BusinessHour.fromFixedBusinessHour(fixedBusinessHour)
            )
        }
        return fixedBusinessHourResponseDto
    }

    fun getTemporaryBusinessHour(
        placeId: Long,
        month: Int
    ): List<TemporaryBusinessHourDto> {
        val tempBusinessHourList = temporaryBusinessHourRepository.findAllByPlaceIdAndMonth(
            placeId, month
        )

        val tempBusinessHourResponseDto: MutableList<TemporaryBusinessHourDto> = ArrayList()
        for (temporaryBusinessHour in tempBusinessHourList) {
            tempBusinessHourResponseDto.add(
                TemporaryBusinessHourDto(
                    id = temporaryBusinessHour.id,
                    date = temporaryBusinessHour.date,
                    businessHour = BusinessHour.fromTemporaryBusinessHour(temporaryBusinessHour)
                )
            )
        }
        return tempBusinessHourResponseDto
    }

    @Transactional
    fun createPlace(
        requestDto: AdminCreatePlaceRequestDto,
        member: Member
    ): AdminCreatePlaceResponseDto {
        val position = requestDto.position

        val place = Place.builder()
            .name(requestDto.name)
            .region(requestDto.region)
            .district(requestDto.district)
            .address(requestDto.address)
            .contact(requestDto.contact)
            .additionalInfo(requestDto.additionalInfo)
            .placeUrl(requestDto.placeUrl)
            .longitude(position.longitude)
            .latitude(position.latitude)
            .registrantComment(requestDto.registrantComment)
            .isApproved(false)
            .kakaoPlaceId(requestDto.kakaoPlaceId)
            .isFromAdmin(true)
            .member(member)
            .build()

        val subwayStationIdList = requestDto.subwayStationIdList
        val subwayStationList = subwayStationRepository.findAllById(
            subwayStationIdList
        )
            .stream()
            .sorted(
                Comparator.comparingInt { station: SubwayStation ->
                    subwayStationIdList.indexOf(
                        station.id
                    )
                })
            .toList()

        val subwayStationPlaceList = IntStream.range(
            0,
            subwayStationList.size
        )
            .mapToObj { i: Int ->
                SubwayStationPlace.builder()
                    .subwayStation(subwayStationList[i])
                    .place(place)
                    .isMain(i == 0)
                    .build()
            }
            .collect(Collectors.toList())

        val categoryMappingList: MutableList<PlaceCategoryMapping> = ArrayList()
        for (categoryRequestDto in requestDto.categoryList) {
            categoryMappingList.add(
                PlaceCategoryMapping.builder()
                    .category(categoryRequestDto.category)
                    .categoryGroup(categoryRequestDto.categoryGroup)
                    .place(place)
                    .build()
            )
        }

        place.adminAddCategoryAndSubwayStation(categoryMappingList, subwayStationPlaceList)

        placeRepository.save(place)
        return AdminCreatePlaceResponseDto(placeId = place.id)
    }

    @Transactional
    fun togglePlaceApprove(placeId: Long, approve: Boolean) {
        val place = placeRepository.findById(placeId)
            .orElseThrow { CustomException((CommonErrorCode.RESOURCE_NOT_FOUND)) }

        place.adminUpdateIsApproved(approve)
    }

    fun deletePlace(placeId: Long) {
        placeRepository.deleteById(placeId)
    }

    fun deleteMenuImageFileList(placeId: Long) {
        try {
            val menuList = menuRepository.findAllByPlaceId(placeId)
            val objectIdentifierList = menuList.stream()
                .map { m: Menu -> ObjectIdentifier.builder().key(m.imageS3Key).build() }
                .toList()

            s3Manager.deleteObjects(s3Manager.bucket, objectIdentifierList)
        } catch (e: SdkException) {
            throw CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR)
        }
    }

    fun getDistrictListOrderByName(region: Region): List<DistrictResponseDto> {
        return region.districtList.stream()
            .sorted(Comparator.comparing { obj: District -> obj.title })
            .map { district: District? -> DistrictResponseDto.fromDistrict(district) }
            .toList()
    }

    fun createMenuImageS3PutPresignedUrl(placeId: Long): AdminS3PutPresignedUrlResponseDto {
        val filename = FileUtils.generateUniqueFilename()
        val s3Key = S3Manager.MENU_IMAGE_S3_PREFIX + placeId + "/" + filename

        val url = s3Manager.generatePutObjectPresignedUrl(s3Manager.bucket, s3Key)
        return AdminS3PutPresignedUrlResponseDto(
            url = url,
            filename = filename
        )
    }

    fun createMenuImageS3DeletePresignedUrl(s3Key: @NotBlank String): AdminS3DeletePresignedUrlResponseDto {
        val url = s3Manager.generateDeleteObjectPresignedUrl(s3Manager.bucket, s3Key)
        return AdminS3DeletePresignedUrlResponseDto(
            url = url
        )
    }
}
