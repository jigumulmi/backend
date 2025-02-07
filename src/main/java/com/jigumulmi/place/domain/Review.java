package com.jigumulmi.place.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jigumulmi.common.Timestamped;
import com.jigumulmi.member.domain.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
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

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    @JsonManagedReference
    private List<ReviewImage> reviewImageList = new ArrayList<>();

    @Builder
    public Review(Integer rating, String content, Member member, Place place,
        List<ReviewReply> reviewReplyList, LocalDateTime deletedAt,
        List<ReviewImage> reviewImageList) {
        this.rating = rating;
        this.content = content;
        this.member = member;
        this.place = place;
        this.reviewReplyList = reviewReplyList != null ? reviewReplyList : new ArrayList<>();
        this.deletedAt = deletedAt;
        this.reviewImageList = reviewImageList != null ? reviewImageList : new ArrayList<>();
    }

    public void updateReview(Integer rating, String content, List<ReviewImage> newReviewImageList,
        List<ReviewImage> trashReviewImageList) {
        if (rating != null) {
            this.rating = rating;
        }
        if (content != null) {
            this.content = content;
        }
        this.reviewImageList.addAll(newReviewImageList);
        this.reviewImageList.removeAll(trashReviewImageList);
    }

    public void deleteReviewWithReplies() {
        this.deletedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    public void addReviewImageList(List<ReviewImage> reviewImageList) {
        this.reviewImageList.addAll(reviewImageList);
    }
}
