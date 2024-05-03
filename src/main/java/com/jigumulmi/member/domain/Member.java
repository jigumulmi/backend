package com.jigumulmi.member.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jigumulmi.config.common.Timestamped;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime deregisteredAt;

    @Builder
    public Member(String nickname, String email, Long kakaoUserId, LocalDateTime deregisteredAt) {
        this.nickname = nickname;
        this.email = email;
        this.kakaoUserId = kakaoUserId;
        this.deregisteredAt = deregisteredAt;
    }

    /**
     * 선택적으로 사용자 인증을 하는 api에서 인증되지 않은 요청의 경우 생성되는 가짜 사용자
     *
     * @param fakeId 가짜 사용자를 생성하기 위한 존재하지 않는 memberId
     */
    public Member(Long fakeId) {
        this.id = fakeId;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void deregister() {
        this.deregisteredAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}
