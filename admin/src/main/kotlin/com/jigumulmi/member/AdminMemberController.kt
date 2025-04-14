package com.jigumulmi.member

import com.jigumulmi.common.AdminPagedResponseDto
import com.jigumulmi.member.dto.AdminMemberListResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "멤버 관리")
@RestController
@RequestMapping("/admin/member")
class AdminMemberController(
    private val adminMemberService: AdminMemberService
) {

    @Operation(summary = "멤버 리스트 조회")
    @GetMapping("")
    fun getMemberList(
        @ParameterObject pageable: Pageable
    ): ResponseEntity<AdminPagedResponseDto<AdminMemberListResponseDto.MemberDto>> {
        val memberList = adminMemberService.getMemberList(pageable)
        return ResponseEntity.ok().body(memberList)
    }
}
