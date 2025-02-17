package com.jigumulmi.place;

import com.jigumulmi.common.PagedResponseDto;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.place.domain.Review;
import com.jigumulmi.place.dto.MenuDto;
import com.jigumulmi.place.dto.UpdateReviewImageS3KeyDto;
import com.jigumulmi.place.dto.request.CreateReviewReplyRequestDto;
import com.jigumulmi.place.dto.request.CreateReviewRequestDto;
import com.jigumulmi.place.dto.request.UpdateReviewReplyRequestDto;
import com.jigumulmi.place.dto.request.UpdateReviewRequestDto;
import com.jigumulmi.place.dto.response.PlaceBasicResponseDto;
import com.jigumulmi.place.dto.response.ReviewImageResponseDto;
import com.jigumulmi.place.dto.response.ReviewReplyResponseDto;
import com.jigumulmi.place.dto.response.ReviewResponseDto;
import com.jigumulmi.place.dto.response.ReviewStatisticsResponseDto;
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

        List<String> s3KeyList = placeManager.createReview(placeId, requestDto, member);
        placeManager.saveReviewImageFileList(requestDto.getImageList(), s3KeyList);
    }

    public void postReviewReply(Long reviewId, CreateReviewReplyRequestDto requestDto,
        Member member) {
        placeManager.postReviewReply(reviewId, requestDto, member);
    }

    public PagedResponseDto<ReviewResponseDto> getReviewList(Member requestMember,
        Pageable pageable, Long placeId) {
        return placeManager.getReviewList(requestMember, pageable, placeId);
    }

    public Review getReview(Member member, Long reviewId) {
        return placeManager.getReview(reviewId, member);
    }

    public List<ReviewReplyResponseDto> getReviewReplyList(Member member, Long reviewId) {
        return placeManager.getReviewReplyList(member, reviewId);
    }

    public void updateReview(Long reviewId, UpdateReviewRequestDto requestDto, Member member) {
        UpdateReviewImageS3KeyDto s3KeyDto = placeManager.updateReview(reviewId, member, requestDto);
        placeManager.saveReviewImageFileList(requestDto.getNewImageList(), s3KeyDto.getNewS3KeyList());
        placeManager.deleteReviewImageFileList(s3KeyDto.getTrashS3KeyList());
    }

    public void updateReviewReply(Long replyId, UpdateReviewReplyRequestDto requestDto,
        Member member) {
        placeManager.updateReviewReply(replyId, requestDto, member);
    }

    public void deleteReview(Long reviewId, Member member) {
        List<String> trashS3KeyList = placeManager.softOrHardDeleteReview(reviewId, member);
        placeManager.deleteReviewImageFileList(trashS3KeyList);
    }

    @Transactional
    public void deleteReviewReply(Long reviewReplyId, Member member) {
        Review review = placeManager.deleteReviewReply(reviewReplyId, member);
        placeManager.hardDeleteReviewIfNeeded(review);
    }

    public List<PlaceCategoryGroup> getPlaceCategoryGroupList() {
        return Arrays.stream(PlaceCategoryGroup.values()).toList();
    }

    public List<PlaceCategory> getPlaceCategoryList(PlaceCategoryGroup placeCategoryGroup) {
        return placeCategoryGroup.getPlaceCategoryList();
    }
}
