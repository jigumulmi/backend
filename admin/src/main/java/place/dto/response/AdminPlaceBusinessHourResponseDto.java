package place.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import place.dto.WeeklyBusinessHourDto;
import place.dto.request.AdminCreateTemporaryBusinessHourRequestDto;

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

    public static AdminPlaceBusinessHourResponseDto from(WeeklyBusinessHourDto fixedBusinessHour,
        List<TemporaryBusinessHourDto> temporaryBusinessHourList) {
        return AdminPlaceBusinessHourResponseDto.builder()
            .fixedBusinessHour(fixedBusinessHour)
            .temporaryBusinessHour(temporaryBusinessHourList)
            .build();
    }

}
