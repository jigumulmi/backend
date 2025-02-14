package com.jigumulmi.member.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jigumulmi.common.Timestamped;
import com.jigumulmi.place.domain.PlaceLike;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"nickname"})})
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> oEffectiveClass = o instanceof HibernateProxy
            ? ((HibernateProxy) o).getHibernateLazyInitializer()
            .getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
            ? ((HibernateProxy) this).getHibernateLazyInitializer()
            .getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        Member member = (Member) o;
        return getId() != null && Objects.equals(getId(), member.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
            ? ((HibernateProxy) this).getHibernateLazyInitializer()
            .getPersistentClass().hashCode() : getClass().hashCode();
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void deregister() {
        this.deregisteredAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}
