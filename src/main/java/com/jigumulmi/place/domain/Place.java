package com.jigumulmi.place.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jigumulmi.admin.place.dto.request.AdminUpdatePlaceRequestDto;
import com.jigumulmi.banner.domain.BannerPlaceMapping;
import com.jigumulmi.config.common.Timestamped;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.place.dto.response.PlaceDetailResponseDto.OpeningHourDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto.PositionDto;
import com.jigumulmi.place.vo.District;
import com.jigumulmi.place.vo.Region;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    @Column(length = 20)
    private Region region;
    @Comment("시군구")
    @Column(length = 20)
    private District district;
    @Comment("도로명 주소")
    private String address;

    private String contact;

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
    @JsonManagedReference
    private List<PlaceLike> placeLikeList = new ArrayList<>();

    @OneToMany(mappedBy = "place", orphanRemoval = true)
    private List<BannerPlaceMapping> bannerPlaceMappingList = new ArrayList<>();

    @Builder
    public Place(String name, List<PlaceCategoryMapping> categoryMappingList, Region region,
        District district, String address,
        String contact, List<Menu> menuList,
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

    public void addChildren(List<PlaceCategoryMapping> categoryMappingList,
        List<SubwayStationPlace> subwayStationPlaceList,
        List<Menu> menuList, List<PlaceImage> placeImageList) {
        this.categoryMappingList.addAll(categoryMappingList);
        this.menuList.addAll(menuList);
        this.subwayStationPlaceList.addAll(subwayStationPlaceList);
        this.placeImageList.addAll(placeImageList);
    }

    public void adminUpdate(AdminUpdatePlaceRequestDto requestDto,
        List<PlaceCategoryMapping> categoryMappingList,
        List<SubwayStationPlace> subwayStationPlaceList,
        List<Menu> menuList, List<PlaceImage> placeImageList) {
        OpeningHourDto openingHour = requestDto.getOpeningHour();
        PositionDto position = requestDto.getPosition();

        this.name = requestDto.getName();
        this.address = requestDto.getAddress();
        this.contact = requestDto.getContact();
        this.openingHourSun = openingHour.getOpeningHourSun();
        this.openingHourMon = openingHour.getOpeningHourMon();
        this.openingHourTue = openingHour.getOpeningHourTue();
        this.openingHourWed = openingHour.getOpeningHourWed();
        this.openingHourThu = openingHour.getOpeningHourThu();
        this.openingHourFri = openingHour.getOpeningHourFri();
        this.openingHourSat = openingHour.getOpeningHourSat();
        this.additionalInfo = requestDto.getAdditionalInfo();
        this.placeUrl = requestDto.getPlaceUrl();
        this.longitude = position.getLongitude();
        this.latitude = position.getLatitude();
        this.registrantComment = requestDto.getRegistrantComment();
        this.isApproved = requestDto.getIsApproved();
        this.kakaoPlaceId = requestDto.getKakaoPlaceId();

        // 실제 쿼리는 insert 후 delete가 이루어지므로
        // 제약조건이 걸리는 경우 위배되지 않는 데이터만 addAll 해야한다
        // TODO 메서드화
        List<PlaceCategoryMapping> intersectionWithElementsFromLeft = PlaceCategoryMapping.getIntersectionWithElementsFromLeft(
            this.categoryMappingList, categoryMappingList);

        categoryMappingList.removeAll(this.categoryMappingList);
        categoryMappingList.addAll(intersectionWithElementsFromLeft);

        this.categoryMappingList.clear();
        this.menuList.clear();
        this.subwayStationPlaceList.clear();
        this.placeImageList.clear();

        addChildren(categoryMappingList, subwayStationPlaceList, menuList, placeImageList);
    }
}
