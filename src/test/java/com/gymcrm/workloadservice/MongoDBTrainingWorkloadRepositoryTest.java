package com.gymcrm.workloadservice;

import com.gymcrm.workloadservice.entity.TrainerWorkload;
import com.gymcrm.workloadservice.repository.impl.MongoDBTrainingWorkloadRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@Testcontainers
class MongoDBTrainingWorkloadRepositoryTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    private MongoDBTrainingWorkloadRepository repository;

    private TrainerWorkload trainer;

    @BeforeEach
    void setUp() {
        repository = new MongoDBTrainingWorkloadRepository(mongoTemplate);
        trainer = new TrainerWorkload();
        trainer.setUsername("test.trainer");
        trainer.setFirstName("Test");
        trainer.setLastName("Trainer");
        trainer.setStatus(true);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(TrainerWorkload.class);
    }

    @Test
    void testSaveAndFindByUsername_NewTrainer() {
        repository.save(trainer);

        TrainerWorkload found = repository.findByUsername("test.trainer");

        assertNotNull(found);
        assertEquals("test.trainer", found.getUsername());
        assertEquals("Test", found.getFirstName());
    }

    @Test
    void testSave_UpdateExistingTrainer() {
        mongoTemplate.save(trainer);

        trainer.setFirstName("Updated");
        repository.save(trainer);

        TrainerWorkload found = repository.findByUsername("test.trainer");

        assertNotNull(found);
        assertEquals("Updated", found.getFirstName());
    }

    @Test
    void testFindByUsername_TrainerNotFound() {
        TrainerWorkload found = repository.findByUsername("non.existent.trainer");
        assertNull(found);
    }
}
