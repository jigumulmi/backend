package com.jigumulmi.place.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
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
public class Review extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer rating;
    @Column(length = 400)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    @JsonBackReference
    private Place place;

    @OneToMany(mappedBy = "review")
    @JsonManagedReference
    private List<ReviewReply> reviewReplyList = new ArrayList<>();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "review")
    @JsonManagedReference
    private List<ReviewReaction> reviewReactionList = new ArrayList<>();

    @Builder
    public Review(Integer rating, String content, Member member, Place place,
        List<ReviewReply> reviewReplyList, LocalDateTime deletedAt,
        List<ReviewReaction> reviewReactionList) {
        this.rating = rating;
        this.content = content;
        this.member = member;
        this.place = place;
        this.reviewReplyList = reviewReplyList;
        this.deletedAt = deletedAt;
        this.reviewReactionList = reviewReactionList;
    }

    public void updateReview(Integer rating, String content) {
        if (rating != null) {
            this.rating = rating;
        }
        if (content != null) {
            this.content = content;
        }
    }

    public void deleteReviewWithReplies() {
        this.deletedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}
