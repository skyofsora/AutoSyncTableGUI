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
                System.out.println("[" + tableName + "] 테이블이 존재하지 않습니다.");
            }

            TableGUI tableGUI = new TableGUI(pw, tableName);
            tableGUI.setTable(rs);
            queryToTable = new QueryToTable(tableGUI.getTableModel());
            String query;
            while ((query = br.readLine()) != null) {
                System.out.println(query);
                queryToTable.checkQuery(query); // 테이블을 업데이트, 추가, 삭제함
            }
            tableGUI.dispose();
        } catch (IOException | SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


}