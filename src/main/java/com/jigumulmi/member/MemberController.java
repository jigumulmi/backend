package com.jigumulmi.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jigumulmi.member.dto.response.KakaoAuthResponseDto;
import com.jigumulmi.member.service.KakaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

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
}
