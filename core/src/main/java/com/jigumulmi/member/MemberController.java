package com.jigumulmi.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jigumulmi.config.security.RequiredAuthUser;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.member.dto.request.KakaoAuthorizationRequestDto;
import com.jigumulmi.member.dto.request.SetNicknameRequestDto;
import com.jigumulmi.member.dto.response.KakaoAuthResponseDto;
import com.jigumulmi.member.dto.response.MemberDetailResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "카카오 인증(로그인 및 회원가입)")
    @ApiResponses(
        value = {@ApiResponse(responseCode = "201", content = {
            @Content(schema = @Schema(implementation = KakaoAuthResponseDto.class))})}
    )
    @PostMapping("/oauth/kakao/login")
    public ResponseEntity<?> kakaoAuthorize(
        @Valid @RequestBody KakaoAuthorizationRequestDto requestDto, HttpSession session)
        throws JsonProcessingException {
        KakaoAuthResponseDto response = memberService.kakaoAuthorize(requestDto, session);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "로그아웃")
    @ApiResponse(responseCode = "201")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session, @RequiredAuthUser Member member) {
        memberService.logout(session);
        return ResponseEntity.status(HttpStatus.CREATED).body("Logout success");
    }

    @Operation(summary = "회원 탈퇴")
    @ApiResponse(responseCode = "201")
    @PostMapping("/deregister")
    public ResponseEntity<?> deregister(HttpSession session, @RequiredAuthUser Member member) {
        memberService.deregister(session, member);
        return ResponseEntity.status(HttpStatus.CREATED).body("Deregister success");
    }

    @Operation(summary = "닉네임 수정(생성)")
    @ApiResponse(responseCode = "201")
    @PutMapping("/nickname")
    public ResponseEntity<?> setNickname(HttpSession session, @RequiredAuthUser Member member,
        @Valid @RequestBody SetNicknameRequestDto requestDto) {
        memberService.setNickname(session, member, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Set nickname success");
    }

    @Operation(summary = "유저 상세 정보 조회")
    @ApiResponses(
        value = {@ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = MemberDetailResponseDto.class))})}
    )
    @GetMapping("")
    public ResponseEntity<?> getUserDetail(@RequiredAuthUser Member member) {
        MemberDetailResponseDto userDetail = memberService.getUserDetail(member);
        return ResponseEntity.ok().body(userDetail);
    }
}
