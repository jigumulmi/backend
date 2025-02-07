package com.jigumulmi.admin.place.dto.response;

import com.jigumulmi.admin.place.dto.request.AdminCreateTemporaryBusinessHourRequestDto;
import com.jigumulmi.admin.place.dto.WeeklyBusinessHourDto;
import com.jigumulmi.place.domain.FixedBusinessHour;
import com.jigumulmi.place.domain.TemporaryBusinessHour;
import com.jigumulmi.place.dto.BusinessHour;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@Builder
public class AdminPlaceBusinessHourResponseDto {

    @Getter
    @SuperBuilder
    public static class TemporaryBusinessHourDto extends AdminCreateTemporaryBusinessHourRequestDto {
        private Long id;
    }

    private WeeklyBusinessHourDto fixedBusinessHour;
    private List<TemporaryBusinessHourDto> temporaryBusinessHour;

    public static BusinessHour fromFixedBusinessHour(FixedBusinessHour fixedBusinessHour) {
        return BusinessHour.builder()
            .openTime(fixedBusinessHour.getOpenTime())
            .closeTime(fixedBusinessHour.getCloseTime())
            .breakStart(fixedBusinessHour.getBreakStart())
            .breakEnd(fixedBusinessHour.getBreakEnd())
            .isDayOff(fixedBusinessHour.getIsDayOff())
            .build();
    }

    public static BusinessHour fromTemporaryBusinessHour(TemporaryBusinessHour temporaryBusinessHour) {
        return BusinessHour.builder()
            .openTime(temporaryBusinessHour.getOpenTime())
            .closeTime(temporaryBusinessHour.getCloseTime())
            .breakStart(temporaryBusinessHour.getBreakStart())
            .breakEnd(temporaryBusinessHour.getBreakEnd())
            .isDayOff(temporaryBusinessHour.getIsDayOff())
            .build();
    }
}
