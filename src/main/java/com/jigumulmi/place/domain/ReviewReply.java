package com.jigumulmi.place.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.jigumulmi.config.common.Timestamped;
import com.jigumulmi.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class ReviewReply extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 400)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    @JsonBackReference
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public ReviewReply(String content, Review review, Member member) {
        this.content = content;
        this.review = review;
        this.member = member;
    }
}
