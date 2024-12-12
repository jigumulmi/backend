package com.jigumulmi.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jigumulmi.common.MemberTestUtils;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.member.dto.KakaoMemberInfoDto;
import com.jigumulmi.member.dto.request.KakaoAuthorizationRequestDto;
import com.jigumulmi.member.dto.response.KakaoAuthResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class KakaoServiceTest {

    @InjectMocks
    @Spy
    private KakaoService kakaoService;

    @Test
    @DisplayName("카카오 로그인")
    public void testAuthorize() throws JsonProcessingException {
        // given
        MockHttpSession session = new MockHttpSession();

        KakaoAuthorizationRequestDto kakaoAuthorizationRequestDto = new KakaoAuthorizationRequestDto();
        ReflectionTestUtils.setField(kakaoAuthorizationRequestDto, "code", "testCode");
        ReflectionTestUtils.setField(kakaoAuthorizationRequestDto, "redirectUrl",
            "testRedirectUrl");

        String accessToken = "testAccessToken";
        Mockito.doReturn(accessToken).when(kakaoService)
            .getAccessToken(kakaoAuthorizationRequestDto);

        String email = "test@email.com";
        KakaoMemberInfoDto kakaoMemberInfoDto = KakaoMemberInfoDto.builder().email(email).build();
        Mockito.doReturn(kakaoMemberInfoDto).when(kakaoService).getKakaoMemberInfo(accessToken);

        Member member = MemberTestUtils.getMember(1L, false);
        Mockito.doReturn(member).when(kakaoService)
            .registerKakaoUserIfNeeded(kakaoMemberInfoDto, accessToken);

        // when
        KakaoAuthResponseDto kakaoAuthResponseDto = kakaoService.authorize(
            kakaoAuthorizationRequestDto, session);

        // then
        Assertions.assertEquals(kakaoAuthResponseDto.getNickname(), member.getNickname());
        Assertions.assertTrue(kakaoAuthResponseDto.isHasRegistered());
        Assertions.assertNotNull(
            session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));
    }
}
