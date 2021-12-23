package org;

import org.domain.Spectacol;
import org.domain.Vanzare;
import org.json.JSONArray;
import org.json.JSONObject;
import org.operation.ClientOperation;
import org.operation.ServerOperation;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServiceProxy implements IService {
    private Socket clientSocket = null;
    private PrintWriter writer = null;
    private BufferedReader reader = null;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final Map<ServerOperation, Lock> operationLockMap = new HashMap<>();
    private final Map<ServerOperation, Condition> operationConditionMap = new HashMap<>();
    private final Map<ServerOperation, JSONObject> operationResponseMap = new HashMap<>();


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
        writer = new PrintWriter(output, true);

        InputStream input = null;
        try {
            input = clientSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        reader = new BufferedReader(new InputStreamReader(input));
        executorService.execute(() -> {
            try {
                requestReceiver();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void requestReceiver() throws IOException {
        while (true) {
            String read = reader.readLine();
            System.out.println(read);
            JSONObject response = new JSONObject(read);
            ClientOperation clientOperation = ClientOperation.valueOf(response.getString("operation"));
            if (clientOperation == ClientOperation.EXIT) {
                clientSocket.close();
                break;
            }
            if (response.getBoolean("requestResponse")) {
                ServerOperation operation = ServerOperation.valueOf(response.getString("responseTo"));
                Lock lock = operationLockMap.get(operation);
                Condition condition = operationConditionMap.get(operation);
                lock.lock();
                operationResponseMap.put(operation, response);
                condition.signal();
                lock.unlock();
            }
        }
    }

    private JSONObject sendRequestToServer(ServerOperation operation, JSONObject data) {
        // TODO: might generate a concurrency problem
        if (!operationResponseMap.containsKey(operation)) {
            operationResponseMap.put(operation, null);
            operationLockMap.put(operation, new ReentrantLock());
            operationConditionMap.put(operation, operationLockMap.get(operation).newCondition());
        }
        operationResponseMap.put(operation, null);

        writer.println(new JSONObject(Map.ofEntries(
                Map.entry("operation", operation.name()),
                Map.entry("data", data)
        )));

        Lock lock = operationLockMap.get(operation);
        Condition condition = operationConditionMap.get(operation);
        System.out.println("Waiting for response");
        lock.lock();
        while (operationResponseMap.get(operation) == null) {
            try {
                condition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        lock.unlock();
        return operationResponseMap.get(operation);
    }

    @Override
    public List<Spectacol> getAllSpectacole() {
        JSONObject response = sendRequestToServer(ServerOperation.GET_ALL_SPECTACOLE, new JSONObject(Map.ofEntries(

        )));
        System.out.println("Send request to server");
        List<Spectacol> spectacols = new ArrayList<>();
        JSONArray array = response.getJSONArray("data");
        for (int i = 0; i < array.length(); i++) {
            spectacols.add(new Spectacol(array.getJSONObject(i)));
        }
        return spectacols;
    }

    @Override
    public List<Integer> getAllLocuriDisponibile(Spectacol spectacol) {
        JSONObject response = sendRequestToServer(ServerOperation.GET_ALL_LOCURI, new JSONObject(Map.ofEntries(
                Map.entry("spectacol", spectacol.toJson())
        )));
        List<Integer> locuri = new ArrayList<>();
        JSONArray array = response.getJSONArray("data");
        for (int i = 0; i < array.length(); i++) {
            locuri.add(array.getInt(i));
        }
        return locuri;
    }

    @Override
    public Vanzare rezerva(Spectacol spectacol, List<Integer> locuri) {
        JSONArray array = new JSONArray();
        for (Integer loc : locuri) {
            array.put(loc);
        }
        JSONObject response = sendRequestToServer(ServerOperation.REZERVA, new JSONObject(Map.ofEntries(
                Map.entry("spectacol", spectacol.toJson()),
                Map.entry("locuri", array)
        )));
        return new Vanzare(response.getJSONObject("data"));
    }

    public boolean isConnected() {
        return clientSocket.isConnected();
    }
}
