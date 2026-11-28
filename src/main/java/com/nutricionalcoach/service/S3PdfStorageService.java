package com.nutricionalcoach.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.Objects;
import java.util.Optional;

/**
 * AWS S3 PDF storage adapter activated when {@code aws.s3.enabled=true}.
 */
@Service
@ConditionalOnProperty(name = "aws.s3.enabled", havingValue = "true")
public class S3PdfStorageService implements PdfStorageService {

    private final S3Client s3Client;
    private final String bucketName;
    private final String region;

    @Autowired
    public S3PdfStorageService(
            S3Client s3Client,
            @Value("${aws.s3.bucket:nutricional-coach-pdfs}") String bucketName,
            @Value("${aws.region:eu-west-1}") String region) {
        this.s3Client = Objects.requireNonNull(s3Client, "s3Client must not be null");
        this.bucketName = bucketName;
        this.region = region;
    }

    @Override
    public String storePdf(String revisionId, byte[] pdfContent) {
        if (revisionId == null || revisionId.isBlank()) {
            throw new IllegalArgumentException("revisionId must not be blank");
        }
        if (pdfContent == null) {
            throw new IllegalArgumentException("pdfContent must not be null");
        }
        String key = buildObjectKey(revisionId);
        byte[] payload = pdfContent.clone();
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("application/pdf")
                .contentLength((long) payload.length)
                .build();
        s3Client.putObject(request, RequestBody.fromBytes(payload));
        return "s3://" + bucketName + "/" + key;
    }

    @Override
    public Optional<byte[]> loadPdf(String revisionId) {
        if (revisionId == null || revisionId.isBlank()) {
            return Optional.empty();
        }
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(buildObjectKey(revisionId))
                .build();
        try {
            ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(request);
            return Optional.of(response.asByteArray());
        } catch (NoSuchKeyException ex) {
            return Optional.empty();
        } catch (S3Exception ex) {
            if (ex.statusCode() == 404) {
                return Optional.empty();
            }
            throw ex;
        }
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getRegion() {
        return region;
    }

    public String buildObjectKey(String revisionId) {
        return "revisiones/revision-" + revisionId + ".pdf";
    }
}
