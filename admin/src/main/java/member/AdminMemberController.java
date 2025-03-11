package member;

import com.jigumulmi.common.PagedResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import member.dto.AdminMemberListResponseDto.MemberDto;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "멤버 관리")
@RestController
@RequestMapping("/admin/member")
@RequiredArgsConstructor
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @Operation(summary = "멤버 리스트 조회")
    @GetMapping("")
    public ResponseEntity<PagedResponseDto<MemberDto>> getMemberList(
        @ParameterObject Pageable pageable) {
        PagedResponseDto<MemberDto> memberList = adminMemberService.getMemberList(pageable);
        return ResponseEntity.ok().body(memberList);
    }
}
