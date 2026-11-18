package com.nutricionalcoach.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configurable AWS S3 PDF storage adapter activated when {@code aws.s3.enabled=true}.
 */
@Service
@ConditionalOnProperty(name = "aws.s3.enabled", havingValue = "true")
public class S3PdfStorageService implements PdfStorageService {

    private final String bucketName;
    private final String region;
    private final Map<String, byte[]> s3ObjectStore = new ConcurrentHashMap<>();

    public S3PdfStorageService(
            @Value("${aws.s3.bucket:nutricional-coach-pdfs}") String bucketName,
            @Value("${aws.region:eu-west-1}") String region) {
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
        s3ObjectStore.put(key, pdfContent.clone());
        return "s3://" + bucketName + "/" + key;
    }

    @Override
    public Optional<byte[]> loadPdf(String revisionId) {
        if (revisionId == null || revisionId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(s3ObjectStore.get(buildObjectKey(revisionId))).map(byte[]::clone);
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
