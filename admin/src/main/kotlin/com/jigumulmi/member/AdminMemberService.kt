package com.jigumulmi.member

import com.jigumulmi.common.AdminPagedResponseDto
import com.jigumulmi.member.dto.AdminMemberListResponseDto.MemberDto
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class AdminMemberService (
    private val memberRepository: MemberRepository
){

    fun getMemberList(pageable: Pageable): AdminPagedResponseDto<MemberDto> {
        val memberPage = memberRepository.findAll(pageable).map(MemberDto::from)
        return AdminPagedResponseDto.of(memberPage, pageable)
    }
}
