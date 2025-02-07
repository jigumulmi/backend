package com.jigumulmi.config.aws;

import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;

public class BaseConfig {

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
}
