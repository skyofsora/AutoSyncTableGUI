package server;

import data.SerializableResultSet;
import sql.SQLConnect;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ServerThread implements Runnable {
    Socket socket;
    PrintWriter pw;
    BufferedReader br;
    ObjectOutputStream oos;
    Connection conn;
    String sql;
    User user;

    final List<User> list;

    public ServerThread(Socket s, List<User> list) {
        this.list = list;
        socket = s;
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            oos = new ObjectOutputStream(socket.getOutputStream());
            pw = new PrintWriter(socket.getOutputStream(), true);
            conn = SQLConnect.getInstance().getConnection("world", "root", "1234");
            user = new User(pw, br.readLine());
            synchronized (this.list) {
                this.list.add(user);
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    public void run() {
        try {
            String read = br.readLine();
            SerializableResultSet rs = new SerializableResultSet(conn.prepareStatement(read).executeQuery());
            oos.writeObject(rs);
            oos.flush();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            try {
                sql = br.readLine();
                System.out.println(sql);
                broadcast(sql);
                if (!(sql == null || sql.isEmpty())) {
                    conn.prepareStatement(sql).execute();
                }
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void broadcast(String message) {
        synchronized (list) {
            for (User user : list) {
                if (this.user.pw != user.pw && Objects.equals(this.user.tableName, user.tableName)) {
                    user.pw.println(message);
                }
            }
        }
    }
}
