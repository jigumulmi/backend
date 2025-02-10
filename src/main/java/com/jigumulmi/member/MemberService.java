package com.jigumulmi.member;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.jigumulmi.config.security.UserDetailsServiceImpl;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.member.dto.request.KakaoAuthorizationRequestDto;
import com.jigumulmi.member.dto.request.SetNicknameRequestDto;
import com.jigumulmi.member.dto.response.KakaoAuthResponseDto;
import com.jigumulmi.member.dto.response.MemberDetailResponseDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberManager memberManager;

    public KakaoAuthResponseDto kakaoAuthorize(KakaoAuthorizationRequestDto requestDto,
        HttpSession session) throws JsonProcessingException {
        String kakaoAccessToken = memberManager.getKakaoAccessToken(requestDto);
        String kakaoEmail = memberManager.getKakaoUserEmail(kakaoAccessToken);

        return memberManager.getOrCreateMember(session, kakaoEmail, kakaoAccessToken);
    }

    public void logout(HttpSession session) {
        UserDetailsServiceImpl.clearSecurityContextAndSession(session);
    }

    public void deregister(HttpSession session, Member member) {
        memberManager.deleteMember(member);
        memberManager.unlinkKakao(member);
        UserDetailsServiceImpl.clearSecurityContextAndSession(session);
    }

    public void setNickname(HttpSession session, Member member,
        SetNicknameRequestDto requestDto) {
        memberManager.updateNickname(member, requestDto.getNickname());
        UserDetailsServiceImpl.setSecurityContextAndSession(member, session);
    }

    public MemberDetailResponseDto getUserDetail(Member member) {
        return MemberDetailResponseDto.from(member);
    }
}
