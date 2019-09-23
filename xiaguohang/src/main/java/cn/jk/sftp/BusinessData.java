package cn.jk.sftp;

import cn.jk.analysis.RddSaveToMysql;
import cn.jk.util.DateUtil;
import cn.jk.util.DeleteFileUtil;

/**
 * Created by YiZe on 2019/5/20.
 *
 * @// TODO: 2019/5/20 卖方业务数据同步
 */
public class BusinessData {
    public static SFTPUtil sftp = null;
    public static HdfsUtil hdfsUtil = null;

    public static boolean getBusinessData() {
        boolean flag = false;
        try {
            sftp = new SFTPUtil("xibank", "RDqct6hk", "27.115.11.214", 9009);
            sftp.login();
            sftp.download("/xibank/out/", "billlist_" + DateUtil.getCurrentByCalendar() + ".csv", "./csv/billlist_" + DateUtil.getCurrentByCalendar() + ".csv");
            hdfsUtil = new HdfsUtil();
            hdfsUtil.upload("/user/xiaguohang/sftp/csv/businessdata/" + DateUtil.getCurrentByCalendar() + "/", "./csv/billlist_" + DateUtil.getCurrentByCalendar() + ".csv");
            RddSaveToMysql.businessDataRDD();
            flag = true;
            return flag;
        } catch (Exception e) {

            System.out.println("文件不存在");
            e.printStackTrace();
        } finally {
            sftp.logout();
        }
        return flag;
    }
}

