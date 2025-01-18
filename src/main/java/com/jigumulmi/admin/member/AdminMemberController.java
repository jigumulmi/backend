package com.jigumulmi.admin.member;

import com.jigumulmi.admin.member.dto.AdminMemberListResponseDto;
import com.jigumulmi.common.PageableParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "멤버 관리")
@RestController
@RequestMapping("/admin/member")
@RequiredArgsConstructor
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @Operation(summary = "멤버 리스트 조회")
    @ApiResponses(
        value = {@ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = AdminMemberListResponseDto.class))})}
    )
    @PageableParams
    @GetMapping("")
    public ResponseEntity<?> getMemberList(
        @ParameterObject Pageable pageable) {
        AdminMemberListResponseDto memberList = adminMemberService.getMemberList(pageable);
        return ResponseEntity.ok().body(memberList);
    }
}
