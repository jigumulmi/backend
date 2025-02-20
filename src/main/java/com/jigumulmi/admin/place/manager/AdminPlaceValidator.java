package com.jigumulmi.admin.place.manager;

import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.AdminErrorCode;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminPlaceValidator {

    private final PlaceRepository placeRepository;

    public void validatePlaceRemoval(Long placeId) {
        Place place = placeRepository.findById(placeId)
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));

        if (place.getIsApproved()) {
            throw new CustomException(AdminErrorCode.INVALID_PLACE_REMOVAL);
        }
    }

}
