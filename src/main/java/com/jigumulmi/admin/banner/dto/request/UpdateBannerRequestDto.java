package com.jigumulmi.admin.banner.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBannerRequestDto extends CreateBannerRequestDto {

    @JsonIgnore
    private MultipartFile outerImage;
    @JsonIgnore
    private MultipartFile innerImage;
}
