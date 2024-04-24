package com.jigumulmi.place.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jigumulmi.config.common.Timestamped;
import com.jigumulmi.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Review extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer rating;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    @JsonBackReference
    private Restaurant restaurant;

    @OneToMany(mappedBy = "review")
    @JsonManagedReference
    private List<ReviewReply> reviewReplyList = new ArrayList<>();

    @Builder
    public Review(Integer rating, String content, Member member, Restaurant restaurant,
        List<ReviewReply> reviewReplyList) {
        this.rating = rating;
        this.content = content;
        this.member = member;
        this.restaurant = restaurant;
        this.reviewReplyList = reviewReplyList;
    }
}
