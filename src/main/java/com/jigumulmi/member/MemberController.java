package com.jigumulmi.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jigumulmi.config.security.UserDetailsImpl;
import com.jigumulmi.member.dto.response.KakaoAuthResponseDto;
import com.jigumulmi.member.service.KakaoService;
import com.jigumulmi.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final KakaoService kakaoService;

    @Operation(summary = "카카오 인증(로그인 및 회원가입)")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = KakaoAuthResponseDto.class))})}
    )
    @GetMapping("/oauth/kakao/login")
    public ResponseEntity<?> kakaoAuthorization(@RequestParam(name = "code") String authorizationCode, HttpSession session) throws JsonProcessingException {
        KakaoAuthResponseDto response = kakaoService.authorize(authorizationCode, session);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "로그아웃")
    @ApiResponse(responseCode = "201")
    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().body("Logout success");
    }

    @Operation(summary = "회원 탈퇴")
    @ApiResponse(responseCode = "201")
    @GetMapping("/deregister")
    public ResponseEntity<?> deregister(HttpSession session, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        session.invalidate();
        kakaoService.unlink(userDetails);
        memberService.removeMember(userDetails);
        return ResponseEntity.ok().body("Deregister success");
    }
}
