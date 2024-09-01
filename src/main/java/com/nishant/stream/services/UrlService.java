package com.nishant.stream.services;

import org.apache.logging.slf4j.SLF4JLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.Map;

@Service
public class UrlService {

    private Logger logger = LoggerFactory.getLogger(SLF4JLogger.class);
    private  final S3Client s3Client;
    private  final S3Presigner presigner;

    @Autowired
    public UrlService(S3Client s3Client, S3Presigner s3Presigner)
    {
         this.s3Client = s3Client;
         this.presigner = s3Presigner;
    }

    /* Create a presigned URL to use in a subsequent PUT request */
    public String createPresignedUrl(String bucket, String key) {

        logger.info("UrlService:createPresignedUrl execution Started");
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10)) // Set the expiration time for the URL
                .putObjectRequest(putObjectRequest)
                .build();

        logger.info("UrlService:createPresignedUrl execution Ended");
        return presigner.presignPutObject(presignRequest).url().toString();
    }
}




