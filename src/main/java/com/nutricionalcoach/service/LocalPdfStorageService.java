package com.nutricionalcoach.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default in-memory PDF storage implementation used when AWS S3 storage is disabled.
 */
@Service
@ConditionalOnProperty(name = "aws.s3.enabled", havingValue = "false", matchIfMissing = true)
public class LocalPdfStorageService implements PdfStorageService {

    private final Map<String, byte[]> pdfStore = new ConcurrentHashMap<>();

    @Override
    public String storePdf(String revisionId, byte[] pdfContent) {
        if (revisionId == null || revisionId.isBlank()) {
            throw new IllegalArgumentException("revisionId must not be blank");
        }
        if (pdfContent == null) {
            throw new IllegalArgumentException("pdfContent must not be null");
        }
        pdfStore.put(revisionId, pdfContent.clone());
        return "/api/v1/revisiones/" + revisionId + "/pdf";
    }

    @Override
    public Optional<byte[]> loadPdf(String revisionId) {
        if (revisionId == null || revisionId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(pdfStore.get(revisionId)).map(byte[]::clone);
    }
}
