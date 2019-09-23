package cn.jk.sftp;

import cn.jk.analysis.RddSaveToMysql;
import cn.jk.util.DeleteFileUtil;

/**
 * Created by YiZe on 2019/5/20.
 *
 * @// TODO: 2019/5/20 还款列表数据同步上传
 */
public class ReimbursementList {
    public static SFTPUtil sftp = null;
    public static HdfsUtil hdfsUtil = null;

    public static boolean getReimbursementList() {
        boolean flag = false;
        try {
            sftp = new SFTPUtil("sftpuser", "sftpuser", "192.168.107.33", 22);
            sftp.login();
            sftp.download("/upload/", "还款列表.csv", "./csv/还款列表.csv");
            hdfsUtil = new HdfsUtil();
            hdfsUtil.upload("/user/xiaguohang/sftp/csv/reimbursementlist", "./csv/还款列表.csv");
            RddSaveToMysql.reimbursementListRDD();
            flag = true;
            return flag;
        } catch (Exception e) {

            System.out.println("文件不存在");
            e.printStackTrace();
        }finally {
            sftp.logout();
        }
        return flag;
    }
}
