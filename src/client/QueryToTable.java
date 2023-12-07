package client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryToTable {
    DefaultTableModel tableModel;

    QueryToTable(DefaultTableModel tableModel) {
        this.tableModel = tableModel;
    }

    public void checkQuery(String query) {

        if (query.indexOf("UPDATE") == 0) {
            updateTable(query);
        } else if (query.indexOf("DELETE") == 0) {
            deleteTable(query);
        } else if (query.indexOf("INSERT") == 0) {
            createTable();
        }

    }

    public void updateTable(String query) {
        Object id = (int) query.charAt(query.indexOf("id = ") + 5) - '0';
        Pattern pattern = Pattern.compile("\\bset\\s+([a-zA-Z_]+)\\s*=\\s*'([^']+)'");
        Matcher matcher = pattern.matcher(query);
        String columnName = null, columnValue = null;
        // 매칭된 패턴 찾기
        if (matcher.find()) {
            // 그룹 1: 컬럼 이름, 그룹 2: 컬럼 값
            columnName = matcher.group(1);
            columnValue = matcher.group(2);
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

    public void createTable() {
        Object[] object = new Object[tableModel.getColumnCount()];
        object[0] = tableModel.getRowCount() + 1;
        tableModel.addRow(object);
    }
}
