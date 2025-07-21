package com.gymcrm.workloadservice.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.dto.message.WorkloadMessage;
import com.gymcrm.dto.workload.DurationRequestDto;
import com.gymcrm.dto.workload.WorkloadRequestDto;
import com.gymcrm.workloadservice.entity.TrainerWorkloadDocument;
import com.gymcrm.workloadservice.util.Constants;
import com.gymcrm.workloadservice.util.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@WithMockUser
public class WorkloadComponentTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");
    @Container
    static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.12-management");

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    @MockBean
    private JwtUtil jwtUtil;

    @TestConfiguration
    static class ComponentTestConfig {
        @Bean
        public Queue updateQueue() {
            return new Queue(Constants.QUEUE_UPDATE, false);
        }
    }

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(TrainerWorkloadDocument.class);
    }

    @Test
    void testRESTWorkloadUpdate() throws Exception {
        WorkloadRequestDto addRequest = new WorkloadRequestDto();
        addRequest.setUsername("api.user");
        addRequest.setFirstName("Api");
        addRequest.setLastName("User");
        addRequest.setIsActive(true);
        addRequest.setTrainingDate(LocalDate.of(2025, 8, 1));
        addRequest.setTrainingDuration(60L);
        addRequest.setActionType(WorkloadRequestDto.ActionType.ADD);

        // send the update request to the REST controller
        mockMvc.perform(post("/api/v1/workload/update")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());

        // request to get the duration
        DurationRequestDto durationRequest = new DurationRequestDto();
        durationRequest.setUsername("api.user");
        durationRequest.setTrainingDate(LocalDate.of(2025, 8, 10));

        // GET request
        mockMvc.perform(get("/api/v1/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(durationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("api.user"))
                .andExpect(jsonPath("$.duration").value(60L));
    }

    @Test
    void testRabbitMQWorkloadUpdate() throws Exception {
        WorkloadRequestDto payload = new WorkloadRequestDto();
        payload.setUsername("message.user");
        payload.setFirstName("Message");
        payload.setLastName("User");
        payload.setIsActive(true);
        payload.setTrainingDate(LocalDate.of(2025, 9, 5));
        payload.setTrainingDuration(90L);
        payload.setActionType(WorkloadRequestDto.ActionType.ADD);

        WorkloadMessage<WorkloadRequestDto> message = new WorkloadMessage<>();
        message.setUsername("message.user");
        message.setTransactionId(UUID.randomUUID().toString());
        message.setPayload(payload);

        // send the message to the RabbitMQ queue
        rabbitTemplate.convertSendAndReceive(Constants.QUEUE_UPDATE, message);

        // request to get the duration
        DurationRequestDto durationRequest = new DurationRequestDto();
        durationRequest.setUsername("message.user");
        durationRequest.setTrainingDate(LocalDate.of(2025, 9, 15));

        // GET request to verify
        mockMvc.perform(get("/api/v1/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(durationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("message.user"))
                .andExpect(jsonPath("$.duration").value(90L));
    }
}
