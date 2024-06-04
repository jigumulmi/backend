package com.jigumulmi.place.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jigumulmi.config.common.Timestamped;
import com.jigumulmi.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    @OneToMany(mappedBy = "reviewReply")
    @JsonManagedReference
    private List<ReviewReplyReaction> reviewReplyReactionList = new ArrayList<>();

    @Builder
    public ReviewReply(String content, Review review, Member member,
        List<ReviewReplyReaction> reviewReplyReactionList) {
        this.content = content;
        this.review = review;
        this.member = member;
        this.reviewReplyReactionList = reviewReplyReactionList;
    }

    public void updateReviewReply(String content) {
        this.content = content;
    }
}
