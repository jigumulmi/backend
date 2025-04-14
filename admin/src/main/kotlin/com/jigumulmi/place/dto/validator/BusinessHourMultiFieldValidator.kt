package com.jigumulmi.place.dto.validator

import com.jigumulmi.place.dto.BusinessHour
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class BusinessHourMultiFieldValidator : ConstraintValidator<ValidBusinessHour, BusinessHour> {
    override fun isValid(
        businessHour: BusinessHour,
        context: ConstraintValidatorContext
    ): Boolean {
        context.disableDefaultConstraintViolation()

        if (businessHour.isDayOff == null) {
            context.buildConstraintViolationWithTemplate("휴무 여부 데이터가 없습니다")
                .addConstraintViolation()
            return false
        }

        if (businessHour.isDayOff == true) { // 휴무일
            if (businessHour.openTime != null || businessHour.closeTime != null || businessHour.breakStart != null || businessHour.breakEnd != null) {
                context.buildConstraintViolationWithTemplate("운영 시간 데이터를 제거해주세요")
                    .addConstraintViolation()
                return false
            }
        } else { // 운영일
            if (businessHour.openTime == null || businessHour.closeTime == null) {
                context.buildConstraintViolationWithTemplate("휴무 여부를 확인해주세요")
                    .addConstraintViolation()
                return false
            } else {
                if ((businessHour.breakStart == null) xor (businessHour.breakEnd == null)) {
                    context.buildConstraintViolationWithTemplate("브레이크 타임 설정을 확인해주세요")
                        .addConstraintViolation()
                    return false
                }
            }
        }

        return true
    }
}
