package com.jigumulmi.place.domain;

import com.jigumulmi.config.common.Timestamped;
import jakarta.persistence.*;
import lombok.*;

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

    @ElementCollection
    private List<String> menuList;

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

    private Boolean isApproved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subway_station_id")
    private SubwayStation subwayStation;

    @Builder
    public Restaurant(String name, String category, String address, String contact, List<String> menuList, String openingHourSun, String openingHourMon, String openingHourTue, String openingHourWed, String openingHourThu, String openingHourFri, String openingHourSat, String additionalInfo, String mainImageUrl, String placeUrl, Double longitude, Double latitude, String registrantComment, Boolean isApproved, SubwayStation subwayStation) {
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
    }
}
