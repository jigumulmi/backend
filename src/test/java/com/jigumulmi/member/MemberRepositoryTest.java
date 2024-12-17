package com.jigumulmi.member;

import com.jigumulmi.member.domain.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("이메일로 회원 조회")
    void testFindMemberByEmail() {
        // given
        String email = "test@email.com";
        Member member = Member.builder()
            .email(email)
            .build();

        memberRepository.save(member);

        //when
        Member findMember = memberRepository.findByEmail(email).orElseThrow();

        // then
        Assertions.assertSame(member, findMember);
        Assertions.assertSame(member.getEmail(), findMember.getEmail());
    }
}
