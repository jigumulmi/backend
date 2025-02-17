package com.jigumulmi.member;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jigumulmi.common.MemberTestUtils;
import com.jigumulmi.common.annotation.ControllerTest;
import com.jigumulmi.config.security.MockMember;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.member.dto.request.KakaoAuthorizationRequestDto;
import com.jigumulmi.member.dto.request.SetNicknameRequestDto;
import com.jigumulmi.member.dto.response.KakaoAuthResponseDto;
import com.jigumulmi.member.dto.response.MemberDetailResponseDto;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@ControllerTest(MemberController.class)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("카카오 인증")
    public void testKakaoAuthorize() throws Exception {
        // given
        KakaoAuthorizationRequestDto kakaoAuthorizationRequestDto = new KakaoAuthorizationRequestDto();
        ReflectionTestUtils.setField(kakaoAuthorizationRequestDto, "code", "testCode");
        ReflectionTestUtils.setField(kakaoAuthorizationRequestDto, "redirectUrl", "testUrl");

        KakaoAuthResponseDto responseDto = KakaoAuthResponseDto.builder()
            .hasRegistered(false)
            .nickname("testNickname")
            .build();
        given(memberService.kakaoAuthorize(any(KakaoAuthorizationRequestDto.class),
            any(HttpSession.class))).willReturn(responseDto);

        // when
        ResultActions perform = mockMvc.perform(
            post("/member/oauth/kakao/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(kakaoAuthorizationRequestDto))
        );

        // then
        perform
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(responseDto)))
            .andDo(print());
    }

    @Test
    @DisplayName("로그아웃")
    @MockMember
    public void testLogout() throws Exception {
        // given
        MockHttpSession session = new MockHttpSession();

        // when
        ResultActions perform = mockMvc.perform(
            post("/member/logout")
                .with(csrf())
                .session(session)
        );

        // then
        perform
            .andExpect(status().isCreated())
            .andDo(print());
    }

    @Test
    @DisplayName("회원 탈퇴")
    @MockMember
    public void testDeregister() throws Exception {
        // given
        MockHttpSession session = new MockHttpSession();
        Member member = MemberTestUtils.getMemberFromSecurityContext();

        willDoNothing().given(memberService).deregister(session, member);

        // when
        ResultActions perform = mockMvc.perform(
            post("/member/deregister")
                .with(csrf())
                .session(session)
        );

        // then
        perform
            .andExpect(status().isCreated())
            .andDo(print());
    }


    @Test
    @DisplayName("닉네임 수정(생성)")
    @MockMember
    public void testSetNickname() throws Exception {
        // given
        Member member = MemberTestUtils.getMemberFromSecurityContext();

        SetNicknameRequestDto setNicknameRequestDto = new SetNicknameRequestDto();
        ReflectionTestUtils.setField(setNicknameRequestDto, "nickname", "newNickname");

        willDoNothing().given(memberService)
            .setNickname(any(HttpSession.class), eq(member), any(SetNicknameRequestDto.class));

        // when
        ResultActions perform = mockMvc.perform(
            put("/member/nickname")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(setNicknameRequestDto))
        );

        // then
        perform
            .andExpect(status().isCreated())
            .andDo(print());

    }

    @Test
    @DisplayName("유저 상세 정보 조회")
    @MockMember
    public void testGetUserDetail() throws Exception {
        // given
        Member member = MemberTestUtils.getMemberFromSecurityContext();

        MemberDetailResponseDto responseDto = MemberDetailResponseDto.from(member);
        given(memberService.getUserDetail(member)).willReturn(responseDto);

        // when
        ResultActions perform = mockMvc.perform(
            get("/member")
                .with(csrf())
        );

        // then
        perform
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(responseDto)))
            .andDo(print());

    }
}
