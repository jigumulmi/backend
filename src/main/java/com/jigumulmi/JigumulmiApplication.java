package com.jigumulmi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class JigumulmiApplication {

    public static void main(String[] args) {
        SpringApplication.run(JigumulmiApplication.class, args);
    }

}
