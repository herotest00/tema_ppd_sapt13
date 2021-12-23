package org.component;

import java.io.IOException;
import java.lang.ref.Cleaner;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.Duration;
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
    private List<Worker> workers = new ArrayList<>();

    public Server(int port, int maxClients, Service service) {
        try {
            serverSocket = new ServerSocket(port);
            service.getAllSpectacole();
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
        executor.execute(() -> {
            while (this.sacrificialObject != null) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Accepted client");
                    Worker worker = new Worker(clientSocket, this, service);
                    workers.add(worker);
                    executor.execute(worker);
                }
                catch (SocketException ex) {
                    break;
                }
                catch (IOException e) {
                    if (!e.getMessage().equals("Socket is closed"))
                        e.printStackTrace();
                    break;
                }
            }
        });
        executor.execute(new IntegrityManager(service, Duration.ofSeconds(5), "logs.log"));
        try {
            Thread.sleep(miliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        workers.forEach(worker -> {
            worker.disconnect();
            worker.closeClientSocket();
        });
        executor.shutdownNow();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Closed stuff");
    }
}
