package com.nishant.stream.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class awsConfig {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.profile}")
    private String profile;

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(ProfileCredentialsProvider
                        .builder()
                        .profileName(profile)
                        .build())
                .build();
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(ProfileCredentialsProvider
                        .builder()
                        .profileName(profile)
                        .build())
                .build();
    }

    @Bean
    public SqsClient sqsClient(){
        return SqsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(ProfileCredentialsProvider
                        .builder()
                        .profileName(profile)
                        .build())
                .build();
    }
}
