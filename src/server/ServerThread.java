package server;

import data.SerializableResultSet;
import sql.SQLConnect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;

public class ServerThread implements Runnable {
    Socket socket;
    BufferedReader br;
    ObjectOutputStream oos;
    Connection conn;
    String sql;

    public ServerThread(Socket s) {
        socket = s;
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            oos = new ObjectOutputStream(s.getOutputStream());
            conn = SQLConnect.getInstance().getConnection("world", "root", "1234");

        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    public void run() {
        try {
            String read = br.readLine();
            SerializableResultSet rs = new SerializableResultSet(conn.prepareStatement(read).executeQuery());
            oos.writeObject(rs);
            System.out.println("테이블 객체 보냄");
            oos.flush();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            try {
                sql = br.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(sql);
            try {
                if (!sql.isEmpty()) System.out.println(conn.prepareStatement(sql).execute());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
