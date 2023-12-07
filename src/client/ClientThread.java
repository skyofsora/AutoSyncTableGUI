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
            System.out.println("사용하려는 테이블은 id를 제외하고 Null이 허용되어야 합니다.\n컬럼명 id에는 대문자가 포함되지 않아야 합니다. ex) ID, iD, Id 안됨\n첫 번째 컬럼이 항상 id이어야 합니다.");
            System.out.print("테이블 이름을 입력해주세요 : ");
            String tableName = sc.next();
            pw.println(tableName);
            pw.println("select * from " + tableName);
            SerializableResultSet rs = (SerializableResultSet) ois.readObject();
            TableGUI tableGUI = new TableGUI(pw, tableName);
            tableGUI.setTable(rs);
            queryToTable = new QueryToTable(tableGUI.getTableModel());
            String query;
            while ((query = br.readLine()) != null) {
                System.out.println(query);
                queryToTable.checkQuery(query);
            }
            tableGUI.dispose();
        } catch (IOException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }


}