package com.jigumulmi.member.service;

import static org.mockito.BDDMockito.given;

import com.jigumulmi.member.MemberRepository;
import com.jigumulmi.common.MemberTestUtils;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.member.dto.request.SetNicknameRequestDto;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 탈퇴")
    public void testRemoveMember() {
        // given
        Long memberId = 1L;
        Member member = MemberTestUtils.getMember(memberId, false);
        LocalDateTime originalDeregisteredAt = member.getDeregisteredAt();

        given(memberRepository.save(member)).willReturn(member);

        // when
        memberService.removeMember(member);

        // then
        LocalDateTime changedDeregisteredAt = member.getDeregisteredAt();
        Assertions.assertNotNull(changedDeregisteredAt);
        Assertions.assertNotEquals(changedDeregisteredAt, originalDeregisteredAt);
        Assertions.assertEquals(member.getId(), memberId);
    }

    @Test
    @DisplayName("닉네임 설정")
    public void testCreateNickname() {
        // given
        Long memberId = 1L;
        Member member = MemberTestUtils.getMember(memberId, false);

        SetNicknameRequestDto setNicknameRequestDto = new SetNicknameRequestDto();
        String changedNickname = "changedNickname";
        ReflectionTestUtils.setField(setNicknameRequestDto, "nickname", changedNickname);

        given(memberRepository.save(member)).willReturn(member);

        // when
        MockHttpSession session = new MockHttpSession();
        memberService.createNickname(session, member, setNicknameRequestDto);

        // then
        Assertions.assertEquals(member.getNickname(), changedNickname);
        Assertions.assertEquals(member.getId(), memberId);
        Assertions.assertNotNull(session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));
    }
}
