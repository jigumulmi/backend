package com.jigumulmi.config.security;

import com.jigumulmi.member.domain.Member;
import java.time.LocalDateTime;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.test.util.ReflectionTestUtils;

public class MockSecurityContextFactory implements WithSecurityContextFactory<MockMember> {

    @Override
    public SecurityContext createSecurityContext(MockMember annotation) {
        LocalDateTime parsedDeregisteredAt = annotation.deregisteredAt().isEmpty() ? null
            : LocalDateTime.parse(annotation.deregisteredAt());

        Member mockMember = Member.builder()
            .nickname(annotation.nickname())
            .email(annotation.email())
            .kakaoUserId(annotation.kakaoUserId())
            .isAdmin(annotation.isAdmin())
            .deregisteredAt(parsedDeregisteredAt)
            .build();
        ReflectionTestUtils.setField(mockMember, "id", annotation.id());
        ReflectionTestUtils.setField(mockMember, "createdAt",
            LocalDateTime.parse(annotation.createdAt()));

        UserDetails userDetails = new UserDetailsImpl(mockMember);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
            userDetails.getAuthorities());

        TestSecurityContextHolder.setAuthentication(authentication);

        return TestSecurityContextHolder.getContext();
    }

}
