package org.example.application;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Main {
    public static void main(String[] args) {

            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(KafkaConfiguration.class);
            MessageSender sender = (MessageSender) context.getBean("messageSender");


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // docker container stop -> sigterm -> this hook
            System.out.println("Закрывается контекст");
            context.close();
        }));  // ← Правильно: одна скобка закрывает addShutdownHook

            try {
                java.lang.Thread.currentThread().join();
            }catch(Exception e){
                e.printStackTrace();
            }

    }
}
