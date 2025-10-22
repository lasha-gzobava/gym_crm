package org.example.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.training.TrainingEventRequest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageProducer {

    private final JmsTemplate jmsTemplate;
    private final MessageConverter messageConverter;

    public void sendTrainingEvent(TrainingEventRequest request) {
        try {
            jmsTemplate.setMessageConverter(messageConverter);
            log.info(" Sending TrainingEventRequest to queue: {}", request);
            jmsTemplate.convertAndSend("training.queue", request);
        } catch (Exception e) {
            log.error(" Failed to publish training event for trainer {}: {}", request.getUsername(), e.getMessage());
            throw new RuntimeException(e);
        }
    }
}