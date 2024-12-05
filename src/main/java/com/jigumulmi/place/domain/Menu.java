package com.jigumulmi.place.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"place"})
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    @JsonBackReference
    private Place place;

    private Boolean isMain;

    private String price;

    private String description;

    @Column(name = "image_s3_key", length = 1024)
    private String imageS3Key;

    @Builder
    public Menu(String name, Place place, Boolean isMain, String price, String description,
        String imageS3Key) {
        this.name = name;
        this.place = place;
        this.isMain = isMain;
        this.price = price;
        this.description = description;
        this.imageS3Key = imageS3Key;
    }
}
