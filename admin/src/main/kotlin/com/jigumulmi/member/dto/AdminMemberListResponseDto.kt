package com.jigumulmi.member.dto

import com.jigumulmi.common.AdminPagedResponseDto
import com.jigumulmi.member.domain.Member
import java.time.LocalDateTime


data class AdminMemberListResponseDto(
    override val page: PageDto,
    override val data: List<MemberDto>
) : AdminPagedResponseDto<AdminMemberListResponseDto.MemberDto>(page, data) {

    data class MemberDto(
        var createdAt: LocalDateTime,
        val modifiedAt: LocalDateTime,
        val deregisteredAt: LocalDateTime? = null,
        val email: String,
        val isAdmin: Boolean,
        val kakaoUserId: Long,
    ) {
        companion object {
            fun from(member: Member): MemberDto {
                return MemberDto(
                    createdAt = member.createdAt,
                    modifiedAt = member.modifiedAt,
                    deregisteredAt = member.deregisteredAt,
                    email = member.email,
                    isAdmin = member.isAdmin,
                    kakaoUserId = member.kakaoUserId
                )
            }
        }
    }
}
