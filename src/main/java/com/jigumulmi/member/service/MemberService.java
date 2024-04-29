package com.jigumulmi.member.service;


import com.jigumulmi.config.security.UserDetailsImpl;
import com.jigumulmi.member.MemberRepository;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.member.dto.request.SetNicknameRequestDto;
import com.jigumulmi.member.dto.response.MemberDetailResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void removeMember(UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();
        memberRepository.delete(member);
    }

    @Transactional
    public void createNickname(UserDetailsImpl userDetails, SetNicknameRequestDto requestDto) {
        Member member = userDetails.getMember();
        member.updateNickname(requestDto.getNickname());
        memberRepository.save(member);
    }

    public MemberDetailResponseDto getUserDetail(UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();
        return new MemberDetailResponseDto(
            member.getCreatedAt(), member.getId(), member.getNickname(), member.getEmail()
        );
    }
}
