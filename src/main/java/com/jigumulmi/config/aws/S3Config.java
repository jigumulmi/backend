package com.jigumulmi.config.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    public Region getRegion() {
        return Region.of(region);
    }

    public AwsBasicCredentials getCredentials() {
        return AwsBasicCredentials.create(accessKey, secretKey);
    }

    public AwsCredentialsProvider getCredentialsProvider() {
        return StaticCredentialsProvider.create(getCredentials());
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
            .region(getRegion())
            .credentialsProvider(getCredentialsProvider())
            .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
            .region(getRegion())
            .credentialsProvider(getCredentialsProvider())
            .build();
    }
}
