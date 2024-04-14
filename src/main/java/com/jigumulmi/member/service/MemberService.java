package com.jigumulmi.member.service;


import com.jigumulmi.config.security.UserDetailsImpl;
import com.jigumulmi.member.MemberRepository;
import com.jigumulmi.member.domain.Member;
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
}
