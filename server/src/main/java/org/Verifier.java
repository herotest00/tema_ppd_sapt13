package org;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Verifier implements Runnable {

    private final Service service;
    private final Duration timeout;
    private final String pathToFile;

    public Verifier(Service service, Duration timeout, String pathToFile) {
        this.service = service;
        this.timeout = timeout;
        this.pathToFile = pathToFile;
    }

    @Override
    public void run() {
        try {
            BufferedWriter file = new BufferedWriter(new FileWriter(pathToFile));
            while (true) {
                Thread.sleep(timeout.toMillis());
                file.write(LocalDateTime.now() + ", " + service.validateData() + "\n");
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
