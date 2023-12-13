package client;

import data.SerializableResultSet;
import gui.TableGUI;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Scanner;

public class ClientThread extends Thread {

    Socket socket;
    ObjectInputStream ois;
    PrintWriter pw;
    BufferedReader br;
    QueryToTable queryToTable;
    TableGUI tableGUI;

    public ClientThread(Socket s) throws IOException {
        socket = s;
        pw = new PrintWriter(socket.getOutputStream(), true);
        ois = new ObjectInputStream(socket.getInputStream());
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void run() {
        super.run();
        try {
            Scanner sc = new Scanner(System.in);
            String tableName;
            SerializableResultSet rs;
            while (true) {
                System.out.print("테이블 이름을 입력해주세요 : ");
                tableName = sc.next();
                pw.println(tableName);
                pw.println("select * from " + tableName);
                rs = (SerializableResultSet) ois.readObject();
                if (rs != null) {
                    break;
                }
                System.out.println("'" + tableName + "' Table Not Found");
            }
            System.out.println("'"+tableName+"' Table Connected");
            tableGUI = new TableGUI(pw, tableName);
            tableGUI.setTable(rs);
            queryToTable = new QueryToTable(tableGUI);
            String query;
            while ((query = br.readLine()) != null) {
                tableGUI.isListenerEnabled = false;
                queryToTable.checkQuery(query); // 테이블을 업데이트, 추가, 삭제함
                tableGUI.isListenerEnabled = true;
            }
            System.exit(0);
        } catch (IOException | SQLException | ClassNotFoundException e) {
            System.out.println("\u001B[31m[Error] " + e.getMessage()+"\u001B[0m");
            System.exit(0);
        }
    }
}