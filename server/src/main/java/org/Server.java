package org;

import java.io.IOException;
import java.lang.ref.Cleaner;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket serverSocket = null;
    private ExecutorService executor = null;
    private Service service;
    private Cleaner cleaner = Cleaner.create();

    private final List<Socket> clients = new ArrayList<>();

    public List<Socket> getClients() {
        return clients;
    }

    public Server(int port, int maxClients, Service service) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        executor = Executors.newFixedThreadPool(maxClients + 1);
        this.service = service;
    }

    public void startServer(long miliseconds) {
        executor.execute(() -> {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    Worker worker = new Worker(clientSocket, this, service);
                    this.cleaner.register(this, worker::disconnect);
                    executor.execute(worker);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        // TODO: inchidere dupa interval de timp
        try {
            Thread.sleep(miliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdownNow();
    }
}
