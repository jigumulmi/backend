package com.jigumulmi.place;

import com.jigumulmi.config.security.OptionalAuthUser;
import com.jigumulmi.config.security.RequiredAuthUser;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.place.dto.request.CreatePlaceRequestDto;
import com.jigumulmi.place.dto.request.CreateReviewReplyRequestDto;
import com.jigumulmi.place.dto.request.CreateReviewRequestDto;
import com.jigumulmi.place.dto.request.GetPlaceListRequestDto;
import com.jigumulmi.place.dto.request.UpdateReviewReplyRequestDto;
import com.jigumulmi.place.dto.request.UpdateReviewRequestDto;
import com.jigumulmi.place.dto.response.PlaceDetailResponseDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto;
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
            @Content(array = @ArraySchema(schema = @Schema(implementation = PlaceResponseDto.class)))})}
    )
    @GetMapping("")
    public ResponseEntity<?> getPlaceList(
        @ParameterObject @ModelAttribute GetPlaceListRequestDto requestDto) {
        List<PlaceResponseDto> placeList = placeService.getPlaceList(requestDto);
        return ResponseEntity.ok().body(placeList);
    }

    @Operation(summary = "장소 상세 조회")
    @ApiResponses(
        value = {@ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = PlaceDetailResponseDto.class))})}
    )
    @GetMapping("/detail/{placeId}")
    public ResponseEntity<?> getPlaceDetail(@PathVariable(name = "placeId") Long placeId) {
        PlaceDetailResponseDto placeDetail = placeService.getPlaceDetail(placeId);
        return ResponseEntity.ok().body(placeDetail);
    }

    @Operation(summary = "리뷰 등록")
    @ApiResponse(responseCode = "201")
    @PostMapping("/review")
    public ResponseEntity<?> postReview(@Valid @RequestBody CreateReviewRequestDto requestDto,
        @RequiredAuthUser Member member) {
        placeService.postReview(requestDto, member);
        return ResponseEntity.status(HttpStatus.CREATED).body("Post review success");
    }

    @Operation(summary = "리뷰의 답글 등록")
    @ApiResponse(responseCode = "201")
    @PostMapping("/review/reply")
    public ResponseEntity<?> postReviewReply(
        @Valid @RequestBody CreateReviewReplyRequestDto requestDto,
        @RequiredAuthUser Member member) {
        placeService.postReviewReply(requestDto, member);
        return ResponseEntity.status(HttpStatus.CREATED).body("Post review reply success");
    }

    @Operation(summary = "리뷰 리스트 조회")
    @ApiResponses(
        value = {@ApiResponse(responseCode = "200", content = {
            @Content(array = @ArraySchema(schema = @Schema(implementation = ReviewListResponseDto.class)))})}
    )
    @GetMapping("/review")
    public ResponseEntity<?> getReviewList(@OptionalAuthUser Member member,
        @RequestParam(name = "placeId") Long placeId) {
        List<ReviewListResponseDto> reviewList = placeService.getReviewList(member, placeId);
        return ResponseEntity.ok().body(reviewList);
    }

    @Operation(summary = "답글 리스트 조회")
    @ApiResponses(
        value = {@ApiResponse(responseCode = "200", content = {
            @Content(array = @ArraySchema(schema = @Schema(implementation = ReviewReplyResponseDto.class)))})}
    )
    @GetMapping("/review/reply")
    public ResponseEntity<?> getReviewReplyList(
        @OptionalAuthUser Member member,
        @RequestParam(name = "reviewId") Long reviewId) {
        List<ReviewReplyResponseDto> reviewReplyList = placeService.getReviewReplyList(member,
            reviewId);
        return ResponseEntity.ok().body(reviewReplyList);
    }

    @Operation(summary = "리뷰 수정", description = "rating과 content 중 수정하지 않는 값은 null로 부탁드립니다")
    @ApiResponse(responseCode = "204")
    @PutMapping("/review")
    public ResponseEntity<?> updateReview(@Valid @RequestBody UpdateReviewRequestDto requestDto,
        @RequiredAuthUser Member member) {
        placeService.updateReview(requestDto, member);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Update review success");
    }

    @Operation(summary = "답글 수정")
    @ApiResponse(responseCode = "204")
    @PutMapping("/review/reply")
    public ResponseEntity<?> updateReviewReply(
        @Valid @RequestBody UpdateReviewReplyRequestDto requestDto,
        @RequiredAuthUser Member member) {
        placeService.updateReviewReply(requestDto, member);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Update review reply success");
    }

    @Operation(summary = "리뷰 삭제")
    @ApiResponse(responseCode = "204")
    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable(name = "reviewId") Long reviewId,
        @RequiredAuthUser Member member) {
        placeService.deleteReview(reviewId, member);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Delete review success");
    }

    @Operation(summary = "답글 삭제")
    @ApiResponse(responseCode = "204")
    @DeleteMapping("/review/reply/{reviewReplyId}")
    public ResponseEntity<?> deleteReviewReply(
        @PathVariable(name = "reviewReplyId") Long reviewReplyId,
        @RequiredAuthUser Member member) {
        placeService.deleteReviewReply(reviewReplyId, member);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Delete review reply success");
    }

    @Operation(summary = "리뷰 좋아요 등록")
    @ApiResponse(responseCode = "204")
    @PostMapping("/review/{reviewId}/like")
    public ResponseEntity<?> createReviewLike(@PathVariable(name = "reviewId") Long reviewId,
        @RequiredAuthUser Member member) {
        placeService.createReviewLike(reviewId, member);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Like review success");
    }

    @Operation(summary = "답글 좋아요 등록")
    @ApiResponse(responseCode = "204")
    @PostMapping("/review/reply/{reviewReplyId}/like")
    public ResponseEntity<?> createReviewReplyLike(
        @PathVariable(name = "reviewReplyId") Long reviewReplyId,
        @RequiredAuthUser Member member) {
        placeService.createReviewReplyLike(reviewReplyId, member);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Like review reply success");
    }

    @Operation(summary = "리뷰 좋아요 삭제")
    @ApiResponse(responseCode = "204")
    @DeleteMapping("/review/like")
    public ResponseEntity<?> deleteReviewLike(
        @RequestParam(name = "reviewReactionId") Long reviewReactionId,
        @RequiredAuthUser Member member) {
        placeService.deleteReviewLike(reviewReactionId, member);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Delete review like");
    }

    @Operation(summary = "답글 좋아요 삭제")
    @ApiResponse(responseCode = "204")
    @DeleteMapping("/review/reply/like")
    public ResponseEntity<?> deleteReviewReplyLike(
        @RequestParam(name = "reviewReplyReactionId") Long reviewReplyReactionId,
        @RequiredAuthUser Member member) {
        placeService.deleteReviewReplyLike(reviewReplyReactionId, member);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Delete review reply like");
    }
}
