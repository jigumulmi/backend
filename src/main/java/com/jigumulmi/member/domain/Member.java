package com.jigumulmi.member.domain;

import com.jigumulmi.config.common.Timestamped;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Member extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;
    private String email;
    private Long kakaoUserId;

    @Builder
    public Member(String nickname, String email, Long kakaoUserId) {
        this.nickname = nickname;
        this.email = email;
        this.kakaoUserId = kakaoUserId;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
