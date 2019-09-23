package cn.jk.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by YiZe on 2019/5/19.
 */
public class ToMysql {

    /**
     * @param info
     * @// TODO: 2019/5/19 贷后预警数据存储
     */
    public static void dhAlarm(String info) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = JDBCUtil.getConnection();
            connection.setAutoCommit(false);
            String sql = "insert into after_loan_warn (result) values(?) ";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,info);
            preparedStatement.addBatch();
            preparedStatement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            System.out.println("贷后预警数据插入失败");
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(connection, preparedStatement, null);
        }
    }
}
