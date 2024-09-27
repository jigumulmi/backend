package com.jigumulmi.place;

import static java.lang.Math.round;

import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import com.jigumulmi.config.exception.errorCode.PlaceErrorCode;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.place.domain.Menu;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.domain.Review;
import com.jigumulmi.place.domain.ReviewReaction;
import com.jigumulmi.place.domain.ReviewReply;
import com.jigumulmi.place.domain.ReviewReplyReaction;
import com.jigumulmi.place.domain.SubwayStation;
import com.jigumulmi.place.domain.SubwayStationPlace;
import com.jigumulmi.place.dto.request.CreatePlaceRequestDto;
import com.jigumulmi.place.dto.request.CreateReviewReplyRequestDto;
import com.jigumulmi.place.dto.request.CreateReviewRequestDto;
import com.jigumulmi.place.dto.request.GetPlaceListRequestDto;
import com.jigumulmi.place.dto.request.UpdateReviewReplyRequestDto;
import com.jigumulmi.place.dto.request.UpdateReviewRequestDto;
import com.jigumulmi.place.dto.response.OverallReviewResponseDto;
import com.jigumulmi.place.dto.response.PlaceDetailResponseDto;
import com.jigumulmi.place.dto.response.PlaceDetailResponseDto.MenuDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto.ImageDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto.SurroundingDateOpeningHour;
import com.jigumulmi.place.dto.response.ReviewReplyResponseDto;
import com.jigumulmi.place.dto.response.ReviewResponseDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import com.jigumulmi.place.repository.CustomPlaceRepository;
import com.jigumulmi.place.repository.MenuRepository;
import com.jigumulmi.place.repository.PlaceImageRepository;
import com.jigumulmi.place.repository.PlaceRepository;
import com.jigumulmi.place.repository.ReviewReactionRepository;
import com.jigumulmi.place.repository.ReviewReplyReactionRepository;
import com.jigumulmi.place.repository.ReviewReplyRepository;
import com.jigumulmi.place.repository.ReviewRepository;
import com.jigumulmi.place.repository.SubwayStationRepository;
import com.jigumulmi.place.vo.CurrentOpeningInfo;
import com.jigumulmi.place.vo.PlaceCategory;
import com.jigumulmi.place.vo.PlaceCategoryGroup;
import com.jigumulmi.place.vo.Reaction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final SubwayStationRepository subwayStationRepository;
    private final PlaceRepository placeRepository;
    private final MenuRepository menuRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewReplyRepository reviewReplyRepository;
    private final ReviewReactionRepository reviewReactionRepository;
    private final ReviewReplyReactionRepository reviewReplyReactionRepository;
    private final CustomPlaceRepository customPlaceRepository;
    private final PlaceImageRepository placeImageRepository;

    public List<SubwayStationResponseDto> getSubwayStationList(String stationName) {
        return subwayStationRepository.findAllByStationNameStartsWith(stationName)
            .stream().map(SubwayStationResponseDto::from).toList();
    }

    @Transactional
    public void registerPlace(CreatePlaceRequestDto requestDto) {

        Place newPlace = Place.builder()
            .name(requestDto.getName())
            .registrantComment(requestDto.getRegistrantComment())
            .isApproved(false)
            .build();

        ArrayList<Menu> menuList = new ArrayList<>();
        for (String menuName : requestDto.getMenuList()) {
            Menu menu = Menu.builder().name(menuName).place(newPlace).build();
            menuList.add(menu);
        }

        SubwayStation subwayStation = subwayStationRepository.findById(
                requestDto.getSubwayStationId())
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));
        SubwayStationPlace subwayStationPlace = SubwayStationPlace.builder()
            .subwayStation(subwayStation)
            .place(newPlace)
            .isMain(true)
            .build();

        newPlace.addChildren(Collections.singletonList(subwayStationPlace), menuList,
            null);

        placeRepository.save(newPlace);
    }

    public List<PlaceResponseDto> getPlaceList(GetPlaceListRequestDto requestDto) {
        Long subwayStationId = requestDto.getSubwayStationId();

        List<PlaceResponseDto> placeList = customPlaceRepository.getPlaceList(subwayStationId);
        for (PlaceResponseDto responseDto : placeList) {
            List<ImageDto> imageList = responseDto.getImageList();
            responseDto.setImageList(Collections.singletonList(imageList.getFirst()));

            PlaceCategory category = responseDto.getCategory();
            PlaceCategoryGroup placeCategoryGroup = PlaceCategoryGroup.findByPlaceCategory(
                category);
            responseDto.setCategoryGroup(placeCategoryGroup);

            SurroundingDateOpeningHour surroundingDateOpeningHour = responseDto.getSurroundingDateOpeningHour();
            String currentOpeningInfo = CurrentOpeningInfo.getCurrentOpeningInfo(
                surroundingDateOpeningHour);
            responseDto.setCurrentOpeningInfo(currentOpeningInfo);
        }

        return placeList;
    }

    @Transactional(readOnly = true)
    public PlaceDetailResponseDto getPlaceDetail(Long placeId) {
        PlaceDetailResponseDto place = customPlaceRepository.getPlaceDetail(placeId);

        SurroundingDateOpeningHour surroundingDateOpeningHour = place.getSurroundingDateOpeningHour();
        String currentOpeningInfo = CurrentOpeningInfo.getCurrentOpeningInfo(
            surroundingDateOpeningHour);

        List<MenuDto> menuList = menuRepository.findAllByPlaceId(placeId).stream()
            .map(MenuDto::from).toList();

        List<ImageDto> imageList = placeImageRepository.findAllByPlaceId(placeId).stream()
            .map(ImageDto::from).toList();

        Map<Integer, Long> reviewRatingStatMap = customPlaceRepository.getReviewRatingStatsByPlaceId(
            placeId);

        long totalCount = 0L;
        long totalRating = 0L;
        for (int i = 1; i <= 5; i++) {
            reviewRatingStatMap.putIfAbsent(i, 0L);

            Long count = reviewRatingStatMap.get(i);
            totalCount += count;
            totalRating += (count * i);
        }
        Double averageRating = round((float) totalRating / totalCount * 100) / 100.0; // 소수점 둘째자리까지

        OverallReviewResponseDto overallReviewResponseDto = OverallReviewResponseDto.builder()
            .averageRating(averageRating)
            .totalCount(totalCount)
            .statistics(reviewRatingStatMap)
            .build();

        PlaceCategory category = place.getCategory();
        PlaceCategoryGroup placeCategoryGroup = PlaceCategoryGroup.findByPlaceCategory(category);

        return PlaceDetailResponseDto.builder()
            .id(place.getId())
            .name(place.getName())
            .imageList(imageList)
            .position(
                place.getPosition()
            )
            .subwayStation(
                place.getSubwayStation()
            )
            .categoryGroup(placeCategoryGroup)
            .category(category)
            .address(place.getAddress())
            .contact(place.getContact())
            .menuList(menuList)
            .openingHour(
                place.getOpeningHour()
            )
            .additionalInfo(place.getAdditionalInfo())
            .overallReview(overallReviewResponseDto)
            .surroundingDateOpeningHour(surroundingDateOpeningHour)
            .currentOpeningInfo(currentOpeningInfo)
            .build();
    }

    public void postReview(CreateReviewRequestDto requestDto, Member member) {
        boolean canPostReview = reviewRepository.findTopByPlaceIdAndMemberIdAndDeletedAtIsNull(
            requestDto.getPlaceId(),
            member.getId()
        ).isEmpty();

        if (canPostReview) {
            Place place = placeRepository.findById(requestDto.getPlaceId())
                .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));
            Review review = Review.builder()
                .place(place)
                .content(requestDto.getContent())
                .rating(requestDto.getRating())
                .member(member)
                .reviewReplyList(Collections.emptyList())
                .build();

            reviewRepository.save(review);
        } else {
            throw new CustomException(PlaceErrorCode.DUPLICATE_REVIEW);
        }
    }

    public void postReviewReply(CreateReviewReplyRequestDto requestDto, Member member) {

        Review review = reviewRepository.findById(requestDto.getReviewId())
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));
        ReviewReply reviewReply = ReviewReply.builder()
            .review(review)
            .content(requestDto.getContent())
            .member(member)
            .build();

        reviewReplyRepository.save(reviewReply);
    }

    public List<ReviewResponseDto> getReviewList(Member member, Long placeId) {
        return customPlaceRepository.getReviewListByPlaceId(placeId, member.getId());
    }


    public List<ReviewReplyResponseDto> getReviewReplyList(Member member, Long reviewId) {
        return customPlaceRepository.getReviewReplyListByReviewId(member.getId(), reviewId);
    }

    public void updateReview(UpdateReviewRequestDto requestDto, Member member) {
        Review review = reviewRepository.findByIdAndMember(requestDto.getReviewId(), member);
        review.updateReview(requestDto.getRating(), requestDto.getContent());
        reviewRepository.save(review);
    }

    public void updateReviewReply(UpdateReviewReplyRequestDto requestDto, Member member) {
        ReviewReply reviewReply = reviewReplyRepository.findByIdAndMember(
            requestDto.getReviewReplyId(), member);
        reviewReply.updateReviewReply(requestDto.getContent());
        reviewReplyRepository.save(reviewReply);
    }

    @Transactional
    public void deleteReview(Long reviewId, Member member) {
        Review review = reviewRepository.findByIdAndMember(reviewId, member);

        List<ReviewReply> reviewReplyList = review.getReviewReplyList();
        if (reviewReplyList.isEmpty()) {
            reviewRepository.delete(review);
        } else {
            review.deleteReviewWithReplies();
            reviewRepository.save(review);
        }
    }

    @Transactional
    public void deleteReviewReply(Long reviewReplyId, Member member) {
        ReviewReply reviewReply = reviewReplyRepository.findByIdAndMember(reviewReplyId, member);

        Review review = reviewReply.getReview();

        List<ReviewReply> reviewReplyList = review.getReviewReplyList();
        int reviewReplyCount = reviewReplyList.size();

        reviewReplyRepository.delete(reviewReply);

        // 속한 리뷰가 삭제된 상태고, 해당 답글이 마지막이었던 경우
        if (review.getDeletedAt() != null && reviewReplyCount == 1) {
            reviewRepository.delete(review);
        }
    }

    public void createReviewLike(Long reviewId, Member member) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));

        ReviewReaction reviewReaction = ReviewReaction.builder()
            .member(member)
            .review(review)
            .category(Reaction.LIKE.name())
            .build();

        reviewReactionRepository.save(reviewReaction);
    }

    public void createReviewReplyLike(Long reviewReplyId, Member member) {
        ReviewReply reviewReply = reviewReplyRepository.findById(reviewReplyId)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));

        ReviewReplyReaction reviewReplyReaction = ReviewReplyReaction.builder()
            .member(member)
            .reviewReply(reviewReply)
            .category(Reaction.LIKE.name())
            .build();

        reviewReplyReactionRepository.save(reviewReplyReaction);
    }

    public void deleteReviewLike(Long reviewReactionId, Member member) {

        reviewReactionRepository.deleteByIdAndMemberId(reviewReactionId, member.getId());
    }

    public void deleteReviewReplyLike(Long reviewReplyReactionId, Member member) {

        reviewReplyReactionRepository.deleteByIdAndMemberId(reviewReplyReactionId, member.getId());
    }

    public List<PlaceCategoryGroup> getPlaceCategoryGroupList() {
        return Arrays.stream(PlaceCategoryGroup.values()).toList();
    }

    public List<PlaceCategory> getPlaceCategoryList(PlaceCategoryGroup placeCategoryGroup) {
        return placeCategoryGroup.getPlaceCategoryList();

    }
}
