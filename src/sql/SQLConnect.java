package sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnect {

    private SQLConnect() {
    }

    public static SQLConnect getInstance() {    // 싱글톤
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final SQLConnect INSTANCE = new SQLConnect();
    }

    public Connection getConnection(String schema, String id, String password) {
        try {
            String url = "jdbc:mysql://localhost:3306/" + schema;
            return DriverManager.getConnection(url, id, password);
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 연결에 실패했습니다.", e);
        }
    }

    public void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.fillInStackTrace();
        }
    }
}