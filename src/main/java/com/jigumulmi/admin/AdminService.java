package com.jigumulmi.admin;

import com.jigumulmi.admin.dto.request.GetMemberListRequestDto;
import com.jigumulmi.admin.dto.request.GetPlaceListRequestDto;
import com.jigumulmi.admin.dto.response.MemberListResponseDto;
import com.jigumulmi.admin.dto.response.MemberListResponseDto.MemberDto;
import com.jigumulmi.admin.dto.response.PageDto;
import com.jigumulmi.admin.dto.response.PlaceListResponseDto;
import com.jigumulmi.admin.dto.response.PlaceListResponseDto.PlaceDto;
import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import com.jigumulmi.member.MemberRepository;
import com.jigumulmi.place.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final int DEFAULT_PAGE_SIZE = 15;

    private final MemberRepository memberRepository;
    private final RestaurantRepository restaurantRepository;

    public MemberListResponseDto getMemberList(GetMemberListRequestDto requestDto) {
        Pageable pageable = PageRequest.of(requestDto.getPage() - 1, DEFAULT_PAGE_SIZE,
            Sort.by(requestDto.getDirection(), "id"));

        Page<MemberDto> memberPage = memberRepository.findAll(pageable).map(MemberDto::from);

        return MemberListResponseDto.builder()
            .data(memberPage.getContent())
            .page(PageDto.builder()
                .totalCount(memberPage.getTotalElements())
                .currentPage(requestDto.getPage())
                .totalPage(memberPage.getTotalPages())
                .build())
            .build();
    }

    public PlaceListResponseDto getPlaceList(GetPlaceListRequestDto requestDto) {
        Pageable pageable = PageRequest.of(requestDto.getPage() - 1, DEFAULT_PAGE_SIZE,
            Sort.by(requestDto.getDirection(), "id"));

        Page<PlaceDto> placePage = restaurantRepository.findAll(pageable).map(PlaceDto::from);

        return PlaceListResponseDto.builder()
            .data(placePage.getContent())
            .page(PageDto.builder()
                .totalCount(placePage.getTotalElements())
                .currentPage(requestDto.getPage())
                .totalPage(placePage.getTotalPages())
                .build())
            .build();
    }

    @Transactional(readOnly = true)
    public PlaceDto getPlaceDetail(Long placeId) {
        return restaurantRepository.findById(placeId).map(PlaceDto::detailedFrom)
            .orElseThrow(() -> new CustomException(
                CommonErrorCode.RESOURCE_NOT_FOUND));

    }
}
