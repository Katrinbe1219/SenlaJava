package com.example.Senla.Task_3;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class ExchangeInfo {
    public static void main(String[] args) throws InterruptedException {

        LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>(3);

        Thread t1 = new Thread(()->{
            Integer item;
            while(!Thread.currentThread().isInterrupted()){

                try {
                    System.out.println("Потребитель ожидает данные");
                     item = queue.take();
                    System.out.println("Потребитель получил " + item);

                    Thread.sleep(500);
                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                }
            }


        });

        Thread t2 = new Thread(()->{
            Integer item;
            try {
                System.out.println("Поставщик ");
                while(!Thread.currentThread().isInterrupted()){

                    item = new java.util.Random().nextInt(900) + 100;
                    System.out.println("Поставщик отправил номер " + item);
                    queue.put(item);
                }

            }
            catch (InterruptedException e){
                Thread.currentThread().interrupt();

            }
        });

        t1.start();
        Thread.sleep(2000);
        t2.start();

        Thread.sleep(15000);
        t2.interrupt();
        t1.interrupt();

    }
}
