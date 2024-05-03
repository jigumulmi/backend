package com.jigumulmi.config.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@AuthenticationPrincipal(expression = "T(com.jigumulmi.config.security.SecurityUtils).checkRequiredAuthenticationPrincipal(#this)")
public @interface RequiredAuthUser {

}
