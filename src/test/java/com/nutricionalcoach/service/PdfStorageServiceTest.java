package com.nutricionalcoach.service;

import com.nutricionalcoach.config.S3ClientConfig;
import com.nutricionalcoach.model.Revision;
import com.nutricionalcoach.repository.RevisionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

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
            .withUserConfiguration(LocalPdfStorageService.class, S3ClientConfig.class, S3PdfStorageService.class);

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
    void s3StorageUploadsAndLoadsPdfUsingS3Client() throws Exception {
        S3Client s3Client = mock(S3Client.class);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        byte[] content = "%PDF-1.4-s3".getBytes(StandardCharsets.UTF_8);
        S3PdfStorageService storageService = new S3PdfStorageService(s3Client, "nutricional-coach-pdfs", "eu-west-1");

        String uri = storageService.storePdf("rev-1", content);

        ArgumentCaptor<PutObjectRequest> putCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);
        verify(s3Client, times(1)).putObject(putCaptor.capture(), bodyCaptor.capture());

        assertThat(uri).isEqualTo("s3://nutricional-coach-pdfs/revisiones/revision-rev-1.pdf");
        assertThat(putCaptor.getValue().bucket()).isEqualTo("nutricional-coach-pdfs");
        assertThat(putCaptor.getValue().key()).isEqualTo("revisiones/revision-rev-1.pdf");
        assertThat(putCaptor.getValue().contentType()).isEqualTo("application/pdf");
        assertThat(putCaptor.getValue().contentLength()).isEqualTo(content.length);
        assertThat(bodyCaptor.getValue().contentStreamProvider().newStream().readAllBytes()).isEqualTo(content);

        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class)))
                .thenReturn(ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), content));

        Optional<byte[]> loaded = storageService.loadPdf("rev-1");
        assertThat(loaded).contains(content);

        ArgumentCaptor<GetObjectRequest> getCaptor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client, times(1)).getObjectAsBytes(getCaptor.capture());
        assertThat(getCaptor.getValue().bucket()).isEqualTo("nutricional-coach-pdfs");
        assertThat(getCaptor.getValue().key()).isEqualTo("revisiones/revision-rev-1.pdf");
    }

    @Test
    void s3StorageReturnsEmptyWhenObjectMissingOrNotFound() {
        S3Client s3Client = mock(S3Client.class);
        S3PdfStorageService storageService = new S3PdfStorageService(s3Client, "nutricional-coach-pdfs", "eu-west-1");

        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class)))
                .thenThrow(NoSuchKeyException.builder().message("missing").build())
                .thenThrow(S3Exception.builder().statusCode(404).message("not found").build());

        assertThat(storageService.loadPdf("missing-1")).isEmpty();
        assertThat(storageService.loadPdf("missing-2")).isEmpty();
        assertThat(storageService.loadPdf(" ")).isEmpty();
    }

    @Test
    void springSelectsLocalByDefaultAndWiresS3ClientWithEndpointWhenEnabled() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(PdfStorageService.class);
            assertThat(context.getBean(PdfStorageService.class)).isInstanceOf(LocalPdfStorageService.class);
            assertThat(context).doesNotHaveBean(S3Client.class);
        });

        contextRunner
                .withPropertyValues(
                        "aws.s3.enabled=true",
                        "aws.s3.bucket=custom-bucket",
                        "aws.region=eu-central-1",
                        "aws.s3.endpoint=http://localhost:14566",
                        "aws.s3.pathStyleAccess=true",
                        "aws.accessKeyId=test",
                        "aws.secretAccessKey=test"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(PdfStorageService.class);
                    assertThat(context).hasSingleBean(S3Client.class);
                    S3Client s3Client = context.getBean(S3Client.class);
                    assertThat(s3Client.serviceClientConfiguration().endpointOverride())
                            .hasValueSatisfying(uri -> assertThat(uri.toString()).isEqualTo("http://localhost:14566"));
                    assertThat(s3Client.serviceClientConfiguration().region().id()).isEqualTo("eu-central-1");

                    PdfStorageService bean = context.getBean(PdfStorageService.class);
                    assertThat(bean).isInstanceOf(S3PdfStorageService.class);
                    S3PdfStorageService s3Bean = (S3PdfStorageService) bean;
                    assertThat(s3Bean.getBucketName()).isEqualTo("custom-bucket");
                    assertThat(s3Bean.getRegion()).isEqualTo("eu-central-1");
                });
    }

    @Test
    void springWiresDefaultCredentialsProviderWhenAccessKeysAreBlank() {
        contextRunner
                .withPropertyValues(
                        "aws.s3.enabled=true",
                        "aws.s3.bucket=prod-bucket",
                        "aws.region=eu-west-1",
                        "aws.s3.endpoint=",
                        "aws.s3.pathStyleAccess=false",
                        "aws.accessKeyId=",
                        "aws.secretAccessKey="
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(S3Client.class);
                    S3Client s3Client = context.getBean(S3Client.class);
                    assertThat(s3Client.serviceClientConfiguration().endpointOverride()).isEmpty();
                    assertThat(s3Client.serviceClientConfiguration().region().id()).isEqualTo("eu-west-1");
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
