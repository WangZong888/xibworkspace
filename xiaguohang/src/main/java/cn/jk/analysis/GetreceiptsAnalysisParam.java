package cn.jk.analysis;

import cn.jk.util.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by YiZe on 2019/5/22.
 * @// TODO: 2019/5/22 查询mysql流水计算结果计算单据信息 并且从申请信息表汇总获取供应商编码、名称
 */
public class GetreceiptsAnalysisParam {
   /* public static String getParam(String risk_id) {
        StringBuffer sb = new StringBuffer();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = JDBCUtil.getConnection();
            String sql = "select outAmount_order_std,outAmount_order_mean from credit_apply_info where credit_risk_id = risk_id";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String outAmount_order_std = resultSet.getString("outAmount_order_std");
                String outAmount_order_mean = resultSet.getString("outAmount_order_mean");
                sb.append(outAmount_order_std).append("\001");
                sb.append(outAmount_order_mean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            JDBCUtil.closeResource(connection, preparedStatement, resultSet);
        }
        return sb.toString();
    }*/
}
