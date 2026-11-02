package app;

import com.sun.net.httpserver.HttpServer;
import tracker.TrackerHandler;
import tracker.TrackerService;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        TrackerService service = new TrackerService();
        TrackerHandler handler = new TrackerHandler(service);

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        server.createContext("/projects", handler);
        server.createContext("/tasks", handler);
        server.createContext("/ping", new PingHandler());

        server.start();
        System.out.println("Сервер запущен: http://localhost:" + PORT);
    }
}