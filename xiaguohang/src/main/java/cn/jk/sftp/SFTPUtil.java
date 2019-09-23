package cn.jk.sftp;

import com.jcraft.jsch.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;
import java.util.Vector;

/**
 * Created by YiZe on 2019/5/19.
 * TODO: 2019/5/19   类说明 sftp工具类
 */
public class SFTPUtil {
    private transient Logger log = LoggerFactory.getLogger(this.getClass());

    private ChannelSftp sftp;

    private Session session;
    /**
     * SFTP 登录用户名
     */
    private String username;
    /**
     * SFTP 登录密码
     */
    private String password;
    /**
     * 私钥
     */
    private String privateKey;
    /**
     * SFTP 服务器地址IP地址
     */
    private String host;
    /**
     * SFTP 端口
     */
    private int port;


    /**
     * 构造基于密码认证的sftp对象
     */
    public SFTPUtil(String username, String password, String host, int port) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    /**
     * 构造基于秘钥认证的sftp对象
     */
    public SFTPUtil(String username, String host, int port, String privateKey) {
        this.username = username;
        this.host = host;
        this.port = port;
        this.privateKey = privateKey;
    }

    public SFTPUtil() {
    }


    /**
     * 连接sftp服务器
     */
    public void login() {
        try {
            JSch jsch = new JSch();
            if (privateKey != null) {
                jsch.addIdentity(privateKey);// 设置私钥
            }

            session = jsch.getSession(username, host, port);

            if (password != null) {
                session.setPassword(password);
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");

            session.setConfig(config);
            System.out.println("waitting...");
            // session.setTimeout(100000);
            session.setTimeout(Integer.parseInt("100000"));
            session.connect(1000000000);
            System.out.println("Opening channel ...");
            Channel channel = session.openChannel("sftp");
            channel.connect(100000);
            System.out.println("success");
            sftp = (ChannelSftp) channel;
        } catch (JSchException e) {
            System.out.println("出错了");
            e.printStackTrace();
        }
    }

    /**
     * 关闭连接 server
     */
    public void logout() {
        if (sftp != null) {
            System.out.println("sftp not null");
            if (sftp.isConnected()) {
                sftp.disconnect();
                System.out.println("sftp is cloused");
            }
        }
        if (session != null) {
            System.out.println("session not null");
            if (session.isConnected()) {
                System.out.println("session not clouse");
                session.disconnect();
                System.out.println("session is cloused");
            }
        }
    }


    /**
     * 将输入流的数据上传到sftp作为文件。文件完整路径=basePath+directory
     *
     * @param basePath     服务器的基础路径
     * @param directory    上传到该目录
     * @param sftpFileName sftp端文件名
     * @param input        输入流
     */
    public void upload(String basePath, String directory, String sftpFileName, String input, String sftpRename) throws SftpException {

        try {
            sftp.cd(basePath);
            sftp.cd(directory);
        } catch (SftpException e) {
            //目录不存在，则创建文件夹
            String[] dirs = directory.split("/");
            String tempPath = basePath;
            for (String dir : dirs) {
                if (null == dir || "".equals(dir)) continue;
                tempPath += "/" + dir;
                try {
                    sftp.cd(tempPath);
                } catch (SftpException ex) {
                    sftp.mkdir(tempPath);
                    sftp.cd(tempPath);
                }
            }
        }
        try {
            sftp.put(input, sftpFileName);  //上传文件
            sftp.rename(basePath + sftpFileName, sftpRename + sftpFileName.replace(".tmp", ""));
            System.out.println("上传并改名成功！！");
            logout();
            System.out.println("已关闭");
        } finally {
            session.disconnect();
            sftp.disconnect();
            sftp.quit();
        }


    }


    /**
     * 下载文件。
     *
     * @param directory    下载目录
     * @param downloadFile 下载的文件
     * @param saveFile     存在本地的路径
     */
    public void download(String directory, String downloadFile, String saveFile) throws SftpException, FileNotFoundException {
        if (directory != null && !"".equals(directory)) {
            sftp.cd(directory);
        }
        File file = new File(saveFile);
        sftp.get(downloadFile, new FileOutputStream(file));
    }

    /**
     * 下载文件
     *
     * @param directory    下载目录
     * @param downloadFile 下载的文件名
     * @return 字节数组
     */
    public String download(String directory, String downloadFile) throws SftpException, IOException {
        if (directory != null && !"".equals(directory)) {
            sftp.cd(directory);
        }
        InputStream is = sftp.get(downloadFile);

        byte[] fileData = IOUtils.toByteArray(is);
        String fileDatas = new String(fileData);
        //System.out.println(resData);

        return fileDatas;
    }


    /**
     * 删除文件
     *
     * @param directory  要删除文件所在目录
     * @param deleteFile 要删除的文件
     */
    public void delete(String directory, String deleteFile) throws SftpException {
        sftp.cd(directory);
        sftp.rm(deleteFile);
    }


    /**
     * 列出目录下的文件
     *
     * @param directory 要列出的目录
     */
    public Vector<?> listFiles(String directory) throws SftpException {
        return sftp.ls(directory);
    }

/*    //上传文件测试
    public static void main(String[] args) throws SftpException, IOException {
        SFTPUtil sftp = new SFTPUtil("root", "agotoz!@#", "172.16.1.81", 22);
        sftp.login();
        *//*File file = new File("E:\\aaaaa.txt");
        InputStream is = new FileInputStream(file);*//*

        sftp.upload("/upload/tmp/", "/upload/tmp/", "test_p.tmp", "E:\\aaaaa.txt", "/upload/gzip/");
    }*/
}
