//package org.example.trainerworkloadms.messaging;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.jms.core.JmsTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//public class TestSender implements CommandLineRunner {
//
//    private final JmsTemplate jmsTemplate;
//
//    public TestSender(JmsTemplate jmsTemplate) {
//        this.jmsTemplate = jmsTemplate;
//    }
//
//    @Override
//    public void run(String... args) {
//        jmsTemplate.convertAndSend("training.queue", "Hello from Rezo 🚀");
//        System.out.println(" Sent test message to training.queue");
//    }
//}
