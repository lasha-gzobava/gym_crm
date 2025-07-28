package org.example;

import org.example.seed.StorageSeed;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    public static void main(String[] args) {
        // Create Spring context from your config class
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("org.example");

        // Trigger the seeding manually (optional if @PostConstruct already fires)
        StorageSeed seeder = context.getBean(StorageSeed.class);
        // seeder.seedDatabase(); ← optional if you want to call it manually

        // Keep context alive or close depending on your use case
        context.close();
    }
}
