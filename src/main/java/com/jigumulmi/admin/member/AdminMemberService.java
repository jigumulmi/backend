package com.jigumulmi.admin.member;

import com.jigumulmi.admin.member.dto.AdminMemberListResponseDto;
import com.jigumulmi.admin.member.dto.AdminMemberListResponseDto.MemberDto;
import com.jigumulmi.common.PageDto;
import com.jigumulmi.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminMemberService {

    private final MemberRepository memberRepository;


    public AdminMemberListResponseDto getMemberList(Pageable pageable) {
        Page<MemberDto> memberPage = memberRepository.findAll(pageable).map(MemberDto::from);

        return AdminMemberListResponseDto.builder()
            .data(memberPage.getContent())
            .page(PageDto.builder()
                .totalCount(memberPage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .totalPage(memberPage.getTotalPages())
                .build()
            )
            .build();
    }

}
