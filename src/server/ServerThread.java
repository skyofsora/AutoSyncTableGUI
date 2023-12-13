package server;

import data.SerializableResultSet;
import sql.SQLConnect;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class ServerThread implements Runnable {
    final List<User> list;
    Socket socket;
    PrintWriter pw;
    BufferedReader br;
    ObjectOutputStream oos;
    Connection conn;
    User user;
    String ip;

    public ServerThread(Socket s, List<User> list) {
        this.list = list;
        socket = s;
        ip = "[" + socket.getInetAddress().getHostAddress() + "] ";
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            oos = new ObjectOutputStream(socket.getOutputStream());
            pw = new PrintWriter(socket.getOutputStream(), true);
            conn = SQLConnect.getInstance().getConnection("world", "root", "1234");
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    public void run() {
        SerializableResultSet rs;
        String tableName = null;
        do {
            try {
                tableName = br.readLine();
                String read = br.readLine();
                rs = new SerializableResultSet(conn.prepareStatement(read).executeQuery());
                System.out.println(ip + "'" + tableName + "' Table Connected");
            } catch (SQLException | IOException e) {
                System.out.println(ip + "'" + tableName + "' Table Not Found");
                e.fillInStackTrace();
                rs = null;
            }
            try {
                oos.writeObject(rs);
                oos.flush();
            } catch (IOException e) {
                e.fillInStackTrace();
                pw.println("\u001B[31m[Error] " + e.getMessage() + "\u001B[0m");
                System.out.println("\u001B[31m" + ip + e.getMessage() + "\u001B[0m");
            }
        } while (rs == null);   // rs가 null이면 반복
        user = new User(pw, tableName);
        synchronized (list) {
            list.add(user);
        }
        String sql;
        try {
            while ((sql = br.readLine()) != null) {
                conn.prepareStatement(sql).execute();
                System.out.println(ip + sql);
                broadcast(sql);
            }
        } catch (IOException | SQLException e) {
            e.fillInStackTrace();
            pw.println("\u001B[31m[Error] " + e.getMessage() + "\u001B[0m");
            System.out.println("\u001B[31m" + ip + e.getMessage() + "\u001B[0m");
        } finally {
            list.remove(user);
            pw.close();
            try {
                socket.close();
            } catch (IOException ignored) {

            }
            try {
                oos.close();
            } catch (IOException ignored) {

            }
            try {
                br.close();
            } catch (IOException ignored) {

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
