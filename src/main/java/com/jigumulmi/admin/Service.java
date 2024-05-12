package com.jigumulmi.admin;

import com.jigumulmi.admin.dto.request.GetMemberListRequestDto;
import com.jigumulmi.member.MemberRepository;
import com.jigumulmi.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class Service {

    private final int DEFAULT_PAGE_SIZE = 10;
    private final MemberRepository memberRepository;

    public List<Member> getMemberList(GetMemberListRequestDto requestDto) {
        Pageable pageable = PageRequest.of(requestDto.getPage(), DEFAULT_PAGE_SIZE,
            Sort.by(requestDto.getDirection(), "id"));

        Page<Member> all = memberRepository.findAll(pageable);
        return all.getContent();
    }

}
