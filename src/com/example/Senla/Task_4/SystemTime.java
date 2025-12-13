package com.example.Senla.Task_4;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;

public class SystemTime {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Введите число n");
        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt();

        SystemThreadClass tim = new SystemThreadClass(n);
        tim.start();
        Thread.sleep(10000);
        tim.stop();

    }
}
