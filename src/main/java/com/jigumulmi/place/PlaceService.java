package com.jigumulmi.place;

import static java.lang.Math.round;

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
import com.jigumulmi.place.dto.response.PlaceResponseDto.CategoryDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto.ImageDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto.SurroundingDateOpeningHour;
import com.jigumulmi.place.dto.response.ReviewImageResponseDto;
import com.jigumulmi.place.dto.response.ReviewReplyResponseDto;
import com.jigumulmi.place.dto.response.ReviewResponseDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto.SubwayStationLineDto;
import com.jigumulmi.place.repository.CustomPlaceRepository;
import com.jigumulmi.place.repository.MenuRepository;
import com.jigumulmi.place.repository.PlaceImageRepository;
import com.jigumulmi.place.repository.PlaceLikeRepository;
import com.jigumulmi.place.repository.PlaceRepository;
import com.jigumulmi.place.repository.ReviewImageRepository;
import com.jigumulmi.place.repository.ReviewReplyRepository;
import com.jigumulmi.place.repository.ReviewRepository;
import com.jigumulmi.place.repository.SubwayStationRepository;
import com.jigumulmi.place.vo.CurrentOpeningInfo;
import com.jigumulmi.place.vo.PlaceCategory;
import com.jigumulmi.place.vo.PlaceCategoryGroup;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class PlaceService {


    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final S3Client s3Client;

    private final SubwayStationRepository subwayStationRepository;
    private final PlaceRepository placeRepository;
    private final MenuRepository menuRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewReplyRepository reviewReplyRepository;
    private final CustomPlaceRepository customPlaceRepository;
    private final PlaceImageRepository placeImageRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final PlaceLikeRepository placeLikeRepository;

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

        newPlace.addChildren(new ArrayList<>(), Collections.singletonList(subwayStationPlace),
            menuList, new ArrayList<>());

        placeRepository.save(newPlace);
    }

    public List<PlaceResponseDto> getPlaceList(GetPlaceListRequestDto requestDto) {
        List<PlaceResponseDto> placeList = customPlaceRepository.getPlaceList(requestDto);
        for (PlaceResponseDto responseDto : placeList) {
            List<ImageDto> imageList = responseDto.getImageList();
            responseDto.setImageList(Collections.singletonList(imageList.getFirst()));

            List<CategoryDto> distinctCategoryList = responseDto.getCategoryList().stream()
                .distinct().toList();
            responseDto.setCategoryList(distinctCategoryList);

            SubwayStationResponseDto subwayStation = responseDto.getSubwayStation();
            List<SubwayStationLineDto> distinctLineList = subwayStation.getSubwayStationLineList()
                .stream()
                .distinct().toList();
            subwayStation.setSubwayStationLineList(distinctLineList);
            responseDto.setSubwayStation(subwayStation);

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

        List<CategoryDto> distinctCategoryList = place.getCategoryList().stream().distinct()
            .toList();

        SubwayStationResponseDto subwayStation = place.getSubwayStation();
        List<SubwayStationLineDto> distinctLineList = subwayStation.getSubwayStationLineList()
            .stream()
            .distinct().toList();
        subwayStation.setSubwayStationLineList(distinctLineList);

        List<MenuDto> menuList = menuRepository.findAllByPlaceId(placeId).stream()
            .map(MenuDto::from).toList();

        List<ImageDto> imageList = placeImageRepository.findByPlace_Id(placeId).stream()
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

        SurroundingDateOpeningHour surroundingDateOpeningHour = place.getSurroundingDateOpeningHour();
        String currentOpeningInfo = CurrentOpeningInfo.getCurrentOpeningInfo(
            surroundingDateOpeningHour);

        List<ReviewImageResponseDto> reviewImageList = reviewImageRepository.findAllByReview_Place_IdOrderByCreatedAtDesc(
                placeId)
            .stream().map(ReviewImageResponseDto::from).toList();

        Long likeCount = customPlaceRepository.getPlaceLikeCount(placeId);

        return PlaceDetailResponseDto.builder()
            .id(place.getId())
            .name(place.getName())
            .imageList(imageList)
            .position(
                place.getPosition()
            )
            .subwayStation(subwayStation)
            .categoryList(distinctCategoryList)
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
            .reviewImageList(reviewImageList)
            .showLikeCount(likeCount != 0)
            .likeCount(likeCount)
            .build();
    }

    @Transactional
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
                .build();

            ArrayList<String> s3KeyList = new ArrayList<>();
            try {
                for (MultipartFile image : requestDto.getImageList()) {
                    String fileExtension = StringUtils.getFilenameExtension(
                        image.getOriginalFilename());
                    String s3Key =
                        "reviewImage/" + requestDto.getPlaceId() + "/" + UUID.randomUUID() + "."
                            + fileExtension;

                    s3KeyList.add(s3Key);

                    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(s3Key)
                        .contentType(image.getContentType())
                        .contentLength(image.getSize())
                        .build();

                    s3Client.putObject(putObjectRequest,
                        RequestBody.fromInputStream(image.getInputStream(), image.getSize())
                    );
                }
            } catch (SdkException | IOException e) {
                throw new RuntimeException(e);
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
        List<ReviewResponseDto> reviewList = customPlaceRepository.getReviewListByPlaceId(
            placeId, member.getId());

        Map<Long, Long> reviewReplyCount = customPlaceRepository.getReviewReplyCount(placeId);

        for (ReviewResponseDto reviewDto : reviewList) {
            Long count = reviewReplyCount.getOrDefault(reviewDto.getId(), 0L);
            reviewDto.setReplyCount(count);
        }

        return reviewList;
    }


    public List<ReviewReplyResponseDto> getReviewReplyList(Member member, Long reviewId) {
        return customPlaceRepository.getReviewReplyListByReviewId(member.getId(), reviewId);
    }

    @Transactional
    public void updateReview(UpdateReviewRequestDto requestDto, Member member) {
        Review review = reviewRepository.findByIdAndMember(requestDto.getReviewId(), member);
        Long placeId = review.getPlace().getId();

        ArrayList<String> s3KeyList = new ArrayList<>();
        try {
            for (MultipartFile image : requestDto.getNewImageList()) {
                String fileExtension = StringUtils.getFilenameExtension(
                    image.getOriginalFilename());
                String s3Key =
                    "reviewImage/" + placeId + "/" + UUID.randomUUID() + "."
                        + fileExtension;

                s3KeyList.add(s3Key);

                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .contentType(image.getContentType())
                    .contentLength(image.getSize())
                    .build();

                s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(image.getInputStream(), image.getSize())
                );
            }
        } catch (SdkException | IOException e) {
            throw new RuntimeException(e);
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
            DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                .bucket(bucket)
                .delete(Delete.builder().objects(objectIdentifierList).build())
                .build();

            s3Client.deleteObjects(deleteObjectsRequest);
        } catch (SdkException e) {
            System.out.println("S3 DeleteObjects Error: " + e.getMessage());
        }
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
        List<ReviewImage> reviewImageList = review.getReviewImageList();

        try {
            List<ObjectIdentifier> objectIdentifierList = reviewImageList.stream().map(
                i -> ObjectIdentifier.builder().key(i.getS3Key()).build()
            ).toList();
            DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                .bucket(bucket)
                .delete(Delete.builder().objects(objectIdentifierList).build())
                .build();

            s3Client.deleteObjects(deleteObjectsRequest);
        } catch (SdkException e) {
            System.out.println("S3 DeleteObjects Error: " + e.getMessage());
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
        Place place = placeRepository.findById(placeId)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));

        if (toggleOn) {
            PlaceLike placeLike = PlaceLike.builder()
                .member(member)
                .place(place)
                .build();

            placeLikeRepository.save(placeLike);
        } else {
            placeLikeRepository.deleteByPlaceAndMember(place, member);
        }
    }
}
