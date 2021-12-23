package org.component;

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
        BufferedWriter file = null;
        try {
            file = new BufferedWriter(new FileWriter(pathToFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (file != null) {
            try {
                while (true) {
                    if (service.validateData()) {
                        file.write(LocalDateTime.now() + ", corect\n");
                    }
                    else {
                        file.write(LocalDateTime.now() + ", incorect\n");
                    }
                    Thread.sleep(timeout.toMillis());
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
