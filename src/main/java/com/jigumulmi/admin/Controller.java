package com.jigumulmi.admin;

import com.jigumulmi.admin.dto.request.GetMemberListRequestDto;
import com.jigumulmi.admin.dto.response.MemberListResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class Controller {

    private final Service service;

    @Operation(summary = "멤버 리스트 조회")
    @ApiResponses(
        value = {@ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = MemberListResponseDto.class))})}
    )
    @GetMapping("/member")
    public ResponseEntity<?> getMemberList(@ModelAttribute GetMemberListRequestDto requestDto) {
        MemberListResponseDto memberList = service.getMemberList(requestDto);
        return ResponseEntity.ok().body(memberList);
    }
}
