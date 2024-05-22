package com.jigumulmi.place.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubwayStationPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subway_station_id")
    @JsonBackReference
    private SubwayStation subwayStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    @JsonBackReference
    private Restaurant restaurant;

    private Boolean isMain;

    @Builder
    public SubwayStationPlace(SubwayStation subwayStation, Restaurant restaurant, Boolean isMain) {
        this.subwayStation = subwayStation;
        this.restaurant = restaurant;
        this.isMain = isMain;
    }
}
