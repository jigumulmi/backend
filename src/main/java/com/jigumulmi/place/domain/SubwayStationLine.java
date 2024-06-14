package com.jigumulmi.place.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubwayStationLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lineNumber;

    @OneToMany(mappedBy = "subwayStationLine")
    @JsonManagedReference
    private List<SubwayStationLineMapping> subwayStationLineMappingList = new ArrayList<>();

    @Builder
    public SubwayStationLine(String lineNumber,
        List<SubwayStationLineMapping> subwayStationLineMappingList) {
        this.lineNumber = lineNumber;
        this.subwayStationLineMappingList = subwayStationLineMappingList;
    }
}
