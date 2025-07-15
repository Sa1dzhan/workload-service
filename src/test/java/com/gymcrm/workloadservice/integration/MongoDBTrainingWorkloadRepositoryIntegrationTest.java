package com.gymcrm.workloadservice.integration;

import com.gymcrm.dto.workload.WorkloadRequestDto;
import com.gymcrm.workloadservice.entity.TrainerWorkload;
import com.gymcrm.workloadservice.entity.TrainerWorkloadDocument;
import com.gymcrm.workloadservice.mapper.TrainerWorkloadMapper;
import com.gymcrm.workloadservice.mapper.TrainerWorkloadMapperImpl;
import com.gymcrm.workloadservice.repository.WorkloadRepository;
import com.gymcrm.workloadservice.repository.impl.MongoDBTrainingWorkloadRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


@DataMongoTest
@Import({MongoDBTrainingWorkloadRepository.class, TrainerWorkloadMapperImpl.class})
public class MongoDBTrainingWorkloadRepositoryIntegrationTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private WorkloadRepository workloadRepository;

    @Autowired
    private TrainerWorkloadMapper mapper;

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(TrainerWorkloadDocument.class);
    }

    @Test
    void testSaveAndFindByUsername_Success() {
        TrainerWorkload workload = new TrainerWorkload();
        workload.setUsername("Mongo.User");
        workload.setFirstName("Mongo");
        workload.setLastName("User");
        workload.setStatus(true);
        workload.updateWorkload(LocalDate.of(2024, 1, 10), 90L, WorkloadRequestDto.ActionType.ADD);

        workloadRepository.save(workload);
        TrainerWorkload foundWorkload = workloadRepository.findByUsername("Mongo.User");

        assertNotNull(foundWorkload);
        assertEquals("Mongo.User", foundWorkload.getUsername());
        assertEquals("Mongo", foundWorkload.getFirstName());
        assertEquals(90L, foundWorkload.getDurationForMonth(2024, 1));
    }

    @Test
    void testFindByUsername_NotFound() {
        TrainerWorkload foundWorkload = workloadRepository.findByUsername("some.user");

        assertNull(foundWorkload);
    }
}
