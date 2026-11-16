package com.nutricionalcoach.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:nutricionaldb_local_seed;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
@AutoConfigureMockMvc
@ActiveProfiles("local")
class DataInitializerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void seedsDemoDataWhenLocalProfileIsActive() throws Exception {
        mockMvc.perform(get("/api/v1/pacientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("pac-demo-1"))
                .andExpect(jsonPath("$[0].nombre").value("Carlos"));

        mockMvc.perform(get("/api/v1/pacientes/pac-demo-1/mediciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/api/v1/revisiones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("rev-demo-1"))
                .andExpect(jsonPath("$[0].pacienteId").value("pac-demo-1"));

        mockMvc.perform(get("/api/v1/revisiones/rev-demo-1/pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        mockMvc.perform(get("/api/v1/recordatorios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("rec-demo-1"))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }
}
