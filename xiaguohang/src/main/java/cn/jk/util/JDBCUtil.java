package cn.jk.util;

import cn.jk.pojo.Label;

import java.sql.*;

/**
 * Created by YiZe on 2019/5/19.
 */
public class JDBCUtil {
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(Label.DRIVER, Label.USER, Label.PASSWORD);
        return connection;
    }
    /**
     * 释放资源
     * @param conn
     * @param st
     * @param rs
     */
    public static void closeResource(Connection conn,PreparedStatement st,ResultSet rs) {
        closeResultSet(rs);
        closeStatement(st);
        closeConnection(conn);
    }

    /**
     * 释放连接 Connection
     * @param conn
     */
    public static void closeConnection(Connection conn) {
        if(conn !=null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //等待垃圾回收
        conn = null;
    }

    /**
     * 释放语句执行者 Statement
     * @param st
     */
    public static void closeStatement(PreparedStatement st) {
        if(st !=null) {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //等待垃圾回收
        st = null;
    }

    /**
     * 释放结果集 ResultSet
     * @param rs
     */
    public static void closeResultSet(ResultSet rs) {
        if(rs !=null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //等待垃圾回收
        rs = null;
    }
}
