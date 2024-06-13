package com.jigumulmi.admin.repository;

import com.jigumulmi.admin.dto.response.AdminPlaceListResponseDto.PlaceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomAdminRepository {

    Page<PlaceDto> getPlaceList(Pageable pageable);
}
