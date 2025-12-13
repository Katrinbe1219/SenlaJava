package com.example.Senla.Task_1;

public class ThreadChecking {
    Object lock = new Object();
    Object another = new Object();




    void running() throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            try {
                System.out.println("Поток запущен и уходит в спячку");
                Thread.sleep(4000);
                System.out.println("Поток проснулся");

                synchronized (another){
                    System.out.println("Получил доступ к another " + Thread.currentThread().getState());
                }

                synchronized (lock){
                    lock.wait();
                    System.out.println("Получил доступ после wait " + Thread.currentThread().getState());
                }



            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Сработала ошибка прерывания");

            }


        });

        System.out.println("Создан " + thread1.getState());
        thread1.start();
        System.out.println("Запущен " + thread1.getState());

        synchronized (another){
            Thread.sleep(3000);
            System.out.println("Another захвачен другим потоком, а основной поток еще спит: " + thread1.getState());
        }

        Thread.sleep(3000);

        synchronized (lock){
            System.out.println("Lock захвачен из main, состояние основного потока: " + thread1.getState());
            lock.notify();
            Thread.sleep(3000);
            System.out.println("Только сработал notify, состояние основного потока:  " + thread1.getState());
        }


        Thread.sleep(2000);

        System.out.println("Последнее состояние " + thread1.getState());

    }
}
