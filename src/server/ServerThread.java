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

    public ServerThread(Socket s, List<User> list) {
        this.list = list;
        socket = s;
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
        while (true) {
            try {
                tableName = br.readLine();
                String read = br.readLine();
                rs = new SerializableResultSet(conn.prepareStatement(read).executeQuery());
                System.out.println(tableName + " 테이블 연결됨");
            } catch (SQLException | IOException e) {
                System.out.println(socket.getInetAddress() + ": [" + tableName + "] 테이블이 존재하지 않습니다.");
                e.fillInStackTrace();
                rs = null;
            }
            try {
                oos.writeObject(rs);
                oos.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (rs != null) break;
        }
        user = new User(pw, tableName);
        synchronized (this.list) {
            this.list.add(user);
        }
        String sql;
        try {
            while ((sql = br.readLine()) != null) {  // 메인 서버 구동지점
                list.removeIf(user -> user.pw.checkError());
                System.out.println(sql);
                broadcast(sql);
                if (!sql.isEmpty()) {
                    conn.prepareStatement(sql).execute();
                }
            }
        } catch (SQLException | IOException e) {
            e.fillInStackTrace();
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
