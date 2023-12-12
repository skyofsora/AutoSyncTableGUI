package client;

import gui.TableGUI;

import javax.swing.table.DefaultTableModel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryToTable {
    DefaultTableModel tableModel;
    TableGUI tableGUI;

    QueryToTable(TableGUI tableGUI) {
        this.tableGUI = tableGUI;
        this.tableModel = tableGUI.getTableModel();
    }

    public void checkQuery(String query) {
        if (query.indexOf("\u001B[31m[SERVER_ERROR]") == 0) {
            System.exit(0);
        } else {
            System.out.println("[Receive] " + query);
            if (query.indexOf("UPDATE") == 0) {
                updateTable(query);
            } else if (query.indexOf("DELETE") == 0) {
                deleteTable(query);
            } else if (query.indexOf("INSERT") == 0) {
                createTable(query);
            }
        }
    }

    public void updateTable(String query) {
        Object id = null;
        String columnName = null, columnValue = null;
        // 정규식을 사용하여 컬럼 이름과 컬럼 값을 추출함
        Pattern pattern = Pattern.compile("\\s+SET\\s+([a-zA-Z_가-힣]+)\\s*=\\s*'([^']*|\\s*)'\\s+WHERE\\s+\\(id\\s*=\\s*(\\d+)\\)");
        Matcher matcher = pattern.matcher(query);

        // 매칭된 패턴 찾기
        if (matcher.find()) {
            // 그룹 1: 컬럼 이름, 그룹 2: 컬럼 값
            columnName = matcher.group(1);
            columnValue = matcher.group(2);
            id = Integer.parseInt(matcher.group(3));
        }
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            if (tableModel.getValueAt(row, 0).equals(id)) {
                int column = tableModel.findColumn(columnName);
                tableModel.setValueAt(columnValue, row, column);
                break;
            }
        }
    }

    public void deleteTable(String query) {
        int id = query.charAt(query.indexOf("id = ") + 5) - '0';
        tableModel.removeRow(id);
    }

    public void createTable(String query) {
        Object[] object = new Object[tableModel.getColumnCount()];
        // 정규 표현식 패턴 정의
        Pattern pattern = Pattern.compile("\\((\\d+),");

        // 패턴과 일치하는 부분 찾기
        Matcher matcher = pattern.matcher(query);
        int id = -1;
        if (matcher.find()) {
            // 매치된 그룹(숫자) 출력
            id = Integer.parseInt(matcher.group(1));
        } else {
            System.out.println("Pattern not found.");
        }
        object[0] = id;
        tableModel.addRow(object);
    }
}
