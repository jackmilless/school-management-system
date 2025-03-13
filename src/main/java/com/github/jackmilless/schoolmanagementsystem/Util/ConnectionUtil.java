package com.github.jackmilless.schoolmanagementsystem.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * ConnectionUtil
 * utility class used to create a connection to the in-memory database
 * creates connection on first call, then returns same connection for life of program
 */
public class ConnectionUtil {
    private static Connection conn;
    private static String url = "jdbc:h2:mem:;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE"; 
    private static String username = "sa";
    private static String password = "";

    public static Connection getConnection() {
        if(conn == null) {
            try { conn = DriverManager.getConnection(url, username, password); } 
            catch (SQLException e) { e.printStackTrace(); }
        }
        return conn;
    }

    public static void closeConnection() {
        if(conn != null) {
            try { conn.close(); } 
            catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
