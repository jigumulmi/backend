package com.jigumulmi.place;

import com.jigumulmi.config.security.SecureAuthUser;
import com.jigumulmi.config.security.UserDetailsImpl;
import com.jigumulmi.place.dto.request.CreatePlaceRequestDto;
import com.jigumulmi.place.dto.request.CreateReviewReplyRequestDto;
import com.jigumulmi.place.dto.request.CreateReviewRequestDto;
import com.jigumulmi.place.dto.request.GetPlaceListRequestDto;
import com.jigumulmi.place.dto.request.UpdateReviewReplyRequestDto;
import com.jigumulmi.place.dto.request.UpdateReviewRequestDto;
import com.jigumulmi.place.dto.response.RestaurantDetailResponseDto;
import com.jigumulmi.place.dto.response.RestaurantResponseDto;
import com.jigumulmi.place.dto.response.ReviewListResponseDto;
import com.jigumulmi.place.dto.response.ReviewReplyResponseDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<?> registerPlace(@Valid @RequestBody CreatePlaceRequestDto requestDto) {
        placeService.registerPlace(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Register success");
    }

    @Operation(summary = "장소 리스트 조회")
    @ApiResponses(
        value = {@ApiResponse(responseCode = "200", content = {
            @Content(array = @ArraySchema(schema = @Schema(implementation = RestaurantResponseDto.class)))})}
    )
    @GetMapping("")
    public ResponseEntity<?> getPlaceList(
        @ParameterObject @ModelAttribute GetPlaceListRequestDto requestDto) {
        List<RestaurantResponseDto> placeList = placeService.getPlaceList(requestDto);
        return ResponseEntity.ok().body(placeList);
    }

    @Operation(summary = "장소 상세 조회")
    @ApiResponses(
        value = {@ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = RestaurantDetailResponseDto.class))})}
    )
    @GetMapping("/detail/{placeId}")
    public ResponseEntity<?> getPlaceDetail(@PathVariable(name = "placeId") Long placeId) {
        RestaurantDetailResponseDto placeDetail = placeService.getPlaceDetail(placeId);
        return ResponseEntity.ok().body(placeDetail);
    }

    @Operation(summary = "리뷰 등록")
    @ApiResponse(responseCode = "201")
    @PostMapping("/review")
    public ResponseEntity<?> postReview(@Valid @RequestBody CreateReviewRequestDto requestDto,
        @SecureAuthUser UserDetailsImpl userDetails) {
        placeService.postReview(requestDto, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body("Post review success");
    }

    @Operation(summary = "리뷰의 답글 등록")
    @ApiResponse(responseCode = "201")
    @PostMapping("/review/reply")
    public ResponseEntity<?> postReviewReply(
        @Valid @RequestBody CreateReviewReplyRequestDto requestDto,
        @SecureAuthUser UserDetailsImpl userDetails) {
        placeService.postReviewReply(requestDto, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body("Post review reply success");
    }

    @Operation(summary = "리뷰 리스트 조회")
    @ApiResponses(
        value = {@ApiResponse(responseCode = "200", content = {
            @Content(array = @ArraySchema(schema = @Schema(implementation = ReviewListResponseDto.class)))})}
    )
    @GetMapping("/review")
    public ResponseEntity<?> getReviewList(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(name = "placeId") Long placeId) {
        List<ReviewListResponseDto> reviewList = placeService.getReviewList(userDetails,
            placeId);
        return ResponseEntity.ok().body(reviewList);
    }

    @Operation(summary = "답글 리스트 조회")
    @ApiResponses(
        value = {@ApiResponse(responseCode = "200", content = {
            @Content(array = @ArraySchema(schema = @Schema(implementation = ReviewReplyResponseDto.class)))})}
    )
    @GetMapping("/review/reply")
    public ResponseEntity<?> getReviewReplyList(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(name = "reviewId") Long reviewId) {
        List<ReviewReplyResponseDto> reviewReplyList = placeService.getReviewReplyList(userDetails,
            reviewId);
        return ResponseEntity.ok().body(reviewReplyList);
    }

    @Operation(summary = "리뷰 수정", description = "rating과 content 중 수정하지 않는 값은 null로 부탁드립니다")
    @ApiResponse(responseCode = "204")
    @PutMapping("/review")
    public ResponseEntity<?> updateReview(@Valid @RequestBody UpdateReviewRequestDto requestDto,
        @SecureAuthUser UserDetailsImpl userDetails) {
        placeService.updateReview(requestDto, userDetails);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Update review success");
    }

    @Operation(summary = "답글 수정")
    @ApiResponse(responseCode = "204")
    @PutMapping("/review/reply")
    public ResponseEntity<?> updateReviewReply(
        @Valid @RequestBody UpdateReviewReplyRequestDto requestDto,
        @SecureAuthUser UserDetailsImpl userDetails) {
        placeService.updateReviewReply(requestDto, userDetails);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Update review reply success");
    }

    @Operation(summary = "리뷰 삭제")
    @ApiResponse(responseCode = "204")
    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable(name = "reviewId") Long reviewId,
        @SecureAuthUser UserDetailsImpl userDetails) {
        placeService.deleteReview(reviewId, userDetails);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Delete review success");
    }

    @Operation(summary = "답글 삭제")
    @ApiResponse(responseCode = "204")
    @DeleteMapping("/review/reply/{reviewReplyId}")
    public ResponseEntity<?> deleteReviewReply(
        @PathVariable(name = "reviewReplyId") Long reviewReplyId,
        @SecureAuthUser UserDetailsImpl userDetails) {
        placeService.deleteReviewReply(reviewReplyId, userDetails);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Delete review reply success");
    }
}
