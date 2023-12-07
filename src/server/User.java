package server;

import java.io.PrintWriter;

public class User {
    public PrintWriter pw;

    public User(PrintWriter pw, String tableName) {
        this.pw = pw;
        this.tableName = tableName;
    }

    public String tableName;
}
