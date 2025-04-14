package com.jigumulmi.member

import com.jigumulmi.common.PagedResponseDto
import com.jigumulmi.member.dto.AdminMemberListResponseDto.MemberDto
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class AdminMemberService (
    private val memberRepository: MemberRepository
){

    fun getMemberList(pageable: Pageable): PagedResponseDto<MemberDto> {
        val memberPage = memberRepository.findAll(pageable).map(MemberDto::from)
        return PagedResponseDto.of(memberPage, pageable)
    }
}
