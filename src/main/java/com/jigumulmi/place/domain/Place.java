package com.jigumulmi.place.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jigumulmi.admin.dto.request.AdminUpdatePlaceRequestDto;
import com.jigumulmi.config.common.Timestamped;
import com.jigumulmi.place.PlaceCategoryConverter;
import com.jigumulmi.place.dto.response.PlaceDetailResponseDto.OpeningHourDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto.PositionDto;
import com.jigumulmi.place.vo.PlaceCategory;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
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

    private PlaceCategory category;

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

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isFromAdmin;

    @Builder
    public Place(String name, PlaceCategory category, String address, String contact, List<Menu> menuList,
        String openingHourSun, String openingHourMon, String openingHourTue, String openingHourWed,
        String openingHourThu, String openingHourFri, String openingHourSat, String additionalInfo,
        String placeUrl, Double longitude, Double latitude, String registrantComment,
        Boolean isApproved, List<Review> reviewList,
        List<SubwayStationPlace> subwayStationPlaceList,
        List<PlaceImage> placeImageList, String kakaoPlaceId,
        Boolean isFromAdmin
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
}
