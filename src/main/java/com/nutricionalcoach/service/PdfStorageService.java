package com.nutricionalcoach.service;

import java.util.Optional;

/**
 * Abstraction for storing and retrieving generated revision PDF documents.
 * Supports local in-memory storage by default and AWS S3 bucket storage when enabled.
 */
public interface PdfStorageService {

    /**
     * Persists the generated PDF bytes for a given revision.
     *
     * @param revisionId revision identifier
     * @param pdfContent generated PDF bytes
     * @return storage path or URI where the PDF can be referenced
     */
    String storePdf(String revisionId, byte[] pdfContent);

    /**
     * Retrieves previously stored PDF bytes for a given revision, if present.
     *
     * @param revisionId revision identifier
     * @return Optional containing the stored PDF bytes, or empty if absent
     */
    Optional<byte[]> loadPdf(String revisionId);
}
