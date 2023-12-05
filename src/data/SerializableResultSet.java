package data;

import java.io.Serial;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SerializableResultSet implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final List<String> columnNames;
    private final List<List<Object>> rows;

    public String check = "도착함";

    public SerializableResultSet(ResultSet resultSet) throws SQLException {
        columnNames = new ArrayList<>();
        rows = new ArrayList<>();

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnName(i));
        }

        while (resultSet.next()) {
            List<Object> row = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                row.add(resultSet.getObject(i));
            }
            rows.add(row);
        }
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public List<List<Object>> getRows() {
        return rows;
    }
}
