package cn.jk.api;

import cn.jk.util.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by YiZe on 2019/5/20.
 *
 * @// TODO: 2019/5/20 获取API参数
 */
public class GETRequestAPIParam {
    public static String getParam() {
        StringBuffer sb = new StringBuffer();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = JDBCUtil.getConnection();
            String sql = "select * from sb";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                resultSet.getInt(0);
                sb.append("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            JDBCUtil.closeResource(connection, preparedStatement, resultSet);
        }
        return sb.toString();
    }
}
