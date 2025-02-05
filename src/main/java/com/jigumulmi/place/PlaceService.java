package com.jigumulmi.place;

import static java.lang.Math.round;

import com.jigumulmi.admin.place.dto.WeeklyBusinessHourDto;
import com.jigumulmi.aws.S3Service;
import com.jigumulmi.common.FileUtils;
import com.jigumulmi.common.PagedResponseDto;
import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import com.jigumulmi.config.exception.errorCode.PlaceErrorCode;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.place.domain.Menu;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.domain.PlaceLike;
import com.jigumulmi.place.domain.Review;
import com.jigumulmi.place.domain.ReviewImage;
import com.jigumulmi.place.domain.ReviewReply;
import com.jigumulmi.place.domain.SubwayStation;
import com.jigumulmi.place.domain.SubwayStationPlace;
import com.jigumulmi.place.dto.BusinessHour;
import com.jigumulmi.place.dto.ImageDto;
import com.jigumulmi.place.dto.MenuDto;
import com.jigumulmi.place.dto.request.CreatePlaceRequestDto;
import com.jigumulmi.place.dto.request.CreateReviewReplyRequestDto;
import com.jigumulmi.place.dto.request.CreateReviewRequestDto;
import com.jigumulmi.place.dto.request.MenuImageS3DeletePresignedUrlRequestDto;
import com.jigumulmi.place.dto.request.MenuImageS3PutPresignedUrlRequestDto;
import com.jigumulmi.place.dto.request.UpdateReviewReplyRequestDto;
import com.jigumulmi.place.dto.request.UpdateReviewRequestDto;
import com.jigumulmi.place.dto.response.PlaceBasicResponseDto;
import com.jigumulmi.place.dto.response.PlaceBasicResponseDto.LiveOpeningInfoDto;
import com.jigumulmi.place.dto.response.PlaceBasicResponseDto.LiveOpeningInfoDto.NextOpeningInfo;
import com.jigumulmi.place.dto.response.PlaceCategoryDto;
import com.jigumulmi.place.dto.response.ReviewImageResponseDto;
import com.jigumulmi.place.dto.response.ReviewReplyResponseDto;
import com.jigumulmi.place.dto.response.ReviewResponseDto;
import com.jigumulmi.place.dto.response.ReviewStatisticsResponseDto;
import com.jigumulmi.place.dto.response.S3DeletePresignedUrlResponseDto;
import com.jigumulmi.place.dto.response.S3PutPresignedUrlResponseDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import com.jigumulmi.place.dto.response.SurroundingDateBusinessHour;
import com.jigumulmi.place.repository.CustomPlaceRepository;
import com.jigumulmi.place.repository.MenuRepository;
import com.jigumulmi.place.repository.PlaceCategoryMappingRepository;
import com.jigumulmi.place.repository.PlaceImageRepository;
import com.jigumulmi.place.repository.PlaceLikeRepository;
import com.jigumulmi.place.repository.PlaceRepository;
import com.jigumulmi.place.repository.ReviewImageRepository;
import com.jigumulmi.place.repository.ReviewReplyRepository;
import com.jigumulmi.place.repository.ReviewRepository;
import com.jigumulmi.place.repository.SubwayStationRepository;
import com.jigumulmi.place.vo.CurrentOpeningStatus;
import com.jigumulmi.place.vo.NextOpeningStatus;
import com.jigumulmi.place.vo.PlaceCategory;
import com.jigumulmi.place.vo.PlaceCategoryGroup;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;

@Service
@RequiredArgsConstructor
public class PlaceService {

    public final static String REVIEW_IMAGE_S3_PREFIX = "reviewImage/";
    public final static String MENU_IMAGE_S3_PREFIX = "menuImage/";

    private final S3Service s3Service;

    private final SubwayStationRepository subwayStationRepository;
    private final PlaceRepository placeRepository;
    private final MenuRepository menuRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewReplyRepository reviewReplyRepository;
    private final CustomPlaceRepository customPlaceRepository;
    private final PlaceImageRepository placeImageRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final PlaceLikeRepository placeLikeRepository;
    private final PlaceCategoryMappingRepository placeCategoryMappingRepository;

    public List<SubwayStationResponseDto> getSubwayStationList(String stationName) {
        return subwayStationRepository.findAllByStationNameStartsWith(stationName)
            .stream().map(SubwayStationResponseDto::fromMainStation).toList();
    }

    @Transactional
    public void registerPlace(CreatePlaceRequestDto requestDto, Member member) {

        Place newPlace = Place.builder()
            .name(requestDto.getName())
            .registrantComment(requestDto.getRegistrantComment())
            .isApproved(false)
            .member(member)
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

        newPlace.addCategoryAndSubwayStation(new ArrayList<>(),
            Collections.singletonList(subwayStationPlace));
        newPlace.addMenu(menuList);

        placeRepository.save(newPlace);
    }

    @Transactional(readOnly = true)
    public PlaceBasicResponseDto getPlaceBasic(Long placeId) {
        PlaceBasicResponseDto place = customPlaceRepository.getPlaceById(placeId);

        List<PlaceCategoryDto> categoryDtoList = placeCategoryMappingRepository.findByPlace_Id(
                placeId)
            .stream()
            .map(PlaceCategoryDto::fromPlaceCategoryMapping).toList();
        place.setCategoryList(categoryDtoList);

        List<ImageDto> imageList = placeImageRepository.findByPlace_Id(placeId).stream()
            .map(ImageDto::from).toList();
        place.setImageList(imageList);

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        WeeklyBusinessHourDto weeklyBusinessHourDto = customPlaceRepository.getWeeklyBusinessHourByPlaceId(
            placeId, today);

        DayOfWeek todayDayOfWeek = today.getDayOfWeek();
        DayOfWeek yesterdayDayOfWeek = today.minusDays(1).getDayOfWeek();
        BusinessHour todayBusinessHour = weeklyBusinessHourDto.getBusinessHour(todayDayOfWeek);
        SurroundingDateBusinessHour surroundingDateBusinessHour = SurroundingDateBusinessHour.builder()
            .today(todayBusinessHour)
            .yesterday(weeklyBusinessHourDto.getBusinessHour(yesterdayDayOfWeek))
            .build();

        LocalTime currentTime = now.toLocalTime();
        CurrentOpeningStatus currentOpeningStatus = CurrentOpeningStatus.getLiveOpeningStatus(
            surroundingDateBusinessHour, currentTime);
        NextOpeningInfo nextOpeningInfo = NextOpeningStatus.getNextOpeningInfo(
            surroundingDateBusinessHour, currentTime);

        LiveOpeningInfoDto liveOpeningInfoDto = LiveOpeningInfoDto.builder()
            .currentOpeningStatus(currentOpeningStatus)
            .nextOpeningInfo(nextOpeningInfo)
            .weeklyBusinessHour(weeklyBusinessHourDto)
            .build();
        place.setLiveOpeningInfo(liveOpeningInfoDto);

        return place;
    }

    public PagedResponseDto<MenuDto> getPlaceMenu(Pageable pageable, Long placeId) {
        Page<MenuDto> menuPage = menuRepository.findAllByPlaceId(placeId, pageable)
            .map(MenuDto::from);
        return PagedResponseDto.of(menuPage, pageable);
    }

    public ReviewStatisticsResponseDto getReviewStatistics(Long placeId) {
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

        return ReviewStatisticsResponseDto.builder()
            .averageRating(averageRating)
            .totalCount(totalCount)
            .statistics(reviewRatingStatMap)
            .build();
    }

    @Transactional
    public void postReview(Long placeId, CreateReviewRequestDto requestDto, Member member) {
        boolean canPostReview = reviewRepository.findTopByPlaceIdAndMemberIdAndDeletedAtIsNull(
            placeId, member.getId()
        ).isEmpty();

        if (canPostReview) {
            Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));
            Review review = Review.builder()
                .place(place)
                .content(requestDto.getContent())
                .rating(requestDto.getRating())
                .member(member)
                .build();

            ArrayList<String> s3KeyList = new ArrayList<>();
            try {
                for (MultipartFile image : requestDto.getImageList()) {
                    String s3Key = REVIEW_IMAGE_S3_PREFIX + placeId + "/"
                        + FileUtils.generateUniqueFilename(image);

                    s3KeyList.add(s3Key);

                    s3Service.putObject(s3Service.bucket, s3Key, image);
                }
            } catch (SdkException | IOException e) {
                throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
            }

            List<ReviewImage> reviewImageList = new ArrayList<>();
            for (String s3Key : s3KeyList) {
                reviewImageList.add(
                    ReviewImage.builder()
                        .s3Key(s3Key)
                        .review(review)
                        .build()
                );
            }

            review.addReviewImageList(reviewImageList);

            reviewRepository.save(review);
        } else {
            throw new CustomException(PlaceErrorCode.DUPLICATE_REVIEW);
        }
    }

    public void postReviewReply(Long reviewId, CreateReviewReplyRequestDto requestDto,
        Member member) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));
        ReviewReply reviewReply = ReviewReply.builder()
            .review(review)
            .content(requestDto.getContent())
            .member(member)
            .build();

        reviewReplyRepository.save(reviewReply);
    }

    @Transactional(readOnly = true)
    public PagedResponseDto<ReviewResponseDto> getReviewList(Member requestMember,
        Pageable pageable, Long placeId) {
        Page<ReviewResponseDto> reviewList = customPlaceRepository.getReviewListByPlaceId(
            placeId, pageable).map(review -> ReviewResponseDto.from(review, requestMember));

        Map<Long, Long> reviewReplyCount = customPlaceRepository.getReviewReplyCount(placeId);

        for (ReviewResponseDto reviewDto : reviewList) {
            Long count = reviewReplyCount.getOrDefault(reviewDto.getId(), 0L);
            reviewDto.setReplyCount(count);
        }

        return PagedResponseDto.of(reviewList, pageable);
    }


    public List<ReviewReplyResponseDto> getReviewReplyList(Member member, Long reviewId) {
        return customPlaceRepository.getReviewReplyListByReviewId(member, reviewId);
    }

    @Transactional
    public void updateReview(Long reviewId, UpdateReviewRequestDto requestDto, Member member) {
        Review review = reviewRepository.findByIdAndMember(reviewId, member);
        Long placeId = review.getPlace().getId();

        ArrayList<String> s3KeyList = new ArrayList<>();
        try {
            for (MultipartFile image : requestDto.getNewImageList()) {
                String s3Key =
                    REVIEW_IMAGE_S3_PREFIX + placeId + "/"
                        + FileUtils.generateUniqueFilename(image);

                s3KeyList.add(s3Key);

                s3Service.putObject(s3Service.bucket, s3Key, image);
            }
        } catch (SdkException | IOException e) {
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

        List<ReviewImage> newReviewImageList = new ArrayList<>();
        for (String s3Key : s3KeyList) {
            newReviewImageList.add(
                ReviewImage.builder()
                    .s3Key(s3Key)
                    .review(review)
                    .build()
            );
        }

        List<ReviewImage> currentImageList = review.getReviewImageList();
        Set<Long> trashImageIdSet = new HashSet<>(requestDto.getTrashImageIdList());
        List<ReviewImage> trashReviewImageList = currentImageList.stream()
            .filter(image -> trashImageIdSet.contains(image.getId()))
            .toList();

        review.updateReview(requestDto.getRating(), requestDto.getContent(), newReviewImageList,
            trashReviewImageList);

        reviewRepository.save(review);

        try {
            List<ObjectIdentifier> objectIdentifierList = trashReviewImageList.stream().map(
                i -> ObjectIdentifier.builder().key(i.getS3Key()).build()
            ).toList();

            s3Service.deleteObjects(s3Service.bucket, objectIdentifierList);
        } catch (SdkException e) {
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void updateReviewReply(Long replyId, UpdateReviewReplyRequestDto requestDto,
        Member member) {
        ReviewReply reviewReply = reviewReplyRepository.findByIdAndMember(replyId, member);
        reviewReply.updateReviewReply(requestDto.getContent());
        reviewReplyRepository.save(reviewReply);
    }

    @Transactional
    public void deleteReview(Long reviewId, Member member) {
        Review review = reviewRepository.findByIdAndMember(reviewId, member);
        List<ReviewImage> reviewImageList = review.getReviewImageList();

        try {
            List<ObjectIdentifier> objectIdentifierList = reviewImageList.stream().map(
                i -> ObjectIdentifier.builder().key(i.getS3Key()).build()
            ).toList();

            s3Service.deleteObjects(s3Service.bucket, objectIdentifierList);
        } catch (SdkException e) {
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

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

    public List<PlaceCategoryGroup> getPlaceCategoryGroupList() {
        return Arrays.stream(PlaceCategoryGroup.values()).toList();
    }

    public List<PlaceCategory> getPlaceCategoryList(PlaceCategoryGroup placeCategoryGroup) {
        return placeCategoryGroup.getPlaceCategoryList();
    }

    public void togglePlaceLike(Long placeId, Boolean toggleOn, Member member) {

        if (toggleOn) {
            Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));
            PlaceLike placeLike = PlaceLike.builder()
                .member(member)
                .place(place)
                .build();

            placeLikeRepository.save(placeLike);
        } else {
            placeLikeRepository.deleteByPlace_IdAndMember(placeId, member);
        }
    }

    public S3PutPresignedUrlResponseDto createMenuImageS3PutPresignedUrl(
        MenuImageS3PutPresignedUrlRequestDto requestDto) {
        String filename = UUID.randomUUID().toString();
        String s3Key = MENU_IMAGE_S3_PREFIX + filename + "." + requestDto.getFileExtension();

        String url = s3Service.generatePutObjectPresignedUrl(s3Service.bucket, s3Key);
        return S3PutPresignedUrlResponseDto.builder()
            .url(url)
            .filename(filename)
            .build();
    }

    public S3DeletePresignedUrlResponseDto createMenuImageS3DeletePresignedUrl(
        MenuImageS3DeletePresignedUrlRequestDto requestDto) {
        String url = s3Service.generateDeleteObjectPresignedUrl(s3Service.bucket,
            requestDto.getS3Key());
        return S3DeletePresignedUrlResponseDto.builder().url(url).build();
    }

    public PagedResponseDto<ReviewImageResponseDto> getReviewImage(Pageable pageable,
        Long placeId) {
        Page<ReviewImageResponseDto> imagePage = reviewImageRepository.findAllByReview_Place_IdOrderByCreatedAtDesc(
            placeId, pageable).map(ReviewImageResponseDto::from);
        return PagedResponseDto.of(imagePage, pageable);
    }
}
