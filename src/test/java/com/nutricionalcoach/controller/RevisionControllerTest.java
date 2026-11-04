package com.nutricionalcoach.controller;

import com.nutricionalcoach.dto.RevisionDTO;
import com.nutricionalcoach.service.RevisionService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RevisionControllerTest {

    @Test
    void listarRevisiones_retorna200ConLista() {
        RevisionService service = mock(RevisionService.class);
        RevisionDTO r1 = new RevisionDTO(
                "rev-1", "paciente-1", "med-1", "med-2",
                -1.5, -0.5, 0.5, null, null, null, null, LocalDate.of(2024, 7, 1));
        RevisionDTO r2 = new RevisionDTO(
                "rev-2", "paciente-1", "med-2", "med-3",
                -0.5, -0.2, 0.3, null, null, null, null, LocalDate.of(2024, 8, 1));
        when(service.listarRevisiones()).thenReturn(List.of(r1, r2));

        RevisionController controller = new RevisionController(service);

        HttpServletRequest request = mock(HttpServletRequest.class);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ResponseEntity<List<RevisionDTO>> respuesta = controller.listarRevisiones();

        assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody()).hasSize(2);
        assertThat(respuesta.getBody().get(0).getId()).isEqualTo("rev-1");
        assertThat(respuesta.getBody().get(1).getId()).isEqualTo("rev-2");
        verify(service, times(1)).listarRevisiones();
    }

    @Test
    void obtenerRevisionExistente_retorna200() {
        RevisionService service = mock(RevisionService.class);
        RevisionDTO dto = new RevisionDTO(
                "rev-1", "paciente-1", "med-1", "med-2",
                -1.5, -0.5, 0.5, null, null, null, null, LocalDate.of(2024, 7, 1));
        when(service.obtenerRevisionPorId("rev-1")).thenReturn(dto);

        RevisionController controller = new RevisionController(service);

        HttpServletRequest request = mock(HttpServletRequest.class);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ResponseEntity<RevisionDTO> respuesta = controller.obtenerRevisionPorId("rev-1");

        assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().getId()).isEqualTo("rev-1");
        verify(service, times(1)).obtenerRevisionPorId("rev-1");
    }

    @Test
    void obtenerRevisionInexistente_retorna404() {
        RevisionService service = mock(RevisionService.class);
        when(service.obtenerRevisionPorId("rev-999")).thenReturn(null);

        RevisionController controller = new RevisionController(service);

        HttpServletRequest request = mock(HttpServletRequest.class);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ResponseEntity<RevisionDTO> respuesta = controller.obtenerRevisionPorId("rev-999");

        assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
        verify(service, times(1)).obtenerRevisionPorId("rev-999");
    }

    @Test
    void crearRevision_retorna201ConLocation() {
        RevisionService service = mock(RevisionService.class);
        RevisionDTO entrada = new RevisionDTO(
                null, "paciente-1", "med-1", "med-2",
                -1.5, -0.5, 0.5, "ajuste", "más proteína", "más cardio",
                null, LocalDate.of(2024, 7, 1));
        RevisionDTO salida = new RevisionDTO(
                "rev-1", "paciente-1", "med-1", "med-2",
                -1.5, -0.5, 0.5, "ajuste", "más proteína", "más cardio",
                null, LocalDate.of(2024, 7, 1));
        when(service.crearRevision(any(RevisionDTO.class))).thenReturn(salida);

        RevisionController controller = new RevisionController(service);

        HttpServletRequest request = mock(HttpServletRequest.class);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ResponseEntity<RevisionDTO> respuesta = controller.crearRevision("paciente-1", entrada);

        assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().getId()).isEqualTo("rev-1");
        assertThat(respuesta.getHeaders().getLocation()).isNotNull();
        assertThat(respuesta.getHeaders().getLocation().toString()).contains("rev-1");
        verify(service, times(1)).crearRevision(any(RevisionDTO.class));
    }

    @Test
    void descargarPdfExistente_retorna200ConPdf() {
        RevisionService service = mock(RevisionService.class);
        when(service.generarPdfRevision("rev-1")).thenReturn(new byte[]{1, 2, 3, 4});

        RevisionController controller = new RevisionController(service);

        HttpServletRequest request = mock(HttpServletRequest.class);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ResponseEntity<byte[]> respuesta = controller.descargarPdf("rev-1");

        assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
        assertThat(respuesta.getBody()).isEqualTo(new byte[]{1, 2, 3, 4});
        assertThat(respuesta.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PDF);
        assertThat(respuesta.getHeaders().getContentDisposition()).isNotNull();
        verify(service, times(1)).generarPdfRevision("rev-1");
    }

    @Test
    void descargarPdfInexistente_retorna404() {
        RevisionService service = mock(RevisionService.class);
        when(service.generarPdfRevision("rev-999")).thenReturn(null);

        RevisionController controller = new RevisionController(service);

        HttpServletRequest request = mock(HttpServletRequest.class);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ResponseEntity<byte[]> respuesta = controller.descargarPdf("rev-999");

        assertThat(respuesta.getStatusCode().value()).isEqualTo(404);
        verify(service, times(1)).generarPdfRevision("rev-999");
    }
}
