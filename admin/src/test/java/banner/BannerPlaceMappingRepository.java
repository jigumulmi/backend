package banner;

import com.jigumulmi.banner.domain.BannerPlaceMapping;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerPlaceMappingRepository extends JpaRepository<BannerPlaceMapping, Long> {

    List<BannerPlaceMapping> findAllByBannerId(Long bannerId);
}
