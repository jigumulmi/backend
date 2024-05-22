package com.jigumulmi.place.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubwayStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stationCode;
    private String externalCode;

    private String stationName;
    private String stationNameEng;
    private String stationNameJpn;
    private String stationNameChn;

    private String lineNumber;

    @OneToMany(mappedBy = "subwayStation")
    @JsonManagedReference
    private List<SubwayStationPlace> subwayStationPlaceList = new ArrayList<>();

    @Builder
    public SubwayStation(String stationCode, String externalCode, String stationName,
        String stationNameEng, String stationNameJpn, String stationNameChn, String lineNumber,
        List<SubwayStationPlace> subwayStationPlaceList) {
        this.stationCode = stationCode;
        this.externalCode = externalCode;
        this.stationName = stationName;
        this.stationNameEng = stationNameEng;
        this.stationNameJpn = stationNameJpn;
        this.stationNameChn = stationNameChn;
        this.lineNumber = lineNumber;
        this.subwayStationPlaceList = subwayStationPlaceList;
    }
}
