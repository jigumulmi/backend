package com.jigumulmi.member.service;


import com.jigumulmi.member.MemberRepository;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.member.dto.request.SetNicknameRequestDto;
import com.jigumulmi.member.dto.response.MemberDetailResponseDto;
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

    public void createNickname(Member member, SetNicknameRequestDto requestDto) {
        member.updateNickname(requestDto.getNickname());
        memberRepository.save(member);
    }

    public MemberDetailResponseDto getUserDetail(Member member) {
        return new MemberDetailResponseDto(
            member.getCreatedAt(), member.getDeregisteredAt(), member.getId(), member.getNickname(),
            member.getEmail()
        );
    }
}
