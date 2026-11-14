package com.nutricionalcoach;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutricionalcoach.model.Recordatorio;
import com.nutricionalcoach.repository.RecordatorioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NutricionalCoachApplicationTest {

    private static final String FRONTEND_ORIGIN = "http://localhost:4200";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RecordatorioRepository recordatorioRepository;

    @Test
    void contextLoadsAndExecutesFullE2EFlowInH2() throws Exception {
        // 1. Listar pacientes activos inicialmente (con CORS :4200)
        mockMvc.perform(get("/api/v1/pacientes")
                        .header("Origin", FRONTEND_ORIGIN))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", FRONTEND_ORIGIN));

        // 2. Alta de paciente (POST /api/v1/pacientes)
        String pacientePayload = """
                {
                  "nombre": "Ana",
                  "apellidos": "García",
                  "email": "ana.garcia@example.com",
                  "telefono": "600123456",
                  "objetivo": "Recomposición corporal"
                }
                """;

        MvcResult createPacienteResult = mockMvc.perform(post("/api/v1/pacientes")
                        .header("Origin", FRONTEND_ORIGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pacientePayload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Access-Control-Allow-Origin", FRONTEND_ORIGIN))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.nombre").value("Ana"))
                .andExpect(jsonPath("$.fechaAlta").isNotEmpty())
                .andReturn();

        JsonNode pacienteNode = objectMapper.readTree(createPacienteResult.getResponse().getContentAsString());
        String pacienteId = pacienteNode.get("id").asText();

        // 3. Detalle y listado de paciente activo (GET /api/v1/pacientes/{id} y GET /api/v1/pacientes)
        mockMvc.perform(get("/api/v1/pacientes/" + pacienteId)
                        .header("Origin", FRONTEND_ORIGIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pacienteId))
                .andExpect(jsonPath("$.email").value("ana.garcia@example.com"));

        mockMvc.perform(get("/api/v1/pacientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(pacienteId))
                .andExpect(jsonPath("$[0].nombre").value("Ana"));

        // 4. Registrar dos mediciones biométricas y listarlas (POST y GET /api/v1/pacientes/{id}/mediciones)
        String medicion1Payload = """
                {
                  "fecha": "2026-10-01",
                  "pesoKg": 72.5,
                  "grasaPct": 22.0,
                  "masaMuscularKg": 52.0,
                  "cinturaCm": 76.0
                }
                """;
        MvcResult med1Result = mockMvc.perform(post("/api/v1/pacientes/" + pacienteId + "/mediciones")
                        .header("Origin", FRONTEND_ORIGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(medicion1Payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Access-Control-Allow-Origin", FRONTEND_ORIGIN))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();
        String med1Id = objectMapper.readTree(med1Result.getResponse().getContentAsString()).get("id").asText();

        String medicion2Payload = """
                {
                  "fecha": "2026-11-01",
                  "pesoKg": 70.5,
                  "grasaPct": 20.0,
                  "masaMuscularKg": 53.2,
                  "cinturaCm": 73.5
                }
                """;
        MvcResult med2Result = mockMvc.perform(post("/api/v1/pacientes/" + pacienteId + "/mediciones")
                        .header("Origin", FRONTEND_ORIGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(medicion2Payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();
        String med2Id = objectMapper.readTree(med2Result.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(get("/api/v1/pacientes/" + pacienteId + "/mediciones")
                        .header("Origin", FRONTEND_ORIGIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        // 5. Crear revisión mensual, consultarla y descargar PDF (POST /pacientes/{id}/revisiones, GET /revisiones/{id}/pdf)
        String revisionPayload = String.format("""
                {
                  "medicionAnteriorId": "%s",
                  "medicionActualId": "%s",
                  "deltaPeso": -2.0,
                  "deltaGrasa": -2.0,
                  "deltaMusculo": 1.2,
                  "comentariosAjuste": "Excelente evolución en el primer mes",
                  "ajustesDieta": "Mantener 2100 kcal y subir proteína a 140g",
                  "ajustesEntreno": "Añadir cuarta sesión de fuerza torso/pierna"
                }
                """, med1Id, med2Id);

        MvcResult revResult = mockMvc.perform(post("/api/v1/pacientes/" + pacienteId + "/revisiones")
                        .header("Origin", FRONTEND_ORIGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(revisionPayload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Access-Control-Allow-Origin", FRONTEND_ORIGIN))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.pacienteId").value(pacienteId))
                .andReturn();
        String revisionId = objectMapper.readTree(revResult.getResponse().getContentAsString()).get("id").asText();

        MvcResult pdfResult = mockMvc.perform(get("/api/v1/revisiones/" + revisionId + "/pdf")
                        .header("Origin", FRONTEND_ORIGIN))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", FRONTEND_ORIGIN))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andReturn();

        byte[] pdfBytes = pdfResult.getResponse().getContentAsByteArray();
        assertThat(pdfBytes).isNotEmpty();
        assertThat(new String(pdfBytes, 0, 4)).isEqualTo("%PDF");

        // 6. Recordatorios pendientes y actualización de feedback (GET y PATCH /api/v1/recordatorios)
        Recordatorio recordatorio = new Recordatorio(
                null,
                pacienteId,
                LocalDate.of(2026, 12, 1),
                "REVISION_MENSUAL",
                "PENDIENTE",
                null
        );
        recordatorio.setId("rec-e2e-1");
        recordatorioRepository.save(recordatorio);

        mockMvc.perform(get("/api/v1/recordatorios")
                        .header("Origin", FRONTEND_ORIGIN))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", FRONTEND_ORIGIN))
                .andExpect(jsonPath("$[0].id").value("rec-e2e-1"))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));

        mockMvc.perform(patch("/api/v1/recordatorios/rec-e2e-1")
                        .header("Origin", FRONTEND_ORIGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"feedbackPaciente\": \"Me siento con más energía en los entrenos\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", FRONTEND_ORIGIN))
                .andExpect(jsonPath("$.feedbackPaciente").value("Me siento con más energía en los entrenos"));

        // 7. Soft-delete del paciente (DELETE /api/v1/pacientes/{id})
        mockMvc.perform(delete("/api/v1/pacientes/" + pacienteId)
                        .header("Origin", FRONTEND_ORIGIN))
                .andExpect(status().isNoContent())
                .andExpect(header().string("Access-Control-Allow-Origin", FRONTEND_ORIGIN));

        mockMvc.perform(get("/api/v1/pacientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
