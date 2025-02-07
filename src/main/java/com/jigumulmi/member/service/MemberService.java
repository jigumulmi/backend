package com.jigumulmi.member.service;


import com.jigumulmi.config.security.UserDetailsServiceImpl;
import com.jigumulmi.member.MemberRepository;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.member.dto.request.SetNicknameRequestDto;
import com.jigumulmi.member.dto.response.MemberDetailResponseDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void removeMember(Member member) {
        member.deregister();
        memberRepository.save(member);
    }

    public void createNickname(HttpSession session, Member member,
        SetNicknameRequestDto requestDto) {
        member.updateNickname(requestDto.getNickname());
        memberRepository.save(member);

        UserDetailsServiceImpl.setSecurityContextAndSession(member, session);
    }

    public MemberDetailResponseDto getUserDetail(Member member) {
        return MemberDetailResponseDto.builder()
            .id(member.getId())
            .nickname(member.getNickname())
            .email(member.getEmail())
            .isAdmin(member.getIsAdmin())
            .createdAt(member.getCreatedAt())
            .deregisteredAt(member.getDeregisteredAt())
            .build();
    }
}
