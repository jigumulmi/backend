package com.jigumulmi.admin;

import com.jigumulmi.admin.dto.request.GetMemberListRequestDto;
import com.jigumulmi.admin.dto.response.MemberListResponseDto;
import com.jigumulmi.admin.dto.response.MemberListResponseDto.MemberDto;
import com.jigumulmi.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class Service {

    private final int DEFAULT_PAGE_SIZE = 10;
    private final MemberRepository memberRepository;

    public MemberListResponseDto getMemberList(GetMemberListRequestDto requestDto) {
        Pageable pageable = PageRequest.of(requestDto.getPage(), DEFAULT_PAGE_SIZE,
            Sort.by(requestDto.getDirection(), "id"));

        Page<MemberDto> memberPage = memberRepository.findAll(pageable).map(MemberDto::from);

        return MemberListResponseDto.builder()
            .memberList(memberPage.getContent())
            .totalCount(memberPage.getNumberOfElements())
            .build();
    }

}
