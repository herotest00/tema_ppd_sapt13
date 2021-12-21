package org;

import org.domain.Spectacol;
import org.domain.Vanzare;
import org.json.JSONObject;
import org.operation.ServerOperation;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceProxy implements IService {
    private Socket clientSocket = null;
    private PrintWriter writer = null;
    private BufferedReader reader = null;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    ServiceProxy(String hostname, int port) {
        try {
            clientSocket = new Socket(hostname, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        OutputStream output = null;
        try {
            output = clientSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer = new PrintWriter(output);

        InputStream input = null;
        try {
            input = clientSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        reader = new BufferedReader(new InputStreamReader(input));
        executorService.execute(() -> {
            System.out.println("Hello world!");
        });
    }

    private void sendRequestToServer(ServerOperation operation, JSONObject data) {
        writer.println(new JSONObject(Map.ofEntries(
                Map.entry("operation", operation.name()),
                Map.entry("data", data)
        )));
    }

    @Override
    public List<Spectacol> getAllSpectacole() {

    }

    @Override
    public List<Integer> getAllLocuriDisponibile(Spectacol spectacol) {
        return null;
    }

    @Override
    public Vanzare rezerva(List<Integer> locuri) {
        return null;
    }
}
