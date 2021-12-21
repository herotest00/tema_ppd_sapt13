package org;

import java.time.Duration;

public class Verifier implements Runnable {

    private final IService service;
    private final Duration timeout;

    public Verifier(IService service, Duration timeout) {
        this.service = service;
        this.timeout = timeout;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(timeout.toMillis());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
