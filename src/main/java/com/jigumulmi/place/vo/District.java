package com.jigumulmi.place.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum District {
    // 서울
    JONGNO_GU("종로구"),
    SEOUL_JUNG_GU("중구"),
    YONGSAN_GU("용산구"),
    SEONGDONG_GU("성동구"),
    GWANGJIN_GU("광진구"),
    DONGDAEMUN_GU("동대문구"),
    JUNGRANG_GU("중랑구"),
    SEONGBUK_GU("성북구"),
    GANGBUK_GU("강북구"),
    DOBONG_GU("도봉구"),
    NOWON_GU("노원구"),
    EUNPYEONG_GU("은평구"),
    SEODAEMUN_GU("서대문구"),
    MAPO_GU("마포구"),
    YANGCHEON_GU("양천구"),
    GANGSEO_GU("강서구"),
    GURO_GU("구로구"),
    GEUMCHEON_GU("금천구"),
    YEONGDEUNGPO_GU("영등포구"),
    DONGJAK_GU("동작구"),
    GWANAK_GU("관악구"),
    SEOCHO_GU("서초구"),
    GANGNAM_GU("강남구"),
    SONGPA_GU("송파구"),
    GANGDONG_GU("강동구"),

    //인천
    INCHEON_JUNG_GU("중구"),
    DONG_GU("동구"),
    MICHUHOL_GU("미추홀구"),
    YEONSU_GU("연수구"),
    NAMDONG_GU("남동구"),
    BUPYEONG_GU("부평구"),
    GYEYANG_GU("계양구"),
    SEO_GU("서구"),
    GANGHWA_GUN("강화군"),
    ONGJIN_GUN("옹진군"),

    // 경기
    SUWON_SI("수원시"),
    YONGIN_SI("용인시"),
    GOYANG_SI("고양시"),
    HWASEONG_SI("화성시"),
    SEONGNAM_SI("성남시"),
    BUCHEON_SI("부천시"),
    NAMYANGJU_SI("남양주시"),
    ANSAN_SI("안산시"),
    PYEONGTAEK_SI("평택시"),
    ANYANG_SI("안양시"),
    SIHEUNG_SI("시흥시"),
    PAJU_SI("파주시"),
    GIMPO_SI("김포시"),
    UIJEONGBU_SI("의정부시"),
    GWANGJU_SI("광주시"),
    HANAM_SI("하남시"),
    GWANGMYEONG_SI("광명시"),
    GUNPO_SI("군포시"),
    YANGJU_SI("양주시"),
    OSAN_SI("오산시"),
    ICHEON_SI("이천시"),
    ANSEONG_SI("안성시"),
    GURI_SI("구리시"),
    UIWANG_SI("의왕시"),
    POCHEON_SI("포천시"),
    YANGPYEONG_GUN("양평군"),
    YEOJU_SI("여주시"),
    DONGDUCHEON_SI("동두천시"),
    GWACHEON_SI("과천시"),
    GAPYEONG_GUN("가평군"),
    YEONCHEON_GUN("연천군"),
    ;

    @JsonValue
    private final String title;

    @JsonCreator
    public static District ofTitle(String title) {
        return Arrays.stream(District.values())
            .filter(district -> district.getTitle().equals(title))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}