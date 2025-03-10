package com.jigumulmi.place.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubwayStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stationName;

    @OneToMany(mappedBy = "subwayStation")
    @OrderBy("isMain DESC")
    @JsonManagedReference
    private List<SubwayStationPlace> subwayStationPlaceList = new ArrayList<>();

    @OneToMany(mappedBy = "subwayStation")
    @JsonManagedReference
    private List<SubwayStationLineMapping> subwayStationLineMappingList = new ArrayList<>();

    @Builder
    public SubwayStation(String stationName, List<SubwayStationPlace> subwayStationPlaceList,
        List<SubwayStationLineMapping> subwayStationLineMappingList) {
        this.stationName = stationName;
        this.subwayStationPlaceList = subwayStationPlaceList;
        this.subwayStationLineMappingList = subwayStationLineMappingList;
    }
}
