package com.jigumulmi.place.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jigumulmi.admin.dto.request.AdminUpdatePlaceRequestDto;
import com.jigumulmi.config.common.Timestamped;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"subwayStation"})
public class Restaurant extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String category;

    private String address; // 도로명 주소

    private String contact;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subway_station_id")
    private SubwayStation subwayStation;

    @OneToMany(mappedBy = "restaurant")
    @JsonManagedReference
    private List<Review> reviewList = new ArrayList<>();

    @Builder
    public Restaurant(String name, String category, String address, String contact,
        List<Menu> menuList, String openingHourSun, String openingHourMon, String openingHourTue,
        String openingHourWed, String openingHourThu, String openingHourFri, String openingHourSat,
        String additionalInfo, String mainImageUrl, String placeUrl, Double longitude,
        Double latitude, String registrantComment, Boolean isApproved, SubwayStation subwayStation,
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
        this.subwayStation = subwayStation;
        this.reviewList = reviewList;
    }

    public void adminUpdate(AdminUpdatePlaceRequestDto requestDto, SubwayStation subwayStation,
        List<Menu> menuList) {
        this.name = requestDto.getName();
        this.category = requestDto.getCategory();
        this.address = requestDto.getAddress();
        this.contact = requestDto.getContact();
        this.menuList = menuList;
        this.openingHourSun = requestDto.getOpeningHourSun();
        this.openingHourMon = requestDto.getOpeningHourMon();
        this.openingHourTue = requestDto.getOpeningHourTue();
        this.openingHourWed = requestDto.getOpeningHourWed();
        this.openingHourThu = requestDto.getOpeningHourThu();
        this.openingHourFri = requestDto.getOpeningHourFri();
        this.openingHourSat = requestDto.getOpeningHourSat();
        this.additionalInfo = requestDto.getAdditionalInfo();
        this.mainImageUrl = requestDto.getMainImageUrl();
        this.placeUrl = requestDto.getPlaceUrl();
        this.longitude = requestDto.getLongitude();
        this.latitude = requestDto.getLatitude();
        this.registrantComment = requestDto.getRegistrantComment();
        this.isApproved = requestDto.getIsApproved();
        this.subwayStation = subwayStation;
    }
}
