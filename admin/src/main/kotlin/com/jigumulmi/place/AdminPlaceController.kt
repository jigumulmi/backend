package com.jigumulmi.place

import com.jigumulmi.common.PagedResponseDto
import com.jigumulmi.config.security.RequiredAuthUser
import com.jigumulmi.member.domain.Member
import com.jigumulmi.place.dto.ImageDto
import com.jigumulmi.place.dto.MenuDto
import com.jigumulmi.place.dto.WeeklyBusinessHourDto
import com.jigumulmi.place.dto.request.*
import com.jigumulmi.place.dto.response.*
import com.jigumulmi.place.dto.response.AdminPlaceListResponseDto.PlaceDto
import com.jigumulmi.place.vo.Region
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Tag(name = "장소 관리")
@RestController
@RequestMapping("/admin/place")
class AdminPlaceController(
    private val adminPlaceService: AdminPlaceService
) {

    @Operation(summary = "장소 리스트 조회")
    @GetMapping("")
    fun getPlaceList(
        @ParameterObject pageable: Pageable,
        @ModelAttribute requestDto: AdminGetPlaceListRequestDto
    ): ResponseEntity<PagedResponseDto<PlaceDto>> {
        val placeList = adminPlaceService.getPlaceList(pageable, requestDto)
        return ResponseEntity.ok().body(placeList)
    }

    @Operation(summary = "장소 기본 정보 조회")
    @GetMapping("/{placeId}/basic")
    fun getPlaceBasic(@PathVariable placeId: Long): ResponseEntity<AdminPlaceBasicResponseDto> {
        val responseDto = adminPlaceService.getPlaceBasic(placeId)
        return ResponseEntity.ok().body(responseDto)
    }

    @Operation(summary = "장소 기본 정보 수정", description = "덮어쓰는 로직이므로 수정되지 않은 기존 데이터도 필요")
    @ApiResponses(
        ApiResponse(responseCode = "201"),
        ApiResponse(responseCode = "400", description = "승인된 장소 수정 시 데이터 누락")
    )
    @PutMapping("/{placeId}/basic")
    fun updatePlaceBasic(
        @PathVariable placeId: Long,
        @RequestBody requestDto: AdminCreatePlaceRequestDto
    ): ResponseEntity<*> {
        requestDto.validate()
        adminPlaceService.updatePlaceBasic(placeId, requestDto)
        return ResponseEntity.status(HttpStatus.CREATED).build<Any>()
    }

    @Operation(summary = "장소 이미지 조회")
    @GetMapping("/{placeId}/image")
    fun getPlaceImage(@PathVariable placeId: Long): ResponseEntity<List<ImageDto?>?> {
        val responseDto = adminPlaceService.getPlaceImage(placeId)
        return ResponseEntity.ok().body(responseDto)
    }

    @Operation(summary = "장소 이미지 수정", description = "덮어쓰는 로직이므로 수정되지 않은 기존 데이터도 필요")
    @ApiResponses(
        ApiResponse(responseCode = "201"),
        ApiResponse(responseCode = "400", description = "승인된 장소 수정 시 데이터 누락")
    )
    @PutMapping("/{placeId}/image")
    fun updatePlaceImage(
        @PathVariable placeId: Long,
        @RequestBody requestDto: List<ImageDto>
    ): ResponseEntity<*> {
        adminPlaceService.updatePlaceImage(placeId, requestDto)
        return ResponseEntity.status(HttpStatus.CREATED).build<Any>()
    }

    @Operation(summary = "장소 메뉴 조회")
    @GetMapping("/{placeId}/menu")
    fun getMenu(@PathVariable placeId: Long): ResponseEntity<List<MenuDto>> {
        val responseDto = adminPlaceService.getMenu(placeId)
        return ResponseEntity.ok().body(responseDto)
    }

    @Operation(summary = "장소 메뉴 수정", description = "덮어쓰는 로직이므로 수정되지 않은 기존 데이터도 필요")
    @ApiResponses(
        ApiResponse(responseCode = "201"),
        ApiResponse(responseCode = "400", description = "승인된 장소 수정 시 데이터 누락")
    )
    @PutMapping("/{placeId}/menu")
    fun updateMenu(
        @PathVariable placeId: Long,
        @RequestBody requestDto: List<MenuDto>
    ): ResponseEntity<*> {
        adminPlaceService.updateMenu(placeId, requestDto)
        return ResponseEntity.status(HttpStatus.CREATED).build<Any>()
    }

    @Operation(summary = "장소 고정 영업시간 수정", description = "덮어쓰는 로직이므로 수정되지 않은 기존 데이터도 필요")
    @ApiResponse(responseCode = "201")
    @PutMapping("/{placeId}/business-hour/fixed")
    fun updateFixedBusinessHour(
        @PathVariable placeId: Long,
        @RequestBody requestDto: @Valid WeeklyBusinessHourDto
    ): ResponseEntity<*> {
        adminPlaceService.updateFixedBusinessHour(placeId, requestDto)
        return ResponseEntity.status(HttpStatus.CREATED).build<Any>()
    }

    @Operation(summary = "장소 변동 영업시간 생성")
    @ApiResponse(responseCode = "201")
    @PostMapping("/{placeId}/business-hour/temporary")
    fun createTemporaryBusinessHour(
        @PathVariable placeId: Long,
        @RequestBody requestDto: @Valid AdminCreateTemporaryBusinessHourRequestDto
    ): ResponseEntity<*> {
        adminPlaceService.createTemporaryBusinessHour(placeId, requestDto)
        return ResponseEntity.status(HttpStatus.CREATED).build<Any>()
    }

    @Operation(summary = "장소 변동 영업시간 수정", description = "덮어쓰는 로직이므로 수정되지 않은 기존 데이터도 필요, 날짜만 변경도 가능")
    @ApiResponse(responseCode = "201")
    @PutMapping("/{placeId}/business-hour/temporary/{temporaryBusinessHourId}")
    fun updateTemporaryBusinessHour(
        @PathVariable placeId: Long,
        @PathVariable temporaryBusinessHourId: Long,
        @RequestBody requestDto: @Valid AdminCreateTemporaryBusinessHourRequestDto
    ): ResponseEntity<*> {
        adminPlaceService.updateTemporaryBusinessHour(temporaryBusinessHourId, requestDto)
        return ResponseEntity.status(HttpStatus.CREATED).build<Any>()
    }

    @Operation(summary = "장소 변동 영업시간 삭제")
    @ApiResponse(responseCode = "204")
    @DeleteMapping("/{placeId}/business-hour/temporary/{temporaryBusinessHourId}")
    fun deleteTemporaryBusinessHour(
        @PathVariable placeId: Long,
        @PathVariable temporaryBusinessHourId: Long
    ): ResponseEntity<*> {
        adminPlaceService.deleteTemporaryBusinessHour(temporaryBusinessHourId)
        return ResponseEntity.noContent().build<Any>()
    }

    @Operation(summary = "장소 영업시간 조회")
    @GetMapping("/{placeId}/business-hour")
    fun getPlaceBusinessHour(
        @PathVariable placeId: Long, @RequestParam(required = false) month: Int? = null
    ): ResponseEntity<AdminPlaceBusinessHourResponseDto> {
        val adjustedMonth = if ((month == null)) LocalDate.now().monthValue else month
        val responseDto = adminPlaceService.getPlaceBusinessHour(placeId, adjustedMonth)
        return ResponseEntity.ok().body(responseDto)
    }

    @Operation(summary = "장소 생성")
    @ApiResponse(responseCode = "201")
    @PostMapping("")
    fun createPlace(
        @RequestBody requestDto: AdminCreatePlaceRequestDto,
        @RequiredAuthUser member: Member
    ): ResponseEntity<AdminCreatePlaceResponseDto> {
        requestDto.validate()
        val responseDto = adminPlaceService.createPlace(requestDto, member)
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto)
    }

    @Operation(summary = "장소 승인/미승인")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "장소 승인 여부 업데이트 성공"),
        ApiResponse(responseCode = "400", description = "장소 승인 검증 실패")
    )
    @PostMapping("/{placeId}/approval")
    fun togglePlaceApprove(
        @PathVariable placeId: Long,
        @RequestBody requestDto: @Valid TogglePlaceApproveRequestDto
    ): ResponseEntity<*> {
        adminPlaceService.togglePlaceApprove(placeId, requestDto.approve)
        return ResponseEntity.noContent().build<Any>()
    }

    @Operation(summary = "장소 삭제")
    @ApiResponse(responseCode = "204")
    @DeleteMapping("/{placeId}")
    fun deletePlace(@PathVariable placeId: Long): ResponseEntity<*> {
        adminPlaceService.deletePlace(placeId)
        return ResponseEntity.noContent().build<Any>()
    }

    @GetMapping("/region")
    @ApiResponses(value = [ApiResponse(responseCode = "200")])
    @Operation(summary = "광역시도 조회")
    fun getRegionList(): ResponseEntity<List<Region>> {
        val regionList = adminPlaceService.getRegionList()
        return ResponseEntity.ok().body(regionList)
    }

    @Operation(summary = "시군구 조회")
    @ApiResponses(value = [ApiResponse(responseCode = "200")])
    @GetMapping("/district")
    fun getDistrictList(@RequestParam region: Region): ResponseEntity<List<DistrictResponseDto>> {
        val districtList = adminPlaceService.getDistrictList(region)
        return ResponseEntity.ok().body(districtList)
    }

    @Operation(summary = "메뉴 이미지 S3 Put Presigned Url 요청")
    @ApiResponse(responseCode = "201")
    @PostMapping("/menu/s3-put-presigned-url")
    fun createMenuImageS3PutPresignedUrl(
        @RequestBody requestDto: @Valid MenuImageS3PutPresignedUrlRequestDto,
        @RequiredAuthUser Member: Member
    ): ResponseEntity<AdminS3PutPresignedUrlResponseDto> {
        val responseDto = adminPlaceService.createMenuImageS3PutPresignedUrl(
            requestDto
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto)
    }

    @Operation(summary = "메뉴 이미지 S3 Delete Presigned Url 요청")
    @ApiResponse(responseCode = "201")
    @PostMapping("/menu/s3-delete-presigned-url")
    fun createMenuImageS3DeletePresignedUrl(
        @RequestBody requestDto: @Valid MenuImageS3DeletePresignedUrlRequestDto,
        @RequiredAuthUser Member: Member
    ): ResponseEntity<AdminS3DeletePresignedUrlResponseDto> {
        val responseDto = adminPlaceService.createMenuImageS3DeletePresignedUrl(
            requestDto
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto)
    }
}
