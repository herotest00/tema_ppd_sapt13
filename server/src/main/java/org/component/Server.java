package org.component;

import java.io.IOException;
import java.lang.ref.Cleaner;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final List<Socket> clients = new ArrayList<>();
    private ServerSocket serverSocket = null;
    private ExecutorService executor = null;
    private final Service service;
    private final Cleaner cleaner = Cleaner.create();
    private Object sacrificialObject = new Object();

    public Server(int port, int maxClients, Service service) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        executor = Executors.newFixedThreadPool(maxClients + 1);
        this.service = service;
    }

    public List<Socket> getClients() {
        return clients;
    }

    public void startServer(long miliseconds) {
//        service.getAllSpectacole();
//        System.out.println("YES start server");
            while (this.sacrificialObject != null) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Accepted client");
                    service.getAllSpectacole();
                    System.out.println("YES accept client");
                    Worker worker = new Worker(clientSocket, this, service);
                    this.cleaner.register(this.sacrificialObject, worker::disconnect);
                    executor.execute(worker);
                } catch (IOException e) {
                    if (!e.getMessage().equals("Socket closed"))
                        e.printStackTrace();
                }
            }
        // TODO: inchidere dupa interval de timp
        try {
            Thread.sleep(miliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.sacrificialObject = null;
        executor.shutdownNow();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Closed stuff");
    }
}
