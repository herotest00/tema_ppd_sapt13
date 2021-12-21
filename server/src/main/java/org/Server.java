package org;

import java.io.IOException;
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
        executor = Executors.newFixedThreadPool(maxClients);
        this.service = service;
    }

    public void startServer() {
        // TODO: inchidere dupa interval de timp
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                executor.execute(new Worker(clientSocket, this, service));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
