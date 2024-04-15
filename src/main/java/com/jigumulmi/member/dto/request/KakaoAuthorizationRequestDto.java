package com.jigumulmi.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class KakaoAuthorizationRequestDto {
    @NotBlank
    private String code;
}
