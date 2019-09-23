package com.jinke.tianyan.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtils {
    private static Connection con;
    //在静态代码块中创建与数据库的连接
/*    static{
        try{
            String driver = "com.mysql.jdbc.Driver";
            Class.forName(driver);
            con = DriverManager.getConnection(Global.MYSQL_DRIVER_URL, Global.MYSQL_USERNAME, Global.MYSQL_PWD);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage(),e);
        }
    }*/

    public static Connection getCon(){
        try{
            String driver = "com.mysql.jdbc.Driver";
            Class.forName(driver);
            con = DriverManager.getConnection(Global.MYSQL_DRIVER_URL, Global.MYSQL_USERNAME, Global.MYSQL_PWD);
            return con;
        }catch(Exception e){
            throw new RuntimeException(e.getMessage(),e);
        }

    }

    public static void close(Connection conn){
        try{
            if(conn != null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
