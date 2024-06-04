package com.jigumulmi.place;

import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.place.domain.Menu;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.domain.Review;
import com.jigumulmi.place.domain.ReviewReaction;
import com.jigumulmi.place.domain.ReviewReply;
import com.jigumulmi.place.domain.ReviewReplyReaction;
import com.jigumulmi.place.domain.SubwayStation;
import com.jigumulmi.place.domain.SubwayStationLineMapping;
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
import com.jigumulmi.place.dto.response.PlaceDetailResponseDto.OpeningHourDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto.PositionDto;
import com.jigumulmi.place.dto.response.ReviewListResponseDto;
import com.jigumulmi.place.dto.response.ReviewReplyResponseDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto.SubwayStationLineDto;
import com.jigumulmi.place.repository.CustomPlaceRepository;
import com.jigumulmi.place.repository.MenuRepository;
import com.jigumulmi.place.repository.PlaceRepository;
import com.jigumulmi.place.repository.ReviewReactionRepository;
import com.jigumulmi.place.repository.ReviewReplyReactionRepository;
import com.jigumulmi.place.repository.ReviewReplyRepository;
import com.jigumulmi.place.repository.ReviewRepository;
import com.jigumulmi.place.repository.SubwayStationPlaceRepository;
import com.jigumulmi.place.repository.SubwayStationRepository;
import com.jigumulmi.place.vo.Reaction;
import java.util.ArrayList;
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
    private final SubwayStationPlaceRepository subwayStationPlaceRepository;
    private final PlaceRepository placeRepository;
    private final MenuRepository menuRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewReplyRepository reviewReplyRepository;
    private final ReviewReactionRepository reviewReactionRepository;
    private final ReviewReplyReactionRepository reviewReplyReactionRepository;
    private final CustomPlaceRepository customPlaceRepository;

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

        placeRepository.save(newPlace);
        menuRepository.saveAll(menuList);
        subwayStationPlaceRepository.save(subwayStationPlace);
    }

    public List<PlaceResponseDto> getPlaceList(GetPlaceListRequestDto requestDto) {
        Long subwayStationId = requestDto.getSubwayStationId();

        return customPlaceRepository.getPlaceList(subwayStationId);
    }

    @Transactional(readOnly = true)
    public PlaceDetailResponseDto getPlaceDetail(Long placeId) {
        Place place = customPlaceRepository.getPlaceDetail(placeId);

        List<MenuDto> menuList = place.getMenuList().stream().map(MenuDto::from).toList();

        Double averageRating = customPlaceRepository.getAverageRatingByPlaceId(placeId);
        Long totalCount = reviewRepository.countByPlaceId(placeId);
        Map<Integer, Long> reviewRatingStatMap = customPlaceRepository.getReviewRatingStatsByPlaceId(
            placeId);
        for (int i = 1; i <= 5; i++) {
            reviewRatingStatMap.putIfAbsent(i, 0L);
        }

        OverallReviewResponseDto overallReviewResponseDto = OverallReviewResponseDto.builder()
            .averageRating(averageRating)
            .totalCount(totalCount)
            .statistics(reviewRatingStatMap)
            .build();

        SubwayStationPlace subwayStationPlace = place.getSubwayStationPlaceList().stream()
            .findFirst()
            .orElseThrow(() -> new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR));
        SubwayStation subwayStation = subwayStationPlace.getSubwayStation();
        // TODO 쿼리 개선
        List<SubwayStationLineDto> subwayStationLineDtoList = subwayStation.getSubwayStationLineMappingList()
            .stream()
            .map(SubwayStationLineMapping::getSubwayStationLine).map(SubwayStationLineDto::from)
            .toList();

        return PlaceDetailResponseDto.builder()
            .id(place.getId())
            .name(place.getName())
            .mainImageUrl(place.getMainImageUrl())
            .position(
                PositionDto.builder()
                    .latitude(place.getLatitude())
                    .longitude(place.getLongitude())
                    .build()
            )
            .subwayStation(
                SubwayStationResponseDto.builder()
                    .id(subwayStation.getId())
                    .stationName(subwayStation.getStationName())
                    .isMain(subwayStationPlace.getIsMain())
                    .subwayStationLineList(subwayStationLineDtoList)
                    .build()
            )
            .category(place.getCategory())
            .address(place.getAddress())
            .contact(place.getContact())
            .menuList(menuList)
            .openingHour(
                OpeningHourDto.builder()
                    .openingHourSun(place.getOpeningHourSun())
                    .openingHourMon(place.getOpeningHourMon())
                    .openingHourTue(place.getOpeningHourTue())
                    .openingHourWed(place.getOpeningHourWed())
                    .openingHourThu(place.getOpeningHourThu())
                    .openingHourFri(place.getOpeningHourFri())
                    .openingHourSat(place.getOpeningHourSat())
                    .build()
            )
            .additionalInfo(place.getAdditionalInfo())
            .overallReview(overallReviewResponseDto)
            .build();
    }

    public void postReview(CreateReviewRequestDto requestDto, Member member) {
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

    public List<ReviewListResponseDto> getReviewList(Member member, Long placeId) {
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

    public void deleteReviewReply(Long reviewReplyId, Member member) {
        ReviewReply reviewReply = reviewReplyRepository.findByIdAndMember(reviewReplyId, member);
        reviewReplyRepository.delete(reviewReply);
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
}
