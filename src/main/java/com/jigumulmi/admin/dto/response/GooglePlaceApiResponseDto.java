package com.jigumulmi.admin.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @see <a
 * href="https://developers.kakao.com/docs/latest/ko/local/dev-guide#search-by-keyword">kakao</a>
 * @see <a
 * href="https://developers.google.com/maps/documentation/places/web-service/place-details?hl=ko">google</a>
 */
@Getter
@Setter
@ToString
public class GooglePlaceApiResponseDto {

    @Getter
    @Setter
    public static class Location {

        private Double latitude;
        private Double longitude;
    }

    @Getter
    @Setter
    public static class RegularOpeningHours {

        @Getter
        @Setter
        public static class OpenClose {

            private Integer day; // 0:일 ~ 6:토
            private Integer hour;
            private Integer minute;
        }

        @Getter
        @Setter
        public static class Period {

            private OpenClose open;
            private OpenClose close;

            public static String makeString(Period period) {
                OpenClose open = period.getOpen();
                OpenClose close = period.getClose();

                String openString = open.getHour() + ":" + open.getMinute();
                String closeString = close.getHour() + ":" + close.getMinute();

                return openString + " - " + closeString;
            }
        }

        //private Boolean openNow;
        private List<Period> periods;
    }

    @Getter
    @Setter
    public static class DisplayName {

        private String text;
    }

    private Location location;
    private RegularOpeningHours regularOpeningHours;
    private DisplayName displayName;
}
