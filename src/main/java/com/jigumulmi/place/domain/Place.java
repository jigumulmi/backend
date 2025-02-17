package com.jigumulmi.place.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jigumulmi.admin.place.dto.request.AdminCreatePlaceRequestDto;
import com.jigumulmi.banner.domain.BannerPlaceMapping;
import com.jigumulmi.common.Timestamped;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.place.dto.PositionDto;
import com.jigumulmi.place.vo.District;
import com.jigumulmi.place.vo.Region;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(columnList = "kakaoPlaceId", unique = true))
public class Place extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Comment("광역시도")
    @Column(columnDefinition = "varchar(40)")
    @Enumerated(EnumType.STRING)
    private Region region;

    @Comment("시군구")
    @Column(columnDefinition = "varchar(40)")
    @Enumerated(EnumType.STRING)
    private District district;

    @Comment("도로명 주소")
    private String address;

    private String contact;

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FixedBusinessHour> fixedBusinessHourList = new ArrayList<>();

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TemporaryBusinessHour> temporaryBusinessHourList = new ArrayList<>();

    private String openingHourSun;

    private String openingHourMon;

    private String openingHourTue;

    private String openingHourWed;

    private String openingHourThu;

    private String openingHourFri;

    private String openingHourSat;

    private String additionalInfo;

    @Column(length = 2083)
    private String placeUrl;

    private Double longitude;

    private Double latitude;

    private String registrantComment;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isApproved;

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PlaceCategoryMapping> categoryMappingList = new ArrayList<>();

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Menu> menuList = new ArrayList<>();

    @OneToMany(mappedBy = "place")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JsonManagedReference
    private List<Review> reviewList = new ArrayList<>();

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("isMain DESC")
    @JsonManagedReference
    private List<SubwayStationPlace> subwayStationPlaceList = new ArrayList<>();

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("isMain DESC")
    @JsonManagedReference
    private List<PlaceImage> placeImageList = new ArrayList<>();

    private String kakaoPlaceId;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isFromAdmin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "place")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JsonManagedReference
    private List<PlaceLike> placeLikeList = new ArrayList<>();

    @OneToMany(mappedBy = "place", orphanRemoval = true)
    private List<BannerPlaceMapping> bannerPlaceMappingList = new ArrayList<>();

    @Builder
    public Place(String name, List<PlaceCategoryMapping> categoryMappingList, Region region,
        District district, String address, String contact, List<Menu> menuList,
        List<FixedBusinessHour> fixedBusinessHourList,
        List<TemporaryBusinessHour> temporaryBusinessHourList,
        String openingHourSun, String openingHourMon, String openingHourTue, String openingHourWed,
        String openingHourThu, String openingHourFri, String openingHourSat, String additionalInfo,
        String placeUrl, Double longitude, Double latitude, String registrantComment,
        Boolean isApproved, List<Review> reviewList,
        List<SubwayStationPlace> subwayStationPlaceList,
        List<PlaceImage> placeImageList, String kakaoPlaceId,
        Boolean isFromAdmin, Member member, List<PlaceLike> placeLikeList,
        List<BannerPlaceMapping> bannerPlaceMappingList
    ) {
        this.name = name;
        this.categoryMappingList =
            categoryMappingList != null ? categoryMappingList : new ArrayList<>();
        this.region = region;
        this.district = district;
        this.address = address;
        this.contact = contact;
        this.menuList = menuList != null ? menuList : new ArrayList<>();
        this.fixedBusinessHourList =
            fixedBusinessHourList != null ? fixedBusinessHourList : new ArrayList<>();
        this.temporaryBusinessHourList =
            temporaryBusinessHourList != null ? temporaryBusinessHourList : new ArrayList<>();
        this.openingHourSun = openingHourSun;
        this.openingHourMon = openingHourMon;
        this.openingHourTue = openingHourTue;
        this.openingHourWed = openingHourWed;
        this.openingHourThu = openingHourThu;
        this.openingHourFri = openingHourFri;
        this.openingHourSat = openingHourSat;
        this.additionalInfo = additionalInfo;
        this.placeUrl = placeUrl;
        this.longitude = longitude;
        this.latitude = latitude;
        this.registrantComment = registrantComment;
        this.isApproved = (isApproved != null) ? isApproved : false;
        this.reviewList = reviewList != null ? reviewList : new ArrayList<>();
        this.subwayStationPlaceList =
            subwayStationPlaceList != null ? subwayStationPlaceList : new ArrayList<>();
        this.placeImageList = placeImageList != null ? placeImageList : new ArrayList<>();
        this.kakaoPlaceId = kakaoPlaceId;
        this.isFromAdmin = (isFromAdmin != null) ? isFromAdmin : false;
        this.member = member;
        this.placeLikeList = placeLikeList != null ? placeLikeList : new ArrayList<>();
        this.bannerPlaceMappingList =
            bannerPlaceMappingList != null ? bannerPlaceMappingList : new ArrayList<>();
    }

    public void addCategoryAndSubwayStation(List<PlaceCategoryMapping> categoryMappingList,
        List<SubwayStationPlace> subwayStationPlaceList) {
        this.categoryMappingList.addAll(categoryMappingList);
        this.subwayStationPlaceList.addAll(subwayStationPlaceList);
    }

    public void adminBasicUpdate(AdminCreatePlaceRequestDto requestDto) {
        PositionDto position = requestDto.getPosition();

        this.name = requestDto.getName();
        this.region = requestDto.getRegion();
        this.district = requestDto.getDistrict();
        this.address = requestDto.getAddress();
        this.contact = requestDto.getContact();
        this.additionalInfo = requestDto.getAdditionalInfo();
        this.placeUrl = requestDto.getPlaceUrl();
        this.longitude = position.getLongitude();
        this.latitude = position.getLatitude();
        this.registrantComment = requestDto.getRegistrantComment();
        this.kakaoPlaceId = requestDto.getKakaoPlaceId();
    }
}
