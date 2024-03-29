package com.jigumulmi.place.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
