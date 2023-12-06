package client;

import data.SerializableResultSet;
import gui.TableGUI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Scanner;

public class ClientThread extends Thread {

    Socket socket;
    ObjectInputStream ois;
    PrintWriter pw;

    public ClientThread(Socket s) throws IOException {
        socket = s;
        pw = new PrintWriter(socket.getOutputStream(), true);
        ois = new ObjectInputStream(socket.getInputStream());
    }

    public void run() {
        super.run();
        try {
            Scanner sc = new Scanner(System.in);
            System.out.print("테이블 이름을 입력해주세요 : ");
            String tableName = sc.next();
            pw.println("select * from " + tableName);
            SerializableResultSet rs = (SerializableResultSet) ois.readObject();
            TableGUI tableGUI = new TableGUI(pw, tableName);
            tableGUI.setTable(rs);
        } catch (IOException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }


}