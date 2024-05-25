package com.jigumulmi.place.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jigumulmi.admin.dto.request.AdminUpdatePlaceRequestDto;
import com.jigumulmi.config.common.Timestamped;
import com.jigumulmi.place.dto.response.RestaurantDetailResponseDto.OpeningHourDto;
import com.jigumulmi.place.dto.response.RestaurantResponseDto.PositionDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Restaurant extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String category;

    private String address; // 도로명 주소

    private String contact;

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "restaurant")
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

    private String mainImageUrl;

    private String placeUrl;

    private Double longitude;

    private Double latitude;

    private String registrantComment;

    @ColumnDefault("false")
    private Boolean isApproved;


    @OneToMany(mappedBy = "restaurant")
    @JsonManagedReference
    private List<Review> reviewList = new ArrayList<>();

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "restaurant")
    @OrderBy("isMain DESC")
    @JsonManagedReference
    private List<SubwayStationPlace> subwayStationPlaceList = new ArrayList<>();

    @Builder
    public Restaurant(String name, String category, String address, String contact,
        List<Menu> menuList, String openingHourSun, String openingHourMon, String openingHourTue,
        String openingHourWed, String openingHourThu, String openingHourFri, String openingHourSat,
        String additionalInfo, String mainImageUrl, String placeUrl, Double longitude,
        Double latitude, String registrantComment, Boolean isApproved,
        List<SubwayStationPlace> subwayStationPlaceList,
        List<Review> reviewList) {
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
        this.mainImageUrl = mainImageUrl;
        this.placeUrl = placeUrl;
        this.longitude = longitude;
        this.latitude = latitude;
        this.registrantComment = registrantComment;
        this.isApproved = isApproved;
        this.subwayStationPlaceList = subwayStationPlaceList;
        this.reviewList = reviewList;
    }

    public void adminUpdate(AdminUpdatePlaceRequestDto requestDto,
        List<SubwayStationPlace> subwayStationPlaceList,
        List<Menu> menuList) {
        OpeningHourDto openingHour = requestDto.getOpeningHour();
        PositionDto position = requestDto.getPosition();

        this.name = requestDto.getName();
        this.category = requestDto.getCategory();
        this.address = requestDto.getAddress();
        this.contact = requestDto.getContact();
        this.menuList = menuList;
        this.openingHourSun = openingHour.getOpeningHourSun();
        this.openingHourMon = openingHour.getOpeningHourMon();
        this.openingHourTue = openingHour.getOpeningHourTue();
        this.openingHourWed = openingHour.getOpeningHourWed();
        this.openingHourThu = openingHour.getOpeningHourThu();
        this.openingHourFri = openingHour.getOpeningHourFri();
        this.openingHourSat = openingHour.getOpeningHourSat();
        this.additionalInfo = requestDto.getAdditionalInfo();
        this.mainImageUrl = requestDto.getMainImageUrl();
        this.placeUrl = requestDto.getPlaceUrl();
        this.longitude = position.getLongitude();
        this.latitude = position.getLatitude();
        this.registrantComment = requestDto.getRegistrantComment();
        this.isApproved = requestDto.getIsApproved();
        this.subwayStationPlaceList = subwayStationPlaceList;
    }
}
