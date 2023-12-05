package client;

import gui.TableGUI;

import java.net.Socket;

public class MainClient {
    String ip = "localhost";
    int port = 7777;

    public MainClient() {
        try {
            Socket s = new Socket(ip, port);
            ClientThread clientThread = new ClientThread(s);
            clientThread.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new MainClient();
    }
}