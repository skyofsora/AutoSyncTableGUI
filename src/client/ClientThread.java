package client;

import data.SerializableResultSet;
import gui.TableGUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

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
            SerializableResultSet rs = (SerializableResultSet) ois.readObject();
            System.out.println("테이블 객체 수신 완료");
            System.out.println(rs.check);
            TableGUI tableGUI = new TableGUI(pw);
            tableGUI.setTable(rs);
        } catch (IOException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }


}