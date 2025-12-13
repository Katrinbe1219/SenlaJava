package com.example.Senla.Task_2;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TwoThreads {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService ex = Executors.newSingleThreadExecutor();

        Thread t1 = new Thread(()-> {
            ex.submit(()->{
                System.out.println("T1");
            });
        });

        Thread t2 = new Thread(()-> {
            ex.submit(()->{
                System.out.println("T2");
            });
        });

        t1.start();

        t2.start();
        t1.join();
        t2.join();
        ex.shutdown();
    }
}
