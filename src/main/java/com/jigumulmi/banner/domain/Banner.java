package com.jigumulmi.banner.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jigumulmi.config.common.Timestamped;
import jakarta.persistence.Column;
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
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Banner extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String title;
    @Column(name = "outer_image_s3_key")
    private String outerImageS3Key;
    @Column(name = "inner_image_s3_key")
    private String innerImageS3Key;
    @ColumnDefault("false")
    private Boolean isActive;

    @OneToMany(mappedBy = "banner", orphanRemoval = true)
    @JsonManagedReference
    private List<BannerPlaceMapping> bannerPlaceMappingList = new ArrayList<>();

    @Builder
    public Banner(String title, String outerImageS3Key, String innerImageS3Key, Boolean isActive,
        List<BannerPlaceMapping> bannerPlaceMappingList) {
        this.title = title;
        this.outerImageS3Key = outerImageS3Key;
        this.innerImageS3Key = innerImageS3Key;
        this.isActive = isActive;
        this.bannerPlaceMappingList =
            bannerPlaceMappingList == null ? new ArrayList<>() : bannerPlaceMappingList;
    }

    public void updateDetail(String title, Boolean isActive) {
        this.title = title;
        this.isActive = isActive;
    }

    public void updateOuterS3ImageKey(String outerImageS3Key) {
        this.outerImageS3Key = outerImageS3Key;
    }

    public void updateInnerS3ImageKey(String innerImageS3Key) {
        this.innerImageS3Key = innerImageS3Key;
    }

}
