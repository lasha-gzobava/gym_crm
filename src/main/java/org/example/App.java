package org.example;

import org.example.seed.StorageSeed;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("org.example");


        StorageSeed seeder = context.getBean(StorageSeed.class);



        context.close();
    }
}
