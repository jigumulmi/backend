package com.jigumulmi.member.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberRole {

    ADMIN("ROLE_ADMIN"),
    GENERAL("ROLE_GENERAL")
    ;

    private final String value;

    public static MemberRole getRole(Boolean isAdmin) {
        if (isAdmin) {
            return ADMIN;
        } else {
            return GENERAL;
        }
    }
}
