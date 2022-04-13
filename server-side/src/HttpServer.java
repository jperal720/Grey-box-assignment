import java.net.ServerSocket;
import java.net.Socket;
import java.net.*;
import java.io.*;

public class HttpServer {
    private static HttpURLConnection connection;

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8080);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new RequestProcessor(clientSocket).run();
            }
        } catch (IOException e) {
        }
    }
}