package server;

import java.io.PrintWriter;

public class User {
    public PrintWriter pw;
    public String tableName;

    public User(PrintWriter pw, String tableName) {
        this.pw = pw;
        this.tableName = tableName;
    }
}
