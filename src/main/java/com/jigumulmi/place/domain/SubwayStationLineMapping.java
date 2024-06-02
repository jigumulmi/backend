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
public class SubwayStationLineMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subway_station_id")
    @JsonBackReference
    private SubwayStation subwayStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subway_station_line_id")
    @JsonBackReference
    private SubwayStationLine subwayStationLine;


    @Builder
    public SubwayStationLineMapping(SubwayStation subwayStation,
        SubwayStationLine subwayStationLine) {
        this.subwayStation = subwayStation;
        this.subwayStationLine = subwayStationLine;
    }
}
