//package org.example.trainerworkloadms.messaging;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.jms.annotation.JmsListener;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class TestListener {
//
//    @JmsListener(destination = "training.queue")
//    public void receive(String message) {
//        log.info(" Received: {}", message);
//        throw new RuntimeException(" Force fail to trigger DLQ");
//    }
//}
