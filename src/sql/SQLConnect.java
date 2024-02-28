package sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnect {
    Connection conn;

    private SQLConnect() {
    }

    public static SQLConnect getInstance() {    // 싱글톤
        return LazyHolder.INSTANCE;
    }

    public Connection getConnection(String schema, String id, String password) {
        try {
            String url = "jdbc:mysql://localhost:3306/" + schema;
            return conn = DriverManager.getConnection(url, id, password);
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 연결에 실패했습니다.", e);
        }
    }

    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.fillInStackTrace();
        }
    }

    private static class LazyHolder {
        private static final SQLConnect INSTANCE = new SQLConnect();
    }
}