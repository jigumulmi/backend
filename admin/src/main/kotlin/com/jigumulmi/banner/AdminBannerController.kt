package com.jigumulmi.banner

import com.jigumulmi.banner.dto.request.BannerPlaceMappingRequestDto
import com.jigumulmi.banner.dto.request.CreateBannerRequestDto
import com.jigumulmi.banner.dto.request.GetCandidatePlaceListRequestDto
import com.jigumulmi.banner.dto.request.UpdateBannerRequestDto
import com.jigumulmi.banner.dto.response.AdminBannerDetailResponseDto
import com.jigumulmi.banner.dto.response.AdminBannerPlaceListResponseDto
import com.jigumulmi.banner.dto.response.AdminBannerResponseDto
import com.jigumulmi.banner.dto.response.CreateBannerResponseDto
import com.jigumulmi.common.AdminPagedResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Tag(name = "배너 관리")
@RestController
@RequestMapping("/admin/banner")
class AdminBannerController(
    private val adminBannerService: AdminBannerService
) {

    @Operation(summary = "배너 생성")
    @ApiResponse(responseCode = "201")
    @PostMapping(path = [""], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createBanner(
        @ModelAttribute requestDto: @Valid CreateBannerRequestDto
    ): ResponseEntity<CreateBannerResponseDto> {
        val responseDto = adminBannerService.createBanner(requestDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto)
    }

    @GetMapping("")
    @ApiResponse(responseCode = "200")
    @Operation(summary = "배너 목록 조회")
    fun getBannerList(): ResponseEntity<List<AdminBannerResponseDto>> {
        val bannerList = adminBannerService.getBannerList()
        return ResponseEntity.ok(bannerList)
    }

    @Operation(summary = "장소 할당")
    @ApiResponse(responseCode = "201")
    @PostMapping(path = ["/{bannerId}/place"])
    fun createBannerPlace(
        @PathVariable bannerId: Long,
        @RequestBody requestDto: @Valid BannerPlaceMappingRequestDto
    ): ResponseEntity<*> {
        adminBannerService.addBannerPlace(bannerId, requestDto)
        return ResponseEntity.status(HttpStatus.CREATED).build<Any>()
    }

    @Operation(summary = "장소 할당 해제")
    @ApiResponse(responseCode = "204")
    @DeleteMapping(path = ["/{bannerId}/place"])
    fun deleteBannerPlace(
        @PathVariable bannerId: Long,
        @RequestBody requestDto: @Valid BannerPlaceMappingRequestDto
    ): ResponseEntity<*> {
        adminBannerService.removeBannerPlace(bannerId, requestDto)
        return ResponseEntity.noContent().build<Any>()
    }

    @Operation(summary = "배너 상세 조회")
    @ApiResponse(responseCode = "200")
    @GetMapping("/{bannerId}")
    fun getBannerDetail(@PathVariable bannerId: Long): ResponseEntity<AdminBannerDetailResponseDto> {
        val responseDto = adminBannerService.getBannerDetail(bannerId)
        return ResponseEntity.ok(responseDto)
    }

    @Operation(summary = "배너와 연관된 장소 목록 조회")
    @GetMapping("/{bannerId}/place")
    fun getMappedPlaceList(
        @ParameterObject pageable: Pageable,
        @PathVariable bannerId: Long
    ): ResponseEntity<AdminPagedResponseDto<AdminBannerPlaceListResponseDto.BannerPlaceDto>> {
        val responseDto = adminBannerService.getMappedPlaceList(
            pageable, bannerId
        )
        return ResponseEntity.ok(responseDto)
    }

    @Operation(summary = "배너 기본 정보 수정", description = "이미지 제외한 정보 수정")
    @ApiResponse(responseCode = "201")
    @PutMapping("/{bannerId}")
    fun updateBannerBasic(
        @PathVariable bannerId: Long,
        @RequestBody requestDto: @Valid UpdateBannerRequestDto
    ): ResponseEntity<*> {
        adminBannerService.updateBannerBasic(bannerId, requestDto)
        return ResponseEntity.status(HttpStatus.CREATED).build<Any>()
    }

    @Operation(summary = "배너 외부 이미지 수정")
    @ApiResponse(responseCode = "201")
    @PutMapping(path = ["/{bannerId}/outerImage"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateBannerOuterImage(
        @PathVariable bannerId: Long,
        @RequestParam outerImage: MultipartFile
    ): ResponseEntity<*> {
        adminBannerService.updateBannerOuterImage(bannerId, outerImage)
        return ResponseEntity.status(HttpStatus.CREATED).build<Any>()
    }

    @Operation(summary = "배너 내부 이미지 수정")
    @ApiResponse(responseCode = "201")
    @PutMapping(path = ["/{bannerId}/innerImage"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateBannerInnerImage(
        @PathVariable bannerId: Long,
        @RequestParam innerImage: MultipartFile
    ): ResponseEntity<*> {
        adminBannerService.updateBannerInnerImage(bannerId, innerImage)
        return ResponseEntity.status(HttpStatus.CREATED).build<Any>()
    }

    @Operation(summary = "배너 삭제")
    @ApiResponse(responseCode = "204")
    @DeleteMapping("/{bannerId}")
    fun deleteBanner(@PathVariable bannerId: Long): ResponseEntity<*> {
        adminBannerService.deleteBanner(bannerId)
        return ResponseEntity.noContent().build<Any>()
    }

    @Operation(summary = "할당 가능한 장소 목록 조회")
    @GetMapping("/place")
    fun getCandidatePlaceList(
        @ParameterObject pageable: Pageable,
        @ModelAttribute requestDto: @Valid GetCandidatePlaceListRequestDto
    ): ResponseEntity<AdminPagedResponseDto<AdminBannerPlaceListResponseDto.BannerPlaceDto>> {
        val responseDto = adminBannerService.getCandidatePlaceList(
            pageable, requestDto
        )
        return ResponseEntity.ok(responseDto)
    }
}
