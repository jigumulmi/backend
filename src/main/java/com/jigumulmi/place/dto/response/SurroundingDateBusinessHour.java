package com.jigumulmi.place.dto.response;

import com.jigumulmi.place.dto.BusinessHour;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SurroundingDateBusinessHour {

    private BusinessHour yesterday;
    private BusinessHour today;
}
