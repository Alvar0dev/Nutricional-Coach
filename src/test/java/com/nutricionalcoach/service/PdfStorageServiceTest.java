package com.nutricionalcoach.service;

import com.nutricionalcoach.model.Revision;
import com.nutricionalcoach.repository.RevisionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PdfStorageServiceTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(LocalPdfStorageService.class, S3PdfStorageService.class);

    @Test
    void localStorageStoresAndLoadsPdfBytes() {
        LocalPdfStorageService storageService = new LocalPdfStorageService();
        byte[] content = "%PDF-1.4-local".getBytes(StandardCharsets.UTF_8);

        String location = storageService.storePdf("rev-1", content);

        assertThat(location).isEqualTo("/api/v1/revisiones/rev-1/pdf");
        assertThat(storageService.loadPdf("rev-1")).contains(content);
        assertThat(storageService.loadPdf("missing")).isEmpty();
    }

    @Test
    void s3StorageStoresAndFormatsS3UriWithConfiguredBucket() {
        S3PdfStorageService storageService = new S3PdfStorageService("nutricional-coach-pdfs", "eu-west-1");
        byte[] content = "%PDF-1.4-s3".getBytes(StandardCharsets.UTF_8);

        String uri = storageService.storePdf("rev-1", content);

        assertThat(uri).isEqualTo("s3://nutricional-coach-pdfs/revisiones/revision-rev-1.pdf");
        assertThat(storageService.getBucketName()).isEqualTo("nutricional-coach-pdfs");
        assertThat(storageService.getRegion()).isEqualTo("eu-west-1");
        assertThat(storageService.loadPdf("rev-1")).contains(content);
    }

    @Test
    void springSelectsLocalByDefaultAndS3WhenEnabled() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(PdfStorageService.class);
            assertThat(context.getBean(PdfStorageService.class)).isInstanceOf(LocalPdfStorageService.class);
        });

        contextRunner
                .withPropertyValues("aws.s3.enabled=true", "aws.s3.bucket=custom-bucket", "aws.region=eu-central-1")
                .run(context -> {
                    assertThat(context).hasSingleBean(PdfStorageService.class);
                    PdfStorageService bean = context.getBean(PdfStorageService.class);
                    assertThat(bean).isInstanceOf(S3PdfStorageService.class);
                    S3PdfStorageService s3Bean = (S3PdfStorageService) bean;
                    assertThat(s3Bean.getBucketName()).isEqualTo("custom-bucket");
                    assertThat(s3Bean.getRegion()).isEqualTo("eu-central-1");
                });
    }

    @Test
    void revisionServiceDelegatesPdfPersistenceToStorageService() {
        RevisionRepository revisionRepository = mock(RevisionRepository.class);
        PdfStorageService pdfStorageService = mock(PdfStorageService.class);

        Revision revision = new Revision();
        revision.setId("rev-1");
        revision.setPacienteId("pac-1");
        revision.setFechaRevision(LocalDate.of(2026, 11, 18));
        revision.setDeltaPeso(-1.2);
        revision.setDeltaGrasa(-1.0);
        revision.setDeltaMusculo(0.5);
        revision.setAjustesDieta("2200 kcal");
        revision.setAjustesEntreno("Fuerza 4 días");
        revision.setComentariosAjuste("Buen progreso");

        byte[] cachedBytes = "%PDF-cached".getBytes(StandardCharsets.UTF_8);
        when(revisionRepository.findById("rev-1")).thenReturn(Optional.of(revision));
        when(pdfStorageService.loadPdf("rev-1"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(cachedBytes));
        when(pdfStorageService.storePdf(eq("rev-1"), any(byte[].class)))
                .thenReturn("/api/v1/revisiones/rev-1/pdf");

        RevisionServiceImpl service = new RevisionServiceImpl(revisionRepository, pdfStorageService);

        byte[] firstCall = service.generarPdfRevision("rev-1");
        assertThat(firstCall).isNotEmpty();
        assertThat(revision.getPdfUrl()).isEqualTo("/api/v1/revisiones/rev-1/pdf");
        verify(pdfStorageService, times(1)).storePdf(eq("rev-1"), any(byte[].class));

        byte[] secondCall = service.generarPdfRevision("rev-1");
        assertThat(secondCall).isEqualTo(cachedBytes);
        verify(pdfStorageService, times(1)).storePdf(eq("rev-1"), any(byte[].class));
    }
}
