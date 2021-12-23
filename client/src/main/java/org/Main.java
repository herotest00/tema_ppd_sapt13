package org;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    static int clients = 5;

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(clients);
        for (int i = 0; i < clients; i++) {
            executorService.execute(new Client());
        }
        try {
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
