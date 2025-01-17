package com.jigumulmi.admin.place.dto.validator;

import com.jigumulmi.admin.place.dto.request.AdminUpdateFixedBusinessHourRequestDto.BusinessHour;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BusinessHourMultiFieldValidator implements ConstraintValidator<ValidBusinessHour, BusinessHour> {

    @Override
    public boolean isValid(BusinessHour businessHour, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (businessHour.getIsDayOff().equals(true)) {
            if (businessHour.getOpenTime() != null && businessHour.getCloseTime() != null) {
                context.buildConstraintViolationWithTemplate("휴무 설정 오류")
                    .addConstraintViolation();
                return false;
            }
        } else {
            if (businessHour.getOpenTime() == null && businessHour.getCloseTime() == null) {
                context.buildConstraintViolationWithTemplate("운영 시간 설정 오류")
                    .addConstraintViolation();
                return false;
            } else {
                if (businessHour.getBreakStart().isBefore(businessHour.getOpenTime())
                    || businessHour.getBreakEnd().isAfter(businessHour.getCloseTime())) {
                    context.buildConstraintViolationWithTemplate("브레이크 타임 설정 오류")
                        .addConstraintViolation();
                    return false;
                }
            }
        }

        return true;
    }
}
