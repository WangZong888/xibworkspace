package cn.jk.pojo;

/**
 * Created by YiZe on 2019/5/19.
 */
public class Label {
//    public static String USER = "mcl";
//    public static String PASSWORD = "mcl";
    public static String USER = "root";
    public static String PASSWORD = "root";
//    public static String DRIVER = "jdbc:mysql://10.10.188.12:12003/bigdata";
    public static String DRIVER = "jdbc:mysql://localhost:3306/dbty";

    //HDFS
    //public static String HDFSURL = "hdfs://bigdata01:8020";
    public static String HDFSURL = "hdfs://hadoop01:9000";
    //卖方业务数据
    public static String BUSINESSDATA = HDFSURL + "/user/xiaguohang/sftp/csv/businessdata/";
    //授信列表
    public static String CREDITLIST = HDFSURL + "/user/xiaguohang/sftp/csv/creditlist/";
    //还款列表
    public static String REIMBURSEMENTLIST = HDFSURL + "/user/xiaguohang/sftp/csv/reimbursementlist/";
    //用信列表
    public static String USECREDITLIST = HDFSURL + "/user/xiaguohang/sftp/csv/usecreditlist/";
}
