package com.gymcrm.workloadservice.integration;

import com.gymcrm.dto.message.WorkloadMessage;
import com.gymcrm.dto.message.WorkloadResponseMessage;
import com.gymcrm.dto.workload.WorkloadRequestDto;
import com.gymcrm.workloadservice.service.WorkloadService;
import com.gymcrm.workloadservice.util.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
@Testcontainers
public class WorkloadConsumerIntegrationTest {

    @Container
    static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.12-management");

    @TestConfiguration
    static class RabbitTestConfig {
        @Bean
        public Queue updateQueue() {
            return new Queue(Constants.QUEUE_UPDATE, false);
        }
    }

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private WorkloadService workloadService;

    @Test
    void testProcessWorkloadUpdate_Success() {
        WorkloadRequestDto payload = new WorkloadRequestDto();
        payload.setUsername("Rabbit.User");
        payload.setFirstName("Rabbit");
        payload.setLastName("User");
        payload.setIsActive(true);
        payload.setTrainingDate(LocalDate.of(2025, 1, 1));
        payload.setTrainingDuration(45L);
        payload.setActionType(WorkloadRequestDto.ActionType.ADD);

        WorkloadMessage<WorkloadRequestDto> message = new WorkloadMessage<>();
        message.setUsername("Rabbit.User");
        message.setTransactionId(UUID.randomUUID().toString());
        message.setPayload(payload);

        doNothing().when(workloadService).updateTrainerWorkload(any(WorkloadRequestDto.class));

        WorkloadResponseMessage response = (WorkloadResponseMessage) rabbitTemplate.convertSendAndReceive(Constants.QUEUE_UPDATE, message);

        assertNotNull(response);
        assertEquals(WorkloadResponseMessage.WorkloadStatus.SUCCESS, response.getStatus());
        verify(workloadService, times(1))
                .updateTrainerWorkload(any(WorkloadRequestDto.class));
    }
}
