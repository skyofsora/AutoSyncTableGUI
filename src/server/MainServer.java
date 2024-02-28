package server;

import sql.SQLConnect;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainServer {
    final int port = 7777;
    ServerSocket serverSocket;
    Socket socket;

    public MainServer() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("클라이언트의 연결 대기중");
            List<User> list = new ArrayList<>();
            while (true) {
                socket = serverSocket.accept();
                if (socket != null) {
                    ServerThread serverThread = new ServerThread(socket, list);
                    Thread tr = new Thread(serverThread);
                    tr.start();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            SQLConnect.getInstance().closeConnection();
        }
    }

    public static void main(String[] args) {
        new MainServer();
    }
}
