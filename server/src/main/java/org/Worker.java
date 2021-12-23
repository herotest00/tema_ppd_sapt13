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
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Worker implements Runnable {
    Map<ServerOperation, Function<JSONObject, JSONObject>> commandList = Map.ofEntries(
            Map.entry(ServerOperation.GET_ALL_LOCURI, this::getAllLocuriHandle),
            Map.entry(ServerOperation.GET_ALL_SPECTACOLE, this::getAllSpectacoleHandle),
            Map.entry(ServerOperation.REZERVA, this::rezervaHandle)
    );

    private Socket clientSocket;
    private PrintWriter writer;
    private Service service;

    public Worker(Socket clientSocket, Server server, Service service) {
        synchronized (server) {
            server.getClients().add(clientSocket);
        }
        this.clientSocket = clientSocket;
        OutputStream output = null;
        try {
            output = clientSocket.getOutputStream();
            this.writer = new PrintWriter(output, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.service = service;
    }

    @Override
    public void run() {
        InputStream input = null;
        try {
            input = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            while (true) {
                JSONObject request = new JSONObject(reader.readLine());
                ServerOperation operation = ServerOperation.valueOf(request.getString("operation"));
                JSONObject response = commandList.get(operation).apply(request.getJSONObject("data"));
                response.put("requestResponse", true);
                response.put("responseTo", operation);
                writer.println(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void disconnect() {
        writer.println(Map.ofEntries(
                Map.entry("operation", ClientOperation.EXIT),
                Map.entry("data", "")
        ));
    }

    private JSONObject getAllLocuriHandle(JSONObject jsonObject) {
        List<Integer> locuri = service.getAllLocuriDisponibile(new Spectacol(jsonObject.getJSONObject("spectacol")));
        JSONArray locuriJson = new JSONArray();
        locuri.forEach(locuriJson::put);
        return new JSONObject(Map.ofEntries(
                Map.entry("operation", ClientOperation.RESPONSE),
                Map.entry("data", locuriJson),
                Map.entry("requestResponse", false)
        ));
    }

    private JSONObject getAllSpectacoleHandle(JSONObject jsonObject) {
        List<Spectacol> spectacole = service.getAllSpectacole();
        JSONArray spectacoleJson = new JSONArray();
        spectacole.forEach(x -> spectacoleJson.put(x.toJson()));
        return new JSONObject(Map.ofEntries(
                Map.entry("operation", ClientOperation.RESPONSE),
                Map.entry("data", spectacoleJson),
                Map.entry("requestResponse", false)
        ));
    }

    private JSONObject rezervaHandle(JSONObject jsonObject) {
        JSONArray locuriJson = jsonObject.getJSONArray("locuri");
        List<Integer> locuri = new ArrayList<>();
        for (int i = 0; i < locuriJson.length(); i++) {
            locuri.add(locuriJson.getInt(i));
        }
        Vanzare vanzare = service.rezerva(new Spectacol(jsonObject.getJSONObject("spectacol")), locuri);
        return new JSONObject(Map.ofEntries(
                Map.entry("operation", ClientOperation.RESPONSE),
                Map.entry("data", vanzare.toJson()),
                Map.entry("requestResponse", false)
        ));
    }
}
