package com.jigumulmi.place;

import com.jigumulmi.admin.place.dto.WeeklyBusinessHourDto;
import com.jigumulmi.aws.S3Manager;
import com.jigumulmi.banner.dto.response.BannerPlaceListResponseDto;
import com.jigumulmi.banner.dto.response.BannerPlaceListResponseDto.BannerPlaceDto;
import com.jigumulmi.common.FileUtils;
import com.jigumulmi.common.PagedResponseDto;
import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import com.jigumulmi.config.exception.errorCode.PlaceErrorCode;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.domain.Review;
import com.jigumulmi.place.domain.ReviewImage;
import com.jigumulmi.place.domain.ReviewReply;
import com.jigumulmi.place.dto.BusinessHour;
import com.jigumulmi.place.dto.ImageDto;
import com.jigumulmi.place.dto.MenuDto;
import com.jigumulmi.place.dto.repository.BusinessHourQueryDto;
import com.jigumulmi.place.dto.request.CreateReviewReplyRequestDto;
import com.jigumulmi.place.dto.request.CreateReviewRequestDto;
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
import com.jigumulmi.place.repository.PlaceRepository;
import com.jigumulmi.place.repository.ReviewImageRepository;
import com.jigumulmi.place.repository.ReviewReplyRepository;
import com.jigumulmi.place.repository.ReviewRepository;
import com.jigumulmi.place.repository.SubwayStationRepository;
import com.jigumulmi.place.vo.CurrentOpeningStatus;
import com.jigumulmi.place.vo.NextOpeningStatus;
import jakarta.validation.constraints.NotBlank;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;

@Component
@RequiredArgsConstructor
public class PlaceManager {

    private final S3Manager s3Manager;

    private final SubwayStationRepository subwayStationRepository;
    private final PlaceRepository placeRepository;
    private final MenuRepository menuRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewReplyRepository reviewReplyRepository;
    private final CustomPlaceRepository customPlaceRepository;
    private final PlaceImageRepository placeImageRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final PlaceCategoryMappingRepository placeCategoryMappingRepository;

    public List<SubwayStationResponseDto> getSubwayStationListByName(String stationName) {
        return subwayStationRepository.findAllByStationNameStartsWith(stationName)
            .stream().map(SubwayStationResponseDto::fromMainStation).toList();
    }

    @Transactional(readOnly = true)
    public PagedResponseDto<BannerPlaceDto> getMappedPlaceList(Pageable pageable, Long bannerId) {
        Page<BannerPlaceDto> placePage = customPlaceRepository.getAllMappedPlaceByBannerId(
            pageable,
            bannerId).map(BannerPlaceDto::from);

        List<BannerPlaceDto> placeDtoList = placePage.getContent();

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        List<Long> placeIdList = placeDtoList.stream().map(BannerPlaceDto::getId).toList();
        List<BusinessHourQueryDto> businessHourQueryDtoList = customPlaceRepository.getSurroundingBusinessHourByPlaceIdIn(
            placeIdList, today);

        Map<Long, SurroundingDateBusinessHour> surroundingBusinessHourMap = new HashMap<>();
        for (BusinessHourQueryDto businessHourQueryDto : businessHourQueryDtoList) {
            Long placeId = businessHourQueryDto.getPlaceId();
            SurroundingDateBusinessHour surroundingDateBusinessHour = surroundingBusinessHourMap.getOrDefault(
                placeId, SurroundingDateBusinessHour.builder().build());

            DayOfWeek dayOfWeek = businessHourQueryDto.getDayOfWeek();
            BusinessHour businessHour = buildBusinessHour(businessHourQueryDto);
            if (Objects.equals(dayOfWeek, today.getDayOfWeek())) {
                surroundingDateBusinessHour.setToday(businessHour);
            } else {
                surroundingDateBusinessHour.setYesterday(businessHour);
            }
            surroundingBusinessHourMap.put(placeId, surroundingDateBusinessHour);
        }

        LocalTime currentTime = now.toLocalTime();
        placeDtoList.forEach(
            place -> place.setCurrentOpeningStatus(surroundingBusinessHourMap.get(place.getId()),
                currentTime));

        return BannerPlaceListResponseDto.of(placePage, pageable);
    }

    public BusinessHour buildBusinessHour(BusinessHourQueryDto businessHourQueryDto) {
        BusinessHour temporaryBusinessHour = businessHourQueryDto.getTemporaryBusinessHour();
        if ((temporaryBusinessHour.getOpenTime() != null
            && temporaryBusinessHour.getCloseTime() != null)
            || temporaryBusinessHour.getIsDayOff() != null) {
            return BusinessHour.builder()
                .openTime(temporaryBusinessHour.getOpenTime())
                .closeTime(temporaryBusinessHour.getCloseTime())
                .breakStart(temporaryBusinessHour.getBreakStart())
                .breakEnd(temporaryBusinessHour.getBreakEnd())
                .isDayOff(temporaryBusinessHour.getIsDayOff())
                .build();
        } else {
            BusinessHour fixedBusinessHour = businessHourQueryDto.getFixedBusinessHour();
            return BusinessHour.builder()
                .openTime(fixedBusinessHour.getOpenTime())
                .closeTime(fixedBusinessHour.getCloseTime())
                .breakStart(fixedBusinessHour.getBreakStart())
                .breakEnd(fixedBusinessHour.getBreakEnd())
                .isDayOff(fixedBusinessHour.getIsDayOff())
                .build();
        }
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
        List<BusinessHourQueryDto> businessHourQueryDtoList = customPlaceRepository.getWeeklyBusinessHourByPlaceId(
            placeId, today);

        WeeklyBusinessHourDto weeklyBusinessHourDto = new WeeklyBusinessHourDto();
        for (BusinessHourQueryDto businessHourQueryDto : businessHourQueryDtoList) {
            DayOfWeek dayOfWeek = businessHourQueryDto.getDayOfWeek();
            BusinessHour businessHour = buildBusinessHour(businessHourQueryDto);

            weeklyBusinessHourDto.updateBusinessHour(Objects.requireNonNull(dayOfWeek), businessHour);
        }

        DayOfWeek todayDayOfWeek = today.getDayOfWeek();
        DayOfWeek yesterdayDayOfWeek = today.minusDays(1).getDayOfWeek();
        SurroundingDateBusinessHour surroundingDateBusinessHour = SurroundingDateBusinessHour.builder()
            .today(weeklyBusinessHourDto.getBusinessHour(todayDayOfWeek))
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

        return ReviewStatisticsResponseDto.fromReviewRatingStatMap(reviewRatingStatMap);
    }

    public PagedResponseDto<ReviewImageResponseDto> getReviewImage(Pageable pageable,
        Long placeId) {
        Page<ReviewImageResponseDto> imagePage = reviewImageRepository.findAllByReview_Place_IdOrderByCreatedAtDesc(
            placeId, pageable).map(ReviewImageResponseDto::from);
        return PagedResponseDto.of(imagePage, pageable);
    }

    public void checkActiveReview(Long placeId, Member member) {
        boolean canPostReview = reviewRepository.findTopByPlaceIdAndMemberIdAndDeletedAtIsNull(
            placeId, member.getId()
        ).isEmpty();

        if (!canPostReview) {
            throw new CustomException(PlaceErrorCode.DUPLICATE_REVIEW);
        }
    }

    @Transactional
    public void createReview(Long placeId, CreateReviewRequestDto requestDto, Member member,
        List<String> s3KeyList) {
        Place place = placeRepository.findById(placeId)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));
        Review review = Review.builder()
            .place(place)
            .content(requestDto.getContent())
            .rating(requestDto.getRating())
            .member(member)
            .build();

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
    }

    public List<String> saveReviewImageFileList(Long placeId, List<MultipartFile> imageList) {
        List<String> s3KeyList = new ArrayList<>();
        try {
            for (MultipartFile image : imageList) {
                String s3Key = S3Manager.REVIEW_IMAGE_S3_PREFIX + placeId + "/"
                    + FileUtils.generateUniqueFilename();

                s3Manager.putObject(s3Manager.bucket, s3Key, image);

                s3KeyList.add(s3Key);
            }
        } catch (SdkException | IOException e) {
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

        return s3KeyList;
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

    public Review getReview(Long reviewId, Member member) {
        return reviewRepository.findByIdAndMember(reviewId, member);
    }

    @Transactional
    public List<String> updateReview(Review review, UpdateReviewRequestDto requestDto,
        List<String> newS3KeyList) {

        List<ReviewImage> newReviewImageList = new ArrayList<>();
        for (String s3Key : newS3KeyList) {
            newReviewImageList.add(
                ReviewImage.builder()
                    .s3Key(s3Key)
                    .review(review)
                    .build()
            );
        }

        List<ReviewImage> currentImageList = review.getReviewImageList();
        List<ReviewImage> trashReviewImageList = currentImageList.stream()
            .filter(image -> requestDto.getTrashImageIdList().contains(image.getId()))
            .toList();

        review.updateReview(requestDto.getRating(), requestDto.getContent(), newReviewImageList,
            trashReviewImageList);

        reviewRepository.save(review);

        return trashReviewImageList.stream().map(ReviewImage::getS3Key).toList();
    }

    public void deleteReviewImageFileList(List<String> trashS3KeyList) {
        try {
            List<ObjectIdentifier> objectIdentifierList = trashS3KeyList.stream().map(
                key -> ObjectIdentifier.builder().key(key).build()
            ).toList();

            s3Manager.deleteObjects(s3Manager.bucket, objectIdentifierList);
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
    public List<String> softOrHardDeleteReview(Long reviewId, Member member) {
        Review review = getReview(reviewId, member);
        List<ReviewImage> reviewImageList = review.getReviewImageList();

        boolean shouldSoftDelete = reviewReplyRepository.existsByReview(review);
        if (shouldSoftDelete) {
            review.deleteReviewWithReplies();
            reviewRepository.save(review);
        } else {
            reviewRepository.delete(review);
        }

        return reviewImageList.stream().map(ReviewImage::getS3Key).toList();
    }

    public ReviewReply deleteReviewReply(Long reviewReplyId, Member member) {
        ReviewReply reviewReply = reviewReplyRepository.findByIdAndMember(reviewReplyId, member);
        reviewReplyRepository.delete(reviewReply);

        return reviewReply;
    }

    public void hardDeleteReviewIfNeeded(ReviewReply reviewReply) {
        Review review = reviewReply.getReview();
        long reviewReplyCount = reviewReplyRepository.countByReview(review);
        if (review.getDeletedAt() != null && reviewReplyCount == 1) {
            reviewRepository.delete(review);
        }
    }

    public S3PutPresignedUrlResponseDto createMenuImageS3PutPresignedUrl(long placeId) {
        String filename = FileUtils.generateUniqueFilename();
        String s3Key = S3Manager.MENU_IMAGE_S3_PREFIX + placeId + "/" + filename;

        String url = s3Manager.generatePutObjectPresignedUrl(s3Manager.bucket, s3Key);
        return S3PutPresignedUrlResponseDto.builder()
            .url(url)
            .filename(filename)
            .build();
    }

    public S3DeletePresignedUrlResponseDto createMenuImageS3DeletePresignedUrl(
        @NotBlank String s3Key) {
        String url = s3Manager.generateDeleteObjectPresignedUrl(s3Manager.bucket, s3Key);
        return S3DeletePresignedUrlResponseDto.builder()
            .url(url)
            .build();
    }
}
