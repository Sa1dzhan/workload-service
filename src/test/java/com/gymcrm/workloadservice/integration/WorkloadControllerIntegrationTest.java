package com.gymcrm.workloadservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.dto.workload.DurationRequestDto;
import com.gymcrm.dto.workload.DurationResponseDto;
import com.gymcrm.dto.workload.WorkloadRequestDto;
import com.gymcrm.workloadservice.controller.WorkloadController;
import com.gymcrm.workloadservice.service.WorkloadService;
import com.gymcrm.workloadservice.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(WorkloadController.class)
@WithMockUser
public class WorkloadControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WorkloadService workloadService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void testUpdateTrainerWorkload_Success() throws Exception {
        WorkloadRequestDto request = new WorkloadRequestDto();
        request.setUsername("test.user");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setIsActive(true);
        request.setTrainingDate(LocalDate.of(2024, 5, 21));
        request.setTrainingDuration(60L);
        request.setActionType(WorkloadRequestDto.ActionType.ADD);

        doNothing().when(workloadService).updateTrainerWorkload(any(WorkloadRequestDto.class));

        mockMvc.perform(post("/api/v1/workload/update")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTrainerWorkload_Success() throws Exception {
        DurationRequestDto request = new DurationRequestDto();
        request.setUsername("test.user");
        request.setTrainingDate(LocalDate.of(2024, 5, 21));

        DurationResponseDto response = new DurationResponseDto();
        response.setUsername("test.user");
        response.setDuration(120L);

        when(workloadService.getWorkloadDuration(any(DurationRequestDto.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test.user"))
                .andExpect(jsonPath("$.duration").value(120L));
    }

    @Test
    void testGetTrainerWorkload_TrainerNotFound() throws Exception {
        DurationRequestDto request = new DurationRequestDto();
        request.setUsername("some.user");
        request.setTrainingDate(LocalDate.of(2024, 5, 21));

        when(workloadService.getWorkloadDuration(any(DurationRequestDto.class)))
                .thenThrow(new RuntimeException("Trainer with such username some.user not found"));

        mockMvc.perform(get("/api/v1/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
