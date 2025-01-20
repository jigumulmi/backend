package com.jigumulmi.config.logging;

import com.jigumulmi.member.vo.MemberRole;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Getter
@Builder
public class LoggingVO {

    @Default
    private String createdAt = LocalDateTime.now().toString();
    private String requestId;
    private String requestMethod;
    private String requestUri;
    private String requestQueryParam;
    private int httpStatusCode;
    private long responseTime;
    private Long memberId;
    private MemberRole memberRole;

}
