package com.jigumulmi.member;

import com.jigumulmi.common.PagedResponseDto;
import com.jigumulmi.member.dto.AdminMemberListResponseDto;
import com.jigumulmi.member.dto.AdminMemberListResponseDto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminMemberService {

    private final MemberRepository memberRepository;


    public PagedResponseDto<MemberDto> getMemberList(Pageable pageable) {
        Page<MemberDto> memberPage = memberRepository.findAll(pageable).map(MemberDto::from);
        return AdminMemberListResponseDto.of(memberPage, pageable);
    }

}
