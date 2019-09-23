package cn.jk.sftp;

import cn.jk.analysis.RddSaveToMysql;

/**
 * Created by YiZe on 2019/5/20.
 * @// TODO: 2019/5/20 用信列表
 */
public class UseCreditList {
    public static SFTPUtil sftp = null;
    public static HdfsUtil hdfsUtil = null;

    public static boolean getUseCreditList() {
        boolean flag = false;
        try {
            sftp = new SFTPUtil("sftpuser", "sftpuser", "192.168.107.33", 22);
            sftp.login();
            sftp.download("/upload/", "用信列表.csv", "./csv/用信列表.csv");
            hdfsUtil = new HdfsUtil();
            hdfsUtil.upload("/user/xiaguohang/sftp/csv/usecreditlist", "./csv/用信列表.csv");
            RddSaveToMysql.usecreditListRDD();
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


