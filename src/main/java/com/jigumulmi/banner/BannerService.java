package com.jigumulmi.banner;

import com.jigumulmi.banner.dto.response.BannerResponseDto;
import com.jigumulmi.banner.repository.BannerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;

    public List<BannerResponseDto> getBannerList() {
        return bannerRepository.findAllByIsActiveTrue().stream()
            .map(BannerResponseDto::from).toList();
    }
}
