package com.jigumulmi.config.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = MockSecurityContextFactory.class)
public @interface MockMember {

    long id() default 1L;
    String nickname() default "testNickname";
    String email() default "test@email.com";
    long kakaoUserId() default 123L;
    boolean isAdmin() default false;
    String createdAt() default "2024-03-29T00:00:00";
    String deregisteredAt() default "";
}
