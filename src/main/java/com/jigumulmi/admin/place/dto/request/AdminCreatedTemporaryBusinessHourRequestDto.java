package com.jigumulmi.admin.place.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.jigumulmi.admin.place.dto.request.AdminUpdateFixedBusinessHourRequestDto.BusinessHour;
import com.jigumulmi.admin.place.dto.validator.ValidBusinessHour;
import com.jigumulmi.place.dto.TimeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminCreatedTemporaryBusinessHourRequestDto {

    private LocalDate date;
    private BusinessHour businessHour;
}

