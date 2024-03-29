package com.jigumulmi.place.domain;

import com.jigumulmi.config.common.Timestamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Restaurant extends Timestamped {

    @Id
    @GeneratedValue
    @Column
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

    @ManyToOne
    @JoinColumn(name = "subway_station_id")
    private SubwayStation subwayStation;
}
