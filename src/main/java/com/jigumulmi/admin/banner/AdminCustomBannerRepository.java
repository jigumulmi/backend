package com.jigumulmi.admin.banner;


import com.jigumulmi.admin.banner.dto.request.BannerPlaceMappingRequestDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AdminCustomBannerRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final JPAQueryFactory queryFactory;

    public void insertBannerPlace(Long bannerId, BannerPlaceMappingRequestDto requestDto) {
        String sql = "INSERT INTO banner_place_mapping (banner_id, place_id) " +
            "VALUES (:bannerId, :placeId)";

        SqlParameterSource[] batch = requestDto.getPlaceIdList().stream()
            .map(placeId -> new MapSqlParameterSource()
                .addValue("bannerId", bannerId)
                .addValue("placeId", placeId))
            .toArray(SqlParameterSource[]::new);

        jdbcTemplate.batchUpdate(sql, batch);
    }
}
