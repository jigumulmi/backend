package com.jigumulmi.place.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum District {
    // 서울
    JONGNO_GU("종로구", 1),
    SEOUL_JUNG_GU("중구", 2),
    YONGSAN_GU("용산구", 3),
    SEONGDONG_GU("성동구", 4),
    GWANGJIN_GU("광진구", 5),
    DONGDAEMUN_GU("동대문구", 6),
    JUNGRANG_GU("중랑구", 7),
    SEONGBUK_GU("성북구", 8),
    GANGBUK_GU("강북구", 9),
    DOBONG_GU("도봉구", 10),
    NOWON_GU("노원구", 11),
    EUNPYEONG_GU("은평구", 12),
    SEODAEMUN_GU("서대문구", 13),
    MAPO_GU("마포구", 14),
    YANGCHEON_GU("양천구", 15),
    GANGSEO_GU("강서구", 16),
    GURO_GU("구로구", 17),
    GEUMCHEON_GU("금천구", 18),
    YEONGDEUNGPO_GU("영등포구", 19),
    DONGJAK_GU("동작구", 20),
    GWANAK_GU("관악구", 21),
    SEOCHO_GU("서초구", 22),
    GANGNAM_GU("강남구", 23),
    SONGPA_GU("송파구", 24),
    GANGDONG_GU("강동구", 25),

    // 인천
    INCHEON_JUNG_GU("중구", 26),
    DONG_GU("동구", 27),
    MICHUHOL_GU("미추홀구", 28),
    YEONSU_GU("연수구", 29),
    NAMDONG_GU("남동구", 30),
    BUPYEONG_GU("부평구", 31),
    GYEYANG_GU("계양구", 32),
    SEO_GU("서구", 33),
    GANGHWA_GUN("강화군", 34),
    ONGJIN_GUN("옹진군", 35),

    // 경기
    SUWON_SI("수원시", 36),
    YONGIN_SI("용인시", 37),
    GOYANG_SI("고양시", 38),
    HWASEONG_SI("화성시", 39),
    SEONGNAM_SI("성남시", 40),
    BUCHEON_SI("부천시", 41),
    NAMYANGJU_SI("남양주시", 42),
    ANSAN_SI("안산시", 43),
    PYEONGTAEK_SI("평택시", 44),
    ANYANG_SI("안양시", 45),
    SIHEUNG_SI("시흥시", 46),
    PAJU_SI("파주시", 47),
    GIMPO_SI("김포시", 48),
    UIJEONGBU_SI("의정부시", 49),
    GWANGJU_SI("광주시", 50),
    HANAM_SI("하남시", 51),
    GWANGMYEONG_SI("광명시", 52),
    GUNPO_SI("군포시", 53),
    YANGJU_SI("양주시", 54),
    OSAN_SI("오산시", 55),
    ICHEON_SI("이천시", 56),
    ANSEONG_SI("안성시", 57),
    GURI_SI("구리시", 58),
    UIWANG_SI("의왕시", 59),
    POCHEON_SI("포천시", 60),
    YANGPYEONG_GUN("양평군", 61),
    YEOJU_SI("여주시", 62),
    DONGDUCHEON_SI("동두천시", 63),
    GWACHEON_SI("과천시", 64),
    GAPYEONG_GUN("가평군", 65),
    YEONCHEON_GUN("연천군", 66)
    ;

    @JsonValue
    private final String title;
    private final Integer id;

    private static final Map<Integer, District> BY_ID = new HashMap<>();

    static {
        for (District district: values()) {
            BY_ID.put(district.getId(), district);
        }
    }

    @JsonCreator
    public static District ofId(Integer id) {
        return BY_ID.get(id);
    }
}