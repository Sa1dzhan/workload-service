package com.gymcrm.workloadservice.integration;

import com.gymcrm.dto.workload.DurationRequestDto;
import com.gymcrm.dto.workload.DurationResponseDto;
import com.gymcrm.dto.workload.WorkloadRequestDto;
import com.gymcrm.workloadservice.entity.TrainerWorkload;
import com.gymcrm.workloadservice.repository.WorkloadRepository;
import com.gymcrm.workloadservice.service.WorkloadService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Testcontainers
public class WorkloadServiceIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private WorkloadService workloadService;

    @Autowired
    private WorkloadRepository workloadRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @AfterEach
    void tearDown() {
        mongoTemplate.getDb().drop();
    }

    @Test
    void testUpdateTrainerWorkload_AddNewWorkload() {
        WorkloadRequestDto request = new WorkloadRequestDto();
        request.setUsername("test.user");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setIsActive(true);
        request.setTrainingDate(LocalDate.of(2024, 7, 15));
        request.setTrainingDuration(60L);
        request.setActionType(WorkloadRequestDto.ActionType.ADD);

        workloadService.updateTrainerWorkload(request);

        TrainerWorkload savedWorkload = workloadRepository.findByUsername("test.user");
        assertNotNull(savedWorkload);
        assertEquals("Test", savedWorkload.getFirstName());
        assertEquals(60L, savedWorkload.getDurationForMonth(2024, 7));
    }

    @Test
    void testUpdateTrainerWorkload_DeleteWorkload() {
        WorkloadRequestDto addRequest = new WorkloadRequestDto();
        addRequest.setUsername("test.trainer");
        addRequest.setFirstName("Test");
        addRequest.setLastName("Trainer");
        addRequest.setIsActive(true);
        addRequest.setTrainingDate(LocalDate.of(2024, 7, 15));
        addRequest.setTrainingDuration(120L);
        addRequest.setActionType(WorkloadRequestDto.ActionType.ADD);
        workloadService.updateTrainerWorkload(addRequest);

        WorkloadRequestDto deleteRequest = new WorkloadRequestDto();
        deleteRequest.setUsername("test.trainer");
        deleteRequest.setFirstName("Test");
        deleteRequest.setLastName("Trainer");
        deleteRequest.setIsActive(true);
        deleteRequest.setTrainingDate(LocalDate.of(2024, 7, 15));
        deleteRequest.setTrainingDuration(30L);
        deleteRequest.setActionType(WorkloadRequestDto.ActionType.DELETE);

        workloadService.updateTrainerWorkload(deleteRequest);

        TrainerWorkload updatedWorkload = workloadRepository.findByUsername("test.trainer");
        assertNotNull(updatedWorkload);
        assertEquals(90L, updatedWorkload.getDurationForMonth(2024, 7));
    }

    @Test
    void testGetWorkloadDuration_Success() {
        WorkloadRequestDto addRequest = new WorkloadRequestDto();
        addRequest.setUsername("test.trainer");
        addRequest.setFirstName("Test");
        addRequest.setLastName("Trainer");
        addRequest.setIsActive(true);
        addRequest.setTrainingDate(LocalDate.of(2024, 7, 15));
        addRequest.setTrainingDuration(180L);
        addRequest.setActionType(WorkloadRequestDto.ActionType.ADD);
        workloadService.updateTrainerWorkload(addRequest);

        DurationRequestDto durationRequest = new DurationRequestDto();
        durationRequest.setUsername("test.trainer");
        durationRequest.setTrainingDate(LocalDate.of(2024, 7, 1));

        DurationResponseDto response = workloadService.getWorkloadDuration(durationRequest);

        assertNotNull(response);
        assertEquals("test.trainer", response.getUsername());
        assertEquals(180L, response.getDuration());
    }

    @Test
    void testGetWorkloadDuration_TrainerNotFound() {
        DurationRequestDto durationRequest = new DurationRequestDto();
        durationRequest.setUsername("unknown.trainer");
        durationRequest.setTrainingDate(LocalDate.of(2024, 7, 1));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            workloadService.getWorkloadDuration(durationRequest);
        });
        assertTrue(exception.getMessage().contains("not found"));
    }
}
