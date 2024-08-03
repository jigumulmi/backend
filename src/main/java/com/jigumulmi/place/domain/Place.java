package com.jigumulmi.place.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jigumulmi.admin.dto.request.AdminUpdatePlaceRequestDto;
import com.jigumulmi.admin.dto.response.GooglePlaceApiResponseDto;
import com.jigumulmi.admin.dto.response.GooglePlaceApiResponseDto.RegularOpeningHours;
import com.jigumulmi.admin.dto.response.GooglePlaceApiResponseDto.RegularOpeningHours.Period;
import com.jigumulmi.admin.dto.response.KakaoPlaceApiResponseDto;
import com.jigumulmi.admin.dto.response.KakaoPlaceApiResponseDto.Document;
import com.jigumulmi.config.common.Timestamped;
import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.AdminErrorCode;
import com.jigumulmi.place.dto.response.PlaceDetailResponseDto.OpeningHourDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto.PositionDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(columnList = "kakaoPlaceId", unique = true))
public class Place extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String category;

    private String address; // 도로명 주소

    private String contact;

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Menu> menuList = new ArrayList<>();

    private String openingHourSun;

    private String openingHourMon;

    private String openingHourTue;

    private String openingHourWed;

    private String openingHourThu;

    private String openingHourFri;

    private String openingHourSat;

    private String additionalInfo;

    @Column(length = 2083)
    private String placeUrl;

    private Double longitude;

    private Double latitude;

    private String registrantComment;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isApproved;


    @OneToMany(mappedBy = "place")
    @JsonManagedReference
    private List<Review> reviewList = new ArrayList<>();

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("isMain DESC")
    @JsonManagedReference
    private List<SubwayStationPlace> subwayStationPlaceList = new ArrayList<>();

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("isMain DESC")
    @JsonManagedReference
    private List<PlaceImage> placeImageList = new ArrayList<>();

    private String kakaoPlaceId;
    private String googlePlaceId;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isFromAdmin;

    @Builder
    public Place(String name, String category, String address, String contact, List<Menu> menuList,
        String openingHourSun, String openingHourMon, String openingHourTue, String openingHourWed,
        String openingHourThu, String openingHourFri, String openingHourSat, String additionalInfo,
        String placeUrl, Double longitude, Double latitude, String registrantComment,
        Boolean isApproved, List<Review> reviewList,
        List<SubwayStationPlace> subwayStationPlaceList,
        List<PlaceImage> placeImageList, String kakaoPlaceId,
        Boolean isFromAdmin, String googlePlaceId
    ) {
        this.name = name;
        this.category = category;
        this.address = address;
        this.contact = contact;
        this.menuList = menuList;
        this.openingHourSun = openingHourSun;
        this.openingHourMon = openingHourMon;
        this.openingHourTue = openingHourTue;
        this.openingHourWed = openingHourWed;
        this.openingHourThu = openingHourThu;
        this.openingHourFri = openingHourFri;
        this.openingHourSat = openingHourSat;
        this.additionalInfo = additionalInfo;
        this.placeUrl = placeUrl;
        this.longitude = longitude;
        this.latitude = latitude;
        this.registrantComment = registrantComment;
        this.isApproved = (isApproved != null) ? isApproved : false;
        this.reviewList = reviewList;
        this.subwayStationPlaceList = subwayStationPlaceList;
        this.placeImageList = placeImageList;
        this.kakaoPlaceId = kakaoPlaceId;
        this.googlePlaceId = googlePlaceId;
        this.isFromAdmin = (isFromAdmin != null) ? isFromAdmin : false;
    }

    public void addChildren(List<SubwayStationPlace> subwayStationPlaceList,
        List<Menu> menuList, List<PlaceImage> placeImageList) {
        this.menuList = menuList;
        this.subwayStationPlaceList = subwayStationPlaceList;
        this.placeImageList = placeImageList;
    }

    public void adminUpdate(AdminUpdatePlaceRequestDto requestDto,
        List<SubwayStationPlace> subwayStationPlaceList,
        List<Menu> menuList, List<PlaceImage> placeImageList) {
        OpeningHourDto openingHour = requestDto.getOpeningHour();
        PositionDto position = requestDto.getPosition();

        this.name = requestDto.getName();
        this.category = requestDto.getCategory();
        this.address = requestDto.getAddress();
        this.contact = requestDto.getContact();
        this.menuList.clear();
        this.menuList.addAll(menuList);
        this.openingHourSun = openingHour.getOpeningHourSun();
        this.openingHourMon = openingHour.getOpeningHourMon();
        this.openingHourTue = openingHour.getOpeningHourTue();
        this.openingHourWed = openingHour.getOpeningHourWed();
        this.openingHourThu = openingHour.getOpeningHourThu();
        this.openingHourFri = openingHour.getOpeningHourFri();
        this.openingHourSat = openingHour.getOpeningHourSat();
        this.additionalInfo = requestDto.getAdditionalInfo();
        this.placeUrl = requestDto.getPlaceUrl();
        this.longitude = position.getLongitude();
        this.latitude = position.getLatitude();
        this.registrantComment = requestDto.getRegistrantComment();
        this.isApproved = requestDto.getIsApproved();
        this.subwayStationPlaceList.clear();
        this.subwayStationPlaceList.addAll(subwayStationPlaceList);
        this.placeImageList.clear();
        this.placeImageList.addAll(placeImageList);
        this.kakaoPlaceId = requestDto.getKakaoPlaceId();
    }

    public static final String CLOSING_DAY = "정기휴무";

    public void adminSaveBasic(GooglePlaceApiResponseDto googlePlaceApiResponseDto,
        KakaoPlaceApiResponseDto kakaoPlaceApiResponseDto) {

        RegularOpeningHours regularOpeningHours = googlePlaceApiResponseDto.getRegularOpeningHours();
        this.googlePlaceId = googlePlaceApiResponseDto.getId();
        if (regularOpeningHours != null) {
            Map<Integer, String> periodMap = new HashMap<>();
            for (Period period : regularOpeningHours.getPeriods()) {
                Integer day = period.getOpen().getDay();
                periodMap.put(day, Period.makeString(period));
            }

            this.openingHourSun = periodMap.getOrDefault(0, CLOSING_DAY);
            this.openingHourMon = periodMap.getOrDefault(1, CLOSING_DAY);
            this.openingHourTue = periodMap.getOrDefault(2, CLOSING_DAY);
            this.openingHourWed = periodMap.getOrDefault(3, CLOSING_DAY);
            this.openingHourThu = periodMap.getOrDefault(4, CLOSING_DAY);
            this.openingHourFri = periodMap.getOrDefault(5, CLOSING_DAY);
            this.openingHourSat = periodMap.getOrDefault(6, CLOSING_DAY);
        }

        List<Document> documents = kakaoPlaceApiResponseDto.getDocuments();
        if (!documents.isEmpty()) {
            Document document = documents.getFirst();
            String categoryName = document.getCategoryName();
            String[] categoryList = categoryName.split(" > ");
            String finalCategory = categoryList[categoryList.length - 1];

            if (Objects.equals(finalCategory, "제과,베이커리")) {
                finalCategory = "베이커리";
            }

            this.name = document.getPlaceName();
            this.category = finalCategory;
            this.address = document.getRoadAddressName();
            this.contact = document.getPhone();
            this.placeUrl = document.getPlaceUrl();
            this.longitude = Double.valueOf(document.getX());
            this.latitude = Double.valueOf(document.getY());
            this.kakaoPlaceId = document.getId();
        }
    }
}
