package com.jigumulmi.common;

import com.jigumulmi.config.JpaAuditingConfig;
import com.jigumulmi.config.TestQueryDslConfig;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DataJpaTest(showSql = false)
@Import({TestQueryDslConfig.class, JpaAuditingConfig.class})
public @interface RepositoryTest {

}
