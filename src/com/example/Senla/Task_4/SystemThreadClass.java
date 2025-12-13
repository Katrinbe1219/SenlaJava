package com.example.Senla.Task_4;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SystemThreadClass {
    ScheduledExecutorService scheduler;
    ScheduledFuture<?> future;
    int interval;

    SystemThreadClass(int n){
        this.interval = n;
        scheduler = Executors.newScheduledThreadPool(1, r ->{
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
    }


    void start(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        future = scheduler.scheduleAtFixedRate(
                () -> {
                    String  curTime = LocalDateTime.now().format(formatter);
                    System.out.println(curTime);

                },
                0,
                this.interval,
                TimeUnit.SECONDS

        );
    }

    void stop(){
        if (future != null){
            future.cancel(true);
        }

        scheduler.shutdown();
    }
}
