package com.jigumulmi.place.dto.validator;

import com.jigumulmi.place.dto.BusinessHour;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BusinessHourMultiFieldValidator implements
    ConstraintValidator<ValidBusinessHour, BusinessHour> {

    @Override
    public boolean isValid(BusinessHour businessHour, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        
        if (businessHour == null) {
            context.buildConstraintViolationWithTemplate("데이터가 없습니다")
                .addConstraintViolation();
            return false;
        }

        if (businessHour.getIsDayOff() == null) {
            context.buildConstraintViolationWithTemplate("휴무 여부 데이터가 없습니다")
                .addConstraintViolation();
            return false;
        }

        if (businessHour.getIsDayOff().equals(true)) { // 휴무일
            if (businessHour.getOpenTime() != null || businessHour.getCloseTime() != null
                || businessHour.getBreakStart() != null || businessHour.getBreakEnd() != null) {
                context.buildConstraintViolationWithTemplate("운영 시간 데이터를 제거해주세요")
                    .addConstraintViolation();
                return false;
            }
        } else { // 운영일
            if (businessHour.getOpenTime() == null || businessHour.getCloseTime() == null) {
                context.buildConstraintViolationWithTemplate("휴무 여부를 확인해주세요")
                    .addConstraintViolation();
                return false;
            } else {
                if (businessHour.getBreakStart() == null ^ businessHour.getBreakEnd() == null) {
                    context.buildConstraintViolationWithTemplate("브레이크 타임 설정을 확인해주세요")
                        .addConstraintViolation();
                    return false;
                }
            }
        }

        return true;
    }
}
