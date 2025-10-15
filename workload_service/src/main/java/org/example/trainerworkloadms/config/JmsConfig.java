package org.example.trainerworkloadms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.trainerworkloadms.dto.TrainingEventRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import jakarta.jms.ConnectionFactory;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Configuration
@EnableJms
public class JmsConfig {

    @Value("${app.queue.training:training.queue}")
    private String trainingQueue;

    @Value("${app.queue.training.dlq:training.dlq}")
    private String deadLetterQueue;

    private final ApplicationContext context;

    public JmsConfig(ApplicationContext context) {
        this.context = context;
    }

    @PostConstruct
    public void logQueues() {
        log.info("📦 ActiveMQ Queues configured:");
        log.info("  • training queue: {}", trainingQueue);
        log.info("  • dead-letter queue: {}", deadLetterQueue);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setTypeIdMappings(Map.of(
                "TrainingEventRequest", TrainingEventRequest.class
        ));
        return converter;
    }


    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter,
            ObjectMapper objectMapper
    ) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrency("1-3");
        factory.setMessageConverter(messageConverter);

        // Inline DLQ error handler
        factory.setErrorHandler(t -> {
            log.error(" JMS listener failed: {}", t.getMessage(), t);
            try {
                var jmsTemplate = context.getBean(org.springframework.jms.core.JmsTemplate.class);
                String payload = objectMapper.writeValueAsString(Map.of(
                        "error", t.getMessage(),
                        "exception", t.getClass().getSimpleName(),
                        "timestamp", LocalDateTime.now().toString()
                ));
                jmsTemplate.convertAndSend(deadLetterQueue, payload);
                log.warn(" Routed failed message to DLQ: {}", deadLetterQueue);
            } catch (Exception e) {
                log.error(" Failed to send error to DLQ: {}", e.getMessage(), e);
            }
        });

        return factory;
    }
}
