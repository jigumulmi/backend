package com.jigumulmi.admin.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KakaoPlaceApiResponseDto {

    @Getter
    @Setter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Document {

        // TODO 장소 중복 방지 로직
        //private String id;
        private String placeName;
        private String categoryName;
        //private String categoryGroupCode;
        //private String categoryGroupName;
        private String phone;
        //private String addressName;
        private String roadAddressName;
        //private String x;
        //private String y;
        private String placeUrl;
        //private String distance;
    }

    @Getter
    @Setter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Meta {

        private Integer totalCount;
        private Integer pageableCount;
        private Boolean isEnd;
    }

    private List<Document> documents = new ArrayList<>();
    //private Meta meta;
}
