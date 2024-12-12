package com.jigumulmi.member.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jigumulmi.config.common.Timestamped;
import com.jigumulmi.place.domain.PlaceLike;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;
    private String email;
    private Long kakaoUserId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime deregisteredAt;

    @ColumnDefault("false")
    private Boolean isAdmin;

    @OneToMany(mappedBy = "member")
    @JsonManagedReference
    private List<PlaceLike> placeLikeList = new ArrayList<>();

    @Builder
    public Member(String nickname, String email, Long kakaoUserId, LocalDateTime deregisteredAt,
        Boolean isAdmin, List<PlaceLike> placeLikeList) {
        this.nickname = nickname;
        this.email = email;
        this.kakaoUserId = kakaoUserId;
        this.deregisteredAt = deregisteredAt;
        this.isAdmin = (isAdmin != null) ? isAdmin : false;
        this.placeLikeList = placeLikeList != null ? placeLikeList : new ArrayList<>();
    }

    /**
     * 스웨거 또는 포스트맨으로 인증된 테스트 사용자
     */
    @Builder(builderMethodName = "testMemberBuilder", builderClassName="TestMemberBuilder")
    public Member(Long id) {
        this.id = id;
        this.nickname = "testMember";
        this.isAdmin = true;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void deregister() {
        this.deregisteredAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}
