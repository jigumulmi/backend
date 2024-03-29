package com.jigumulmi.place.domain;

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

    private String name;

    private String line;

    @OneToMany(mappedBy = "subwayStation")
    private List<Restaurant> restaurantList = new ArrayList<>();

    @Builder
    public SubwayStation(String name, String line, List<Restaurant> restaurantList) {
        this.name = name;
        this.line = line;
        this.restaurantList = restaurantList;
    }
}
