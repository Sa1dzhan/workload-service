package com.app.workloadservice;

import com.app.workloadservice.dto.DurationRequestDto;
import com.app.workloadservice.dto.DurationResponseDto;
import com.app.workloadservice.dto.WorkloadRequestDto;
import com.app.workloadservice.entity.TrainerWorkload;
import com.app.workloadservice.repository.WorkloadRepository;
import com.app.workloadservice.service.impl.WorkloadServiceImpl;
import com.app.workloadservice.util.ActionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkloadServiceImplTest {

    @Mock
    private WorkloadRepository repository;

    @InjectMocks
    private WorkloadServiceImpl workloadService;

    private WorkloadRequestDto workloadRequest;
    private DurationRequestDto durationRequest;
    private TrainerWorkload existingTrainer;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDate.of(2024, 1, 15);

        workloadRequest = new WorkloadRequestDto();
        workloadRequest.setUsername("testuser");
        workloadRequest.setFirstName("John");
        workloadRequest.setLastName("Doe");
        workloadRequest.setIsActive(true);
        workloadRequest.setTrainingDate(testDate);
        workloadRequest.setTrainingDuration(100L);
        workloadRequest.setActionType(ActionType.ADD);

        durationRequest = new DurationRequestDto();
        durationRequest.setUsername("testuser");
        durationRequest.setTrainingDate(testDate);

        existingTrainer = new TrainerWorkload();
        existingTrainer.setUsername("testuser");
        existingTrainer.setFirstName("John");
        existingTrainer.setLastName("Doe");
        existingTrainer.setStatus(true);
    }

    @Test
    void testUpdateTrainerWorkload_ExistingTrainer() {
        when(repository.getTrainer("testuser")).thenReturn(existingTrainer);

        workloadService.updateTrainerWorkload(workloadRequest);

        verify(repository).getTrainer("testuser");
        verify(repository).saveTrainer(any(TrainerWorkload.class));
    }

    @Test
    void testUpdateTrainerWorkload_NewTrainer() {
        when(repository.getTrainer("testuser")).thenReturn(null);

        workloadService.updateTrainerWorkload(workloadRequest);

        verify(repository).getTrainer("testuser");
        verify(repository).saveTrainer(any(TrainerWorkload.class));
    }

    @Test
    void testUpdateTrainerWorkload_RepositoryException() {
        when(repository.getTrainer(anyString())).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            workloadService.updateTrainerWorkload(workloadRequest);
        });

        assertTrue(exception.getMessage().contains("Database error"));
    }

    @Test
    void testGetWorkloadDuration_ExistingTrainer() {
        existingTrainer.updateWorkload(testDate, 150L, ActionType.ADD);
        when(repository.getTrainer("testuser")).thenReturn(existingTrainer);

        DurationResponseDto response = workloadService.getWorkloadDuration(durationRequest);

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals(150L, response.getDuration());
        verify(repository).getTrainer("testuser");
    }

    @Test
    void testGetWorkloadDuration_TrainerNotFound() {
        when(repository.getTrainer("testuser")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            workloadService.getWorkloadDuration(durationRequest);
        });

        assertTrue(exception.getMessage().contains("Trainer with such username testuser not found"));
    }

    @Test
    void testGetWorkloadDuration_NoWorkloadData() {
        when(repository.getTrainer("testuser")).thenReturn(existingTrainer);

        DurationResponseDto response = workloadService.getWorkloadDuration(durationRequest);

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals(0L, response.getDuration());
    }

    @Test
    void testUpdateTrainerWorkload_UpdatesTrainerProperties() {
        TrainerWorkload outdatedTrainer = new TrainerWorkload();
        outdatedTrainer.setUsername("testuser");
        outdatedTrainer.setFirstName("OldFirst");
        outdatedTrainer.setLastName("OldLast");
        outdatedTrainer.setStatus(false);

        when(repository.getTrainer("testuser")).thenReturn(outdatedTrainer);

        workloadService.updateTrainerWorkload(workloadRequest);

        verify(repository).saveTrainer(argThat(trainer ->
                "John".equals(trainer.getFirstName()) &&
                        "Doe".equals(trainer.getLastName()) &&
                        trainer.getStatus().equals(true)
        ));
    }

    @Test
    void testUpdateTrainerWorkload_DeleteAction() {
        workloadRequest.setActionType(ActionType.DELETE);
        existingTrainer.updateWorkload(testDate, 200L, ActionType.ADD);
        when(repository.getTrainer("testuser")).thenReturn(existingTrainer);

        workloadService.updateTrainerWorkload(workloadRequest);

        verify(repository).saveTrainer(any(TrainerWorkload.class));
    }
}
