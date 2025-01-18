package com.jigumulmi.admin.place.dto.response;

import com.jigumulmi.admin.place.dto.request.AdminCreateTemporaryBusinessHourRequestDto;
import com.jigumulmi.admin.place.dto.request.AdminUpdateFixedBusinessHourRequestDto;
import com.jigumulmi.place.domain.FixedBusinessHour;
import com.jigumulmi.place.domain.TemporaryBusinessHour;
import com.jigumulmi.place.dto.BusinessHour;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminPlaceBusinessHourResponseDto {

    private AdminUpdateFixedBusinessHourRequestDto fixedBusinessHour;
    private List<AdminCreateTemporaryBusinessHourRequestDto> temporaryBusinessHour;

    public static BusinessHour fromFixedBusinessHour(FixedBusinessHour fixedBusinessHour) {
        return BusinessHour.builder()
            .openTime(fixedBusinessHour.getOpenTime())
            .closeTime(fixedBusinessHour.getCloseTime())
            .breakStart(fixedBusinessHour.getBreakStart())
            .breakEnd(fixedBusinessHour.getBreakEnd())
            .isDayOff(fixedBusinessHour.getIsDayOff())
            .build();
    }

    public static BusinessHour fromTemporaryBusinessHour(TemporaryBusinessHour fixedBusinessHour) {
        return BusinessHour.builder()
            .openTime(fixedBusinessHour.getOpenTime())
            .closeTime(fixedBusinessHour.getCloseTime())
            .breakStart(fixedBusinessHour.getBreakStart())
            .breakEnd(fixedBusinessHour.getBreakEnd())
            .isDayOff(fixedBusinessHour.getIsDayOff())
            .build();
    }
}
