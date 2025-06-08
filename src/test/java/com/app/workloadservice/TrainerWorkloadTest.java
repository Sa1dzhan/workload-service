package com.app.workloadservice;

import com.app.workloadservice.entity.TrainerWorkload;
import com.app.workloadservice.util.ActionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TrainerWorkloadTest {

    private TrainerWorkload trainerWorkload;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        trainerWorkload = new TrainerWorkload();
        trainerWorkload.setUsername("testuser");
        trainerWorkload.setFirstName("John");
        trainerWorkload.setLastName("Doe");
        trainerWorkload.setStatus(true);

        testDate = LocalDate.of(2024, 1, 15);
    }

    @Test
    void testUpdateWorkload_AddAction() {
        long initialDuration = 100L;
        long additionalDuration = 50L;

        trainerWorkload.updateWorkload(testDate, initialDuration, ActionType.ADD);

        trainerWorkload.updateWorkload(testDate, additionalDuration, ActionType.ADD);

        long finalDuration = trainerWorkload.getDurationForMonth(2024, 1);
        assertEquals(initialDuration + additionalDuration, finalDuration);
    }

    @Test
    void testUpdateWorkload_DeleteAction() {
        long initialDuration = 100L;
        long reductionDuration = 30L;

        trainerWorkload.updateWorkload(testDate, initialDuration, ActionType.ADD);

        trainerWorkload.updateWorkload(testDate, reductionDuration, ActionType.DELETE);

        long finalDuration = trainerWorkload.getDurationForMonth(2024, 1);
        assertEquals(initialDuration - reductionDuration, finalDuration);
    }

    @Test
    void testUpdateWorkload_DeleteMoreThanAvailable() {
        long initialDuration = 50L;
        long reductionDuration = 100L;

        trainerWorkload.updateWorkload(testDate, initialDuration, ActionType.ADD);

        trainerWorkload.updateWorkload(testDate, reductionDuration, ActionType.DELETE);

        long finalDuration = trainerWorkload.getDurationForMonth(2024, 1);
        assertEquals(0L, finalDuration);
    }

    @Test
    void testUpdateWorkload_DifferentMonths() {
        LocalDate januaryDate = LocalDate.of(2024, 1, 15);
        LocalDate februaryDate = LocalDate.of(2024, 2, 15);

        long januaryDuration = 100L;
        long februaryDuration = 200L;

        trainerWorkload.updateWorkload(januaryDate, januaryDuration, ActionType.ADD);
        trainerWorkload.updateWorkload(februaryDate, februaryDuration, ActionType.ADD);

        assertEquals(januaryDuration, trainerWorkload.getDurationForMonth(2024, 1));
        assertEquals(februaryDuration, trainerWorkload.getDurationForMonth(2024, 2));
    }

    @Test
    void testUpdateWorkload_DifferentYears() {
        LocalDate date2024 = LocalDate.of(2024, 1, 15);
        LocalDate date2025 = LocalDate.of(2025, 1, 15);

        long duration2024 = 100L;
        long duration2025 = 200L;

        trainerWorkload.updateWorkload(date2024, duration2024, ActionType.ADD);
        trainerWorkload.updateWorkload(date2025, duration2025, ActionType.ADD);

        assertEquals(duration2024, trainerWorkload.getDurationForMonth(2024, 1));
        assertEquals(duration2025, trainerWorkload.getDurationForMonth(2025, 1));
    }

    @Test
    void testGetDurationForMonth_NonExistentMonth() {
        long duration = trainerWorkload.getDurationForMonth(2024, 5);

        assertEquals(0L, duration);
    }

    @Test
    void testGetDurationForMonth_CreatesYearAndMonth() {
        long duration = trainerWorkload.getDurationForMonth(2030, 11);

        assertEquals(0L, duration);
        assertTrue(trainerWorkload.getYears().containsKey(2030));
    }

    @Test
    void testUpdateWorkload_NullActionType() {
        long initialDuration = 100L;

        assertDoesNotThrow(() -> {
            trainerWorkload.updateWorkload(testDate, initialDuration, null);
        });

        assertEquals(0L, trainerWorkload.getDurationForMonth(2024, 2));
    }
}
