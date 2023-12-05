package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {
    final int port = 7777;
    ServerSocket serverSocket;
    Socket socket;

    public MainServer() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("클라이언트의 연결 대기중");
            socket = serverSocket.accept();
            System.out.println("연결 완료");
            ServerThread serverThread = new ServerThread(socket);
            Thread tr = new Thread(serverThread);
            tr.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new MainServer();
    }
}
