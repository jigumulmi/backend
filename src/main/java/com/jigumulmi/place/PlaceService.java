package com.jigumulmi.place;

import com.jigumulmi.common.PagedResponseDto;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.place.domain.Review;
import com.jigumulmi.place.domain.ReviewReply;
import com.jigumulmi.place.dto.MenuDto;
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
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceManager placeManager;

    public List<SubwayStationResponseDto> getSubwayStationList(String stationName) {
        return placeManager.getSubwayStationListByName(stationName);
    }

    public PlaceBasicResponseDto getPlaceBasic(Long placeId) {
        return placeManager.getPlaceBasic(placeId);
    }

    public PagedResponseDto<MenuDto> getPlaceMenu(Pageable pageable, Long placeId) {
        return placeManager.getPlaceMenu(pageable, placeId);
    }

    public ReviewStatisticsResponseDto getReviewStatistics(Long placeId) {
        return placeManager.getReviewStatistics(placeId);
    }

    public PagedResponseDto<ReviewImageResponseDto> getReviewImage(Pageable pageable,
        Long placeId) {
        return placeManager.getReviewImage(pageable, placeId);
    }

    public void postReview(Long placeId, CreateReviewRequestDto requestDto, Member member) {
        placeManager.checkActiveReview(placeId, member);

        List<String> s3KeyList = placeManager.saveReviewImage(placeId, requestDto.getImageList());
        placeManager.createReview(placeId, requestDto, member, s3KeyList);
    }

    public void postReviewReply(Long reviewId, CreateReviewReplyRequestDto requestDto,
        Member member) {
        placeManager.postReviewReply(reviewId, requestDto, member);
    }

    public PagedResponseDto<ReviewResponseDto> getReviewList(Member requestMember,
        Pageable pageable, Long placeId) {
        return placeManager.getReviewList(requestMember, pageable, placeId);
    }

    public List<ReviewReplyResponseDto> getReviewReplyList(Member member, Long reviewId) {
        return placeManager.getReviewReplyList(member, reviewId);
    }

    public void updateReview(Long reviewId, UpdateReviewRequestDto requestDto, Member member) {
        Review review = placeManager.getReview(reviewId, member);

        List<String> newS3KeyList = placeManager.saveReviewImage(review.getPlace().getId(),
            requestDto.getNewImageList());
        List<String> trashS3KeyList = placeManager.updateReview(review, requestDto, newS3KeyList);
        placeManager.deleteReviewImage(trashS3KeyList);
    }

    public void updateReviewReply(Long replyId, UpdateReviewReplyRequestDto requestDto,
        Member member) {
        placeManager.updateReviewReply(replyId, requestDto, member);
    }

    public void deleteReview(Long reviewId, Member member) {
        List<String> trashS3KeyList = placeManager.softOrHardDeleteReview(reviewId, member);
        placeManager.deleteReviewImage(trashS3KeyList);
    }

    @Transactional
    public void deleteReviewReply(Long reviewReplyId, Member member) {
        ReviewReply reviewReply = placeManager.deleteReviewReply(reviewReplyId, member);
        placeManager.hardDeleteReviewIfNeeded(reviewReply);
    }

    public List<PlaceCategoryGroup> getPlaceCategoryGroupList() {
        return Arrays.stream(PlaceCategoryGroup.values()).toList();
    }

    public List<PlaceCategory> getPlaceCategoryList(PlaceCategoryGroup placeCategoryGroup) {
        return placeCategoryGroup.getPlaceCategoryList();
    }

    public S3PutPresignedUrlResponseDto createMenuImageS3PutPresignedUrl(
        MenuImageS3PutPresignedUrlRequestDto requestDto) {
        return placeManager.createMenuImageS3PutPresignedUrl(requestDto.getFileExtension());
    }

    public S3DeletePresignedUrlResponseDto createMenuImageS3DeletePresignedUrl(
        MenuImageS3DeletePresignedUrlRequestDto requestDto) {
        return placeManager.createMenuImageS3DeletePresignedUrl(requestDto.getS3Key());
    }
}
