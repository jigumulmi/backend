package com.jigumulmi.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.member.dto.request.KakaoAuthorizationRequestDto;
import com.jigumulmi.member.dto.request.SetNicknameRequestDto;
import com.jigumulmi.member.dto.response.KakaoAuthResponseDto;
import com.jigumulmi.utils.MemberTestUtils;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * 서비스 계층 로직이 매니저 계층 메서드를 조합하는 경우만 존재하기 때문에 테스트의 의미가 매우 적다고 생각하지만,
 * <p>
 * 아직 방향을 잡아나가는 과정이기 때문에 우선 작성
 * <p>
 * 경우에 따라 서비스 계층만의 단위 테스트로 작성하거나, 통합테스트이지만 매니저 계층까지만 제한하면 될 듯 하다
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @SpyBean
    private MemberManager memberManager;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("카카오 인증")
    public void testKakaoAuthorize() throws JsonProcessingException {
        // given
        MockHttpSession session = new MockHttpSession();

        KakaoAuthorizationRequestDto requestDto = new KakaoAuthorizationRequestDto();

        String accessToken = "testAccessToken";
        Mockito.doReturn(accessToken).when(memberManager).getKakaoAccessToken(requestDto);

        String email = "test@email.com";
        Mockito.doReturn(email).when(memberManager).getKakaoUserEmail(accessToken);

        Long kakaoUserId = 1234L;
        Mockito.doReturn(kakaoUserId).when(memberManager).getKakaoUserId(accessToken);

        // when
        KakaoAuthResponseDto responseDto = memberService.kakaoAuthorize(requestDto, session);

        // then
        Assertions.assertFalse(responseDto.isHasRegistered());
        Assertions.assertNotNull(
            session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));
    }

    @Test
    @DisplayName("회원 탈퇴")
    public void testDeregister() {
        // given
        MockHttpSession session = new MockHttpSession();

        Long memberId = 1L;
        Member member = MemberTestUtils.getMember(memberId, false);
        LocalDateTime originalDeregisteredAt = member.getDeregisteredAt();

        memberRepository.save(member);

        Mockito.doNothing().when(memberManager).unlinkKakao(member);

        // when
        memberService.deregister(session, member);

        // then
        LocalDateTime changedDeregisteredAt = member.getDeregisteredAt();
        Assertions.assertNotNull(changedDeregisteredAt);
        Assertions.assertNotEquals(changedDeregisteredAt, originalDeregisteredAt);
        Assertions.assertEquals(member.getId(), memberId);
        Assertions.assertTrue(session.isInvalid());
    }

    @Test
    @DisplayName("닉네임 설정")
    public void testSetNickname() {
        // given
        MockHttpSession session = new MockHttpSession();

        Long memberId = 1L;
        Member member = MemberTestUtils.getMember(memberId, false);

        SetNicknameRequestDto setNicknameRequestDto = new SetNicknameRequestDto();
        String changedNickname = "changedNickname";
        ReflectionTestUtils.setField(setNicknameRequestDto, "nickname", changedNickname);

        memberRepository.save(member);

        // when
        memberService.setNickname(session, member, setNicknameRequestDto);

        // then
        Assertions.assertEquals(member.getNickname(), changedNickname);
        Assertions.assertEquals(member.getId(), memberId);
        Assertions.assertNotNull(
            session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));
    }
}
