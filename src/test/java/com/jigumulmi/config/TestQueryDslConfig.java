package com.jigumulmi.config;

import com.jigumulmi.admin.banner.AdminCustomBannerRepository;
import com.jigumulmi.config.querydsl.QueryDslConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@TestConfiguration
public class TestQueryDslConfig extends QueryDslConfig {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Bean
    public AdminCustomBannerRepository adminCustomBannerRepository() {
        return new AdminCustomBannerRepository(jdbcTemplate, queryFactory());
    }
}
