package com.jigumulmi.place;

import com.jigumulmi.common.PageableParams;
import com.jigumulmi.common.PagedResponseDto;
import com.jigumulmi.config.security.OptionalAuthUser;
import com.jigumulmi.config.security.RequiredAuthUser;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.place.dto.MenuDto;
import com.jigumulmi.place.dto.request.CreatePlaceRequestDto;
import com.jigumulmi.place.dto.request.CreateReviewReplyRequestDto;
import com.jigumulmi.place.dto.request.CreateReviewRequestDto;
import com.jigumulmi.place.dto.request.MenuImageS3DeletePresignedUrlRequestDto;
import com.jigumulmi.place.dto.request.MenuImageS3PutPresignedUrlRequestDto;
import com.jigumulmi.place.dto.request.UpdateReviewReplyRequestDto;
import com.jigumulmi.place.dto.request.UpdateReviewRequestDto;
import com.jigumulmi.place.dto.response.PlaceBasicResponseDto;
import com.jigumulmi.place.dto.response.ReviewImageResponseDto;
import com.jigumulmi.place.dto.response.ReviewReplyResponseDto;
import com.jigumulmi.place.dto.response.ReviewResponseDto;
import com.jigumulmi.place.dto.response.ReviewStatisticsResponseDto;
import com.jigumulmi.place.dto.response.S3DeletePresignedUrlResponseDto;
import com.jigumulmi.place.dto.response.S3PutPresignedUrlResponseDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import com.jigumulmi.place.vo.PlaceCategory;
import com.jigumulmi.place.vo.PlaceCategoryGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {

    private final PlaceService placeService;

    @Operation(summary = "지하철역 조회")
    @ApiResponses(
        value = {@ApiResponse(responseCode = "200", content = {
            @Content(array = @ArraySchema(schema = @Schema(implementation = SubwayStationResponseDto.class)))})}
    )
    @GetMapping("/subway")
    public ResponseEntity<?> getSubwayStations(
        @RequestParam(name = "stationName") String stationName) {
        List<SubwayStationResponseDto> subwayStationList = placeService.getSubwayStationList(
            stationName);
        return ResponseEntity.ok().body(subwayStationList);
    }

    @Operation(summary = "장소 등록 신청")
    @ApiResponse(responseCode = "201")
    @PostMapping("")
    public ResponseEntity<?> registerPlace(
        @Valid @RequestBody CreatePlaceRequestDto requestDto,
        @OptionalAuthUser Member member
    ) {
        placeService.registerPlace(requestDto, member);
        return ResponseEntity.status(HttpStatus.CREATED).body("Register success");
    }

    @Operation(summary = "장소 상위 카테고리 조회")
    @ApiResponses(
        value = {@ApiResponse(responseCode = "200", content = {
            @Content(array = @ArraySchema(schema = @Schema(implementation = PlaceCategoryGroup.class)))})}
    )
    @GetMapping("/category-group")
    public ResponseEntity<?> getPlaceCategoryGroupList() {
        List<PlaceCategoryGroup> placeCategoryGroupList = placeService.getPlaceCategoryGroupList();
        return ResponseEntity.ok().body(placeCategoryGroupList);
    }

    @Operation(summary = "장소 하위 카테고리 조회")
    @ApiResponses(
        value = {@ApiResponse(responseCode = "200", content = {
            @Content(array = @ArraySchema(schema = @Schema(implementation = PlaceCategory.class)))})}
    )
    @GetMapping("/category")
    public ResponseEntity<?> getPlaceCategoryList(
        @RequestParam(name = "placeCategoryGroup") PlaceCategoryGroup placeCategoryGroup) {
        List<PlaceCategory> placeCategoryList = placeService.getPlaceCategoryList(
            placeCategoryGroup);
        return ResponseEntity.ok().body(placeCategoryList);
    }

    @Operation(summary = "장소 기본정보 조회")
    @GetMapping("/{placeId}/basic")
    public ResponseEntity<PlaceBasicResponseDto> getPlaceHome(@PathVariable Long placeId) {
        PlaceBasicResponseDto placeDetail = placeService.getPlaceBasic(placeId);
        return ResponseEntity.ok().body(placeDetail);
    }

    @Operation(summary = "장소 메뉴정보 조회", description = "홈 탭과 메뉴 탭에서 모두 사용 -> size 파라미터 조정")
    @PageableParams
    @GetMapping("/{placeId}/menu")
    public ResponseEntity<PagedResponseDto<MenuDto>> getPlaceMenu(
        @ParameterObject Pageable pageable,
        @PathVariable Long placeId) {
        PagedResponseDto<MenuDto> responseDto = placeService.getPlaceMenu(pageable, placeId);
        return ResponseEntity.ok().body(responseDto);
    }

    @Operation(summary = "장소 리뷰 통계 조회", description = "홈 탭과 리뷰 탭에서 모두 사용")
    @GetMapping("/{placeId}/review/statistics")
    public ResponseEntity<ReviewStatisticsResponseDto> getReviewStatistics(
        @PathVariable Long placeId) {
        ReviewStatisticsResponseDto reviewStatistics = placeService.getReviewStatistics(placeId);
        return ResponseEntity.ok().body(reviewStatistics);
    }

    @Operation(summary = "리뷰 사진 모음 조회", description = "홈 탭과 리뷰 탭에서 모두 사용 -> size 파라미터 조정")
    @PageableParams
    @GetMapping("/{placeId}/review/image")
    public ResponseEntity<PagedResponseDto<ReviewImageResponseDto>> getReviewImage(
        @ParameterObject Pageable pageable,
        @PathVariable Long placeId) {
        PagedResponseDto<ReviewImageResponseDto> responseDto = placeService.getReviewImage(pageable,
            placeId);
        return ResponseEntity.ok().body(responseDto);
    }

    @Operation(summary = "리뷰 등록")
    @ApiResponse(responseCode = "201")
    @PostMapping(path = "/{placeId}/review", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> postReview(@PathVariable Long placeId,
        @Valid @ModelAttribute CreateReviewRequestDto requestDto,
        @RequiredAuthUser Member member) {
        placeService.postReview(placeId, requestDto, member);
        return ResponseEntity.status(HttpStatus.CREATED).body("Post review success");
    }

    @Operation(summary = "리뷰의 답글 등록")
    @ApiResponse(responseCode = "201")
    @PostMapping("/review/{reviewId}/reply")
    public ResponseEntity<?> postReviewReply(@PathVariable Long reviewId,
        @Valid @RequestBody CreateReviewReplyRequestDto requestDto,
        @RequiredAuthUser Member member) {
        placeService.postReviewReply(reviewId, requestDto, member);
        return ResponseEntity.status(HttpStatus.CREATED).body("Post review reply success");
    }

    @Operation(summary = "리뷰 목록 조회", description = "홈 탭과 리뷰 탭에서 모두 사용 -> size 파라미터 조정")
    @PageableParams
    @GetMapping("/{placeId}/review")
    public ResponseEntity<PagedResponseDto<ReviewResponseDto>> getReviewList(
        @OptionalAuthUser Member member,
        @ParameterObject Pageable pageable,
        @PathVariable Long placeId) {
        PagedResponseDto<ReviewResponseDto> reviewList = placeService.getReviewList(member,
            pageable, placeId);
        return ResponseEntity.ok().body(reviewList);
    }

    @Operation(summary = "답글 목록 조회")
    @GetMapping("/review/{reviewId}/reply")
    public ResponseEntity<List<ReviewReplyResponseDto>> getReviewReplyList(
        @OptionalAuthUser Member member,
        @PathVariable Long reviewId) {
        List<ReviewReplyResponseDto> reviewReplyList = placeService.getReviewReplyList(member,
            reviewId);
        return ResponseEntity.ok().body(reviewReplyList);
    }

    @Operation(summary = "리뷰 수정")
    @ApiResponse(responseCode = "204")
    @PutMapping(path = "/review/{reviewId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId,
        @Valid @ModelAttribute UpdateReviewRequestDto requestDto,
        @RequiredAuthUser Member member) {
        placeService.updateReview(reviewId, requestDto, member);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "답글 수정")
    @ApiResponse(responseCode = "204")
    @PutMapping("/review/reply/{reviewReplyId}")
    public ResponseEntity<?> updateReviewReply(@PathVariable Long reviewReplyId,
        @Valid @RequestBody UpdateReviewReplyRequestDto requestDto,
        @RequiredAuthUser Member member) {
        placeService.updateReviewReply(reviewReplyId, requestDto, member);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "리뷰 삭제")
    @ApiResponse(responseCode = "204")
    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId,
        @RequiredAuthUser Member member) {
        placeService.deleteReview(reviewId, member);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "답글 삭제")
    @ApiResponse(responseCode = "204")
    @DeleteMapping("/review/reply/{reviewReplyId}")
    public ResponseEntity<?> deleteReviewReply(@PathVariable Long reviewReplyId,
        @RequiredAuthUser Member member) {
        placeService.deleteReviewReply(reviewReplyId, member);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "장소 좋아요 등록/삭제 토글")
    @ApiResponse(responseCode = "204")
    @PostMapping("/{placeId}/like")
    public ResponseEntity<?> togglePlaceLike(
        @PathVariable(name = "placeId") Long placeId,
        @RequestParam(name = "toggleOn") Boolean toggleOn,
        @RequiredAuthUser Member member) {
        placeService.togglePlaceLike(placeId, toggleOn, member);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "메뉴 이미지 S3 Put Presigned Url 요청")
    @ApiResponse(responseCode = "201", content = {
        @Content(schema = @Schema(implementation = S3PutPresignedUrlResponseDto.class))})
    @PostMapping("/menu/s3-put-presigned-url")
    public ResponseEntity<?> createMenuImageS3PutPresignedUrl(
        @RequestBody MenuImageS3PutPresignedUrlRequestDto requestDto,
        @RequiredAuthUser Member Member) {
        S3PutPresignedUrlResponseDto responseDto = placeService.createMenuImageS3PutPresignedUrl(
            requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(summary = "메뉴 이미지 S3 Delete Presigned Url 요청")
    @ApiResponse(responseCode = "201", content = {
        @Content(schema = @Schema(implementation = S3DeletePresignedUrlResponseDto.class))})
    @PostMapping("/menu/s3-delete-presigned-url")
    public ResponseEntity<?> createMenuImageS3DeletePresignedUrl(
        @RequestBody MenuImageS3DeletePresignedUrlRequestDto requestDto,
        @RequiredAuthUser Member Member) {
        S3DeletePresignedUrlResponseDto responseDto = placeService.createMenuImageS3DeletePresignedUrl(
            requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
