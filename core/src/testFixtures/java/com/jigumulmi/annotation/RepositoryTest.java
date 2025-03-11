package com.jigumulmi.annotation;

import com.jigumulmi.config.JpaAuditingConfig;
import com.jigumulmi.config.querydsl.QueryDslConfig;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DataJpaTest(showSql = false, includeFilters = @ComponentScan.Filter(Repository.class))
@Import({QueryDslConfig.class, JpaAuditingConfig.class})
public @interface RepositoryTest {

}
