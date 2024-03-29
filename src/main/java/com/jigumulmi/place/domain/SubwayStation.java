package com.jigumulmi.place.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubwayStation {

    @Id
    @GeneratedValue
    @Column
    private Long id;

    private String name;

    private String line;

    @OneToMany(mappedBy = "subwayStation")
    private List<Restaurant> restaurantList = new ArrayList<>();
}
