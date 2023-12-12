package gui;

import data.SerializableResultSet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class TableGUI extends JFrame {
    public final String tableName;
    private final PrintWriter pw;
    public boolean isListenerEnabled = true;
    private JTable table;
    private DefaultTableModel tableModel;

    public TableGUI(PrintWriter pw, String tableName) {
        this.pw = pw;
        this.tableName = tableName;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public void setTable(SerializableResultSet rs) throws SQLException {
        tableModel = new DefaultTableModel();
        setTitle(tableName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 열 이름 추가
        for (String columnName : rs.getColumnNames()) {
            tableModel.addColumn(columnName);
        }

        // 행 데이터 추가
        for (List<Object> row : rs.getRows()) {
            tableModel.addRow(row.toArray());
        }


        // 테이블 생성 및 모델 설정
        table = new JTable(tableModel);
        table.getColumn("id").setWidth(0);
        table.getColumn("id").setMinWidth(0);
        table.getColumn("id").setMaxWidth(0);

        // 밸류 체크
        tableModel.addTableModelListener(e -> {
            if (isListenerEnabled) {
                if (e.getColumn() != -1) {
                    String changedData;
                    Object temp = table.getValueAt(e.getFirstRow(), e.getColumn());
                    if (temp == null || Objects.equals(temp.toString(), "")) {
                        changedData = "''";
                    } else {
                        changedData = "'" + temp + "'";
                    }
                    String columnName = table.getColumnName(e.getColumn());
                    Object id = table.getValueAt(e.getFirstRow(), 0);
                    if (id != null) {
                        sendSQL("UPDATE " + tableName + " SET " + columnName + " = " + changedData + " WHERE (id = " + id + ")");
                    }
                }
            }
        });

        // 스크롤 가능한 패널에 테이블 추가
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 추가 및 삭제 버튼 생성
        JPanel buttonPanel = setButtonInPanel();

        add(buttonPanel, BorderLayout.SOUTH);
        // JFrame 설정
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }


    private JPanel setButtonInPanel() {
        JButton addButton = getjButton();

        JButton deleteButton = new JButton("삭제");
        deleteButton.addActionListener(e -> {
            // 삭제 버튼 액션 처리
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                sendSQL("DELETE from " + tableName + " WHERE (id = " + table.getValueAt(selectedRow, 0) + ")");
                tableModel.removeRow(selectedRow);

            }
        });

        // 버튼을 담은 패널 추가
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        return buttonPanel;
    }

    private JButton getjButton() {
        JButton addButton = new JButton("추가");
        addButton.addActionListener(e -> {

            // 추가 버튼 액션 처리
            Object[] object = new Object[tableModel.getColumnCount()];
            object[0] = (int) tableModel.getValueAt(tableModel.getRowCount() - 1, 0) + 1;
            tableModel.addRow(object);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("INSERT into ").append(tableName).append(" values (").append(object[0]).append(", ");

            for (int i = 1; i < table.getColumnCount(); i++) {
                stringBuilder.append("Null");
                if (i != table.getColumnCount() - 1) {
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append(")");
            sendSQL(stringBuilder.toString());
        });
        return addButton;
    }

    private void sendSQL(String sql) {
        System.out.println("[Send] " + sql);
        pw.println(sql);
    }
}