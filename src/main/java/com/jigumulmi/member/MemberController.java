package com.jigumulmi.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jigumulmi.config.security.UserDetailsImpl;
import com.jigumulmi.member.service.KakaoService;
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

    private final KakaoService kakaoService;

    @GetMapping("/oauth/kakao/callback")
    public ResponseEntity<?> redirectKakaoLogin(@RequestParam(name = "code") String authorizationCode, HttpSession session) throws JsonProcessingException {
        kakaoService.authorize(authorizationCode, session);
        return ResponseEntity.ok().body("Kakao OAuth Success");
    }
}
