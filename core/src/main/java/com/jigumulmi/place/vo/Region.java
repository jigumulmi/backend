package com.jigumulmi.place.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Region {
    SEOUL("서울", Arrays.asList(
        District.JONGNO_GU,
        District.SEOUL_JUNG_GU,
        District.YONGSAN_GU,
        District.SEONGDONG_GU,
        District.GWANGJIN_GU,
        District.DONGDAEMUN_GU,
        District.JUNGRANG_GU,
        District.SEONGBUK_GU,
        District.GANGBUK_GU,
        District.DOBONG_GU,
        District.NOWON_GU,
        District.EUNPYEONG_GU,
        District.SEODAEMUN_GU,
        District.MAPO_GU,
        District.YANGCHEON_GU,
        District.GANGSEO_GU,
        District.GURO_GU,
        District.GEUMCHEON_GU,
        District.YEONGDEUNGPO_GU,
        District.DONGJAK_GU,
        District.GWANAK_GU,
        District.SEOCHO_GU,
        District.GANGNAM_GU,
        District.SONGPA_GU,
        District.GANGDONG_GU
    )),
    INCHEON("인천", Arrays.asList(
        District.INCHEON_JUNG_GU,
        District.DONG_GU,
        District.MICHUHOL_GU,
        District.YEONSU_GU,
        District.NAMDONG_GU,
        District.BUPYEONG_GU,
        District.GYEYANG_GU,
        District.SEO_GU,
        District.GANGHWA_GUN,
        District.ONGJIN_GUN
    )),
    GYEONGGI("경기", Arrays.asList(
        District.SUWON_SI,
        District.YONGIN_SI,
        District.GOYANG_SI,
        District.HWASEONG_SI,
        District.SEONGNAM_SI,
        District.BUCHEON_SI,
        District.NAMYANGJU_SI,
        District.ANSAN_SI,
        District.PYEONGTAEK_SI,
        District.ANYANG_SI,
        District.SIHEUNG_SI,
        District.PAJU_SI,
        District.GIMPO_SI,
        District.UIJEONGBU_SI,
        District.GWANGJU_SI,
        District.HANAM_SI,
        District.GWANGMYEONG_SI,
        District.GUNPO_SI,
        District.YANGJU_SI,
        District.OSAN_SI,
        District.ICHEON_SI,
        District.ANSEONG_SI,
        District.GURI_SI,
        District.UIWANG_SI,
        District.POCHEON_SI,
        District.YANGPYEONG_GUN,
        District.YEOJU_SI,
        District.DONGDUCHEON_SI,
        District.GWACHEON_SI,
        District.GAPYEONG_GUN,
        District.YEONCHEON_GUN
    ))
    ;

    @JsonValue
    private final String title;
    private final List<District> districtList;

    private static final Map<String, Region> BY_TITLE = new HashMap<>();

    static {
        for (Region region: values()) {
            BY_TITLE.put(region.getTitle(), region);
        }
    }

    @JsonCreator
    public static Region ofTitle(String title) {
        return BY_TITLE.get(title);
    }

}