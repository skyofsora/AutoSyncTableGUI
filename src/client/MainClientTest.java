package client;


import java.net.Socket;

class MainClientTest extends Thread{
    String ip = "localhost";
    int port = 7777;

    @Override
    public void run() {
        super.run();
        try {
            Socket s = new Socket(ip, port);
            ClientThread clientThread = new ClientThread(s);
            clientThread.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new MainClient().start();
    }
}