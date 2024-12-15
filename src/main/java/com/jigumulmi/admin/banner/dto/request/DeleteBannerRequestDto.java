package com.jigumulmi.admin.banner.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeleteBannerRequestDto {

    @NotEmpty
    private List<Long> bannerIdList;
}
