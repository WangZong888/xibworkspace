package cn.jk.sftp;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by YiZe on 2019/5/19.
 */

public class HdfsUtil {

    /**
     * 写文件到hdfs上
     */
    public Boolean write(String hdfspath, String result) {
        boolean flag = false;
        InputStream in = null;
        FSDataOutputStream fo = null;
        try {

            in = new ByteArrayInputStream(result.getBytes("UTF-8"));
            Configuration conf = new Configuration();
            conf.set("fs.defaultFS", "hdfs://hadoop01:9000");
            conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
            FileSystem fs = FileSystem.get(conf);
            fo = fs.create(new Path(hdfspath));
            int len = 0;
            byte[] b = new byte[1024];
            while ((len = in.read(b)) > -1) {
                fo.write(b, 0, len);
            }
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != in) {
                    in.close();
                    in = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (null != fo) {
                    fo.close();
                    fo = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }


    /**
     * 上传文件到hdfs上
     */
    public void upload(String dst, String src) throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://hadoop01:9000");
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        FileSystem fs = FileSystem.get(conf);
        if (fs.exists(new Path(dst))) {
            fs.copyFromLocalFile(new Path(src), new Path(dst));
        } else {
            fs.mkdirs(new Path(dst));
            fs.copyFromLocalFile(new Path(src), new Path(dst));
        }
        fs.close();
    }

    /**
     * 判断文件是否存在
     *
     * @throws IOException
     */
    public void exist(String hdfspath) throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://hadoop01:9000");
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        FileSystem fs = FileSystem.newInstance(conf);
        if (fs.exists(new Path(hdfspath))) {
            fs.delete(new Path(hdfspath), true);
        }
        fs.close();
    }

}


