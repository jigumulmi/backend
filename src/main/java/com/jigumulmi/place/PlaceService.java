package com.jigumulmi.place;

import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.place.domain.Menu;
import com.jigumulmi.place.domain.Restaurant;
import com.jigumulmi.place.domain.Review;
import com.jigumulmi.place.domain.ReviewReply;
import com.jigumulmi.place.domain.SubwayStation;
import com.jigumulmi.place.domain.SubwayStationPlace;
import com.jigumulmi.place.dto.request.CreatePlaceRequestDto;
import com.jigumulmi.place.dto.request.CreateReviewReplyRequestDto;
import com.jigumulmi.place.dto.request.CreateReviewRequestDto;
import com.jigumulmi.place.dto.request.GetPlaceListRequestDto;
import com.jigumulmi.place.dto.request.UpdateReviewReplyRequestDto;
import com.jigumulmi.place.dto.request.UpdateReviewRequestDto;
import com.jigumulmi.place.dto.response.OverallReviewResponseDto;
import com.jigumulmi.place.dto.response.RestaurantDetailResponseDto;
import com.jigumulmi.place.dto.response.RestaurantDetailResponseDto.MenuDto;
import com.jigumulmi.place.dto.response.RestaurantDetailResponseDto.OpeningHourDto;
import com.jigumulmi.place.dto.response.RestaurantResponseDto;
import com.jigumulmi.place.dto.response.RestaurantResponseDto.PositionDto;
import com.jigumulmi.place.dto.response.ReviewListResponseDto;
import com.jigumulmi.place.dto.response.ReviewReplyResponseDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import com.jigumulmi.place.repository.CustomPlaceRepository;
import com.jigumulmi.place.repository.MenuRepository;
import com.jigumulmi.place.repository.RestaurantRepository;
import com.jigumulmi.place.repository.ReviewReplyRepository;
import com.jigumulmi.place.repository.ReviewRepository;
import com.jigumulmi.place.repository.SubwayStationPlaceRepository;
import com.jigumulmi.place.repository.SubwayStationRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final SubwayStationRepository subwayStationRepository;
    private final SubwayStationPlaceRepository subwayStationPlaceRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewReplyRepository reviewReplyRepository;
    private final CustomPlaceRepository customPlaceRepository;

    public List<SubwayStationResponseDto> getSubwayStationList(String stationName) {
        List<SubwayStation> subwayStationList = subwayStationRepository.findAllByStationNameStartsWith(
            stationName, Sort.by(Direction.ASC, "stationName"));

        ArrayList<SubwayStationResponseDto> responseDtoList = new ArrayList<>();
        for (SubwayStation subwayStation : subwayStationList) {
            SubwayStationResponseDto responseDto = SubwayStationResponseDto.builder()
                .id(subwayStation.getId())
                .stationName(subwayStation.getStationName())
                .lineNumber(subwayStation.getLineNumber())
                .build();

            responseDtoList.add(responseDto);
        }

        return responseDtoList;
    }

    @Transactional
    public void registerPlace(CreatePlaceRequestDto requestDto) {

        Restaurant newRestaurant = Restaurant.builder()
            .name(requestDto.getName())
            .registrantComment(requestDto.getRegistrantComment())
            .isApproved(false)
            .build();

        SubwayStation subwayStation = subwayStationRepository.findById(
                requestDto.getSubwayStationId())
            .orElseThrow(IllegalArgumentException::new);

        SubwayStationPlace subwayStationPlace = SubwayStationPlace.builder()
            .isMain(true)
            .subwayStation(subwayStation)
            .restaurant(newRestaurant)
            .build();

        ArrayList<Menu> menuList = new ArrayList<>();
        for (String menuName : requestDto.getMenuList()) {
            Menu menu = Menu.builder().name(menuName).restaurant(newRestaurant).build();
            menuList.add(menu);
        }

        restaurantRepository.save(newRestaurant);
        menuRepository.saveAll(menuList);
        subwayStationPlaceRepository.save(subwayStationPlace);
    }

    public List<RestaurantResponseDto> getPlaceList(GetPlaceListRequestDto requestDto) {
        Long subwayStationId = requestDto.getSubwayStationId();
        Long placeId = requestDto.getPlaceId();

        return customPlaceRepository.getRestaurantList(placeId, subwayStationId);
    }

    @Transactional(readOnly = true)
    public RestaurantDetailResponseDto getPlaceDetail(Long placeId) {
        Restaurant restaurant = customPlaceRepository.getRestaurantDetail(placeId);

        List<MenuDto> menuList = restaurant.getMenuList().stream().map(MenuDto::from).toList();

        Double averageRating = customPlaceRepository.getAverageRatingByPlaceId(placeId);
        Long totalCount = reviewRepository.countByRestaurantId(placeId);
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

        List<SubwayStationPlace> subwayStationPlaceList = restaurant.getSubwayStationPlaceList();
        List<SubwayStationResponseDto> subwayStationDtoList = new ArrayList<>();
        for (SubwayStationPlace subwayStationPlace : subwayStationPlaceList) {
            SubwayStation subwayStation = subwayStationPlace.getSubwayStation();
            subwayStationDtoList.add(
                SubwayStationResponseDto.builder()
                    .id(subwayStation.getId())
                    .lineNumber(subwayStation.getLineNumber())
                    .stationName(subwayStation.getStationName())
                    .build()
            );
        }

        return RestaurantDetailResponseDto.builder()
            .id(restaurant.getId())
            .name(restaurant.getName())
            .mainImageUrl(restaurant.getMainImageUrl())
            .position(
                PositionDto.builder()
                    .latitude(restaurant.getLatitude())
                    .longitude(restaurant.getLongitude())
                    .build()
            )
            .subwayStationList(subwayStationDtoList)
            .category(restaurant.getCategory())
            .address(restaurant.getAddress())
            .contact(restaurant.getContact())
            .menuList(menuList)
            .openingHour(
                OpeningHourDto.builder()
                    .openingHourSun(restaurant.getOpeningHourSun())
                    .openingHourMon(restaurant.getOpeningHourMon())
                    .openingHourTue(restaurant.getOpeningHourTue())
                    .openingHourWed(restaurant.getOpeningHourWed())
                    .openingHourThu(restaurant.getOpeningHourThu())
                    .openingHourFri(restaurant.getOpeningHourFri())
                    .openingHourSat(restaurant.getOpeningHourSat())
                    .build()
            )
            .additionalInfo(restaurant.getAdditionalInfo())
            .overallReview(overallReviewResponseDto)
            .build();
    }

    public void postReview(CreateReviewRequestDto requestDto, Member member) {
        Restaurant restaurant = restaurantRepository.findById(requestDto.getPlaceId())
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));
        Review review = Review.builder()
            .restaurant(restaurant)
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
}
