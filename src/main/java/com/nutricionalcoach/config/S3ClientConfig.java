package com.nutricionalcoach.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

import java.net.URI;

/**
 * Configures the AWS SDK v2 {@link S3Client} when {@code aws.s3.enabled=true}.
 */
@Configuration
@ConditionalOnProperty(name = "aws.s3.enabled", havingValue = "true")
public class S3ClientConfig {

    @Bean(destroyMethod = "close")
    public S3Client s3Client(
            @Value("${aws.region:eu-west-1}") String region,
            @Value("${aws.s3.endpoint:}") String endpoint,
            @Value("${aws.s3.pathStyleAccess:false}") boolean pathStyleAccess,
            @Value("${aws.accessKeyId:}") String accessKeyId,
            @Value("${aws.secretAccessKey:}") String secretAccessKey) {
        String effectiveRegion = (region == null || region.isBlank()) ? "eu-west-1" : region.trim();
        S3ClientBuilder builder = S3Client.builder()
                .region(Region.of(effectiveRegion))
                .forcePathStyle(pathStyleAccess);

        if (endpoint != null && !endpoint.isBlank()) {
            builder.endpointOverride(URI.create(endpoint.trim()));
        }

        if (accessKeyId != null && !accessKeyId.isBlank()
                && secretAccessKey != null && !secretAccessKey.isBlank()) {
            builder.credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKeyId.trim(), secretAccessKey.trim())));
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.builder().build());
        }

        return builder.build();
    }
}
