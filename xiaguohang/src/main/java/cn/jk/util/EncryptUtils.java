package cn.jk.util;


import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.Enumeration;

/**
 * Created by YiZe on 2019/7/2.
 */
public class EncryptUtils {


    public static byte[] getSha256Data(String oriText) throws Exception{
        return getSha256Data(oriText, 1);
    }

    public static byte[] getSha256Data(String oriText, int iterations)  throws Exception{
        try {
            MessageDigest e = MessageDigest.getInstance("SHA-256");

            byte[] result = e.digest(oriText.getBytes());

            for(int i = 1; i < iterations; ++i) {
                e.reset();
                result = e.digest(result);
            }

            return result;
        } catch (GeneralSecurityException var7) {
            throw new Exception(var7);
        }

    }



    /**
     * 签名
     * @param plainText 签名原文
     * @param algorithm 签名算法
     * @param privateKey    私钥
     * @return  签名结果(BASE64)
     * @throws Exception 签名异常
     */
    public static String sign(String plainText, String algorithm, PrivateKey privateKey ) throws Exception {
        try {
            Signature sig = Signature.getInstance(algorithm);
            sig.initSign(privateKey);
            sig.update(plainText.getBytes());
            byte[] b = sig.sign();
            return Base64.getEncoder().encodeToString(b);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("签名异常，不支持算法[{}]"+algorithm+e);
            throw new Exception(e);
        } catch (SignatureException e) {
            System.out.println("签名异常，异常原因[{}]"+e.getMessage());
            throw new Exception(e);
        } catch (InvalidKeyException e) {
            System.out.println("签名异常，无效密钥[{}]"+privateKey);
            throw new Exception(e);
        }
    }

    /**
     * 从classpath下读取文件内容
     * @param path  资源路径
     * @return  文件内容（base64编码)
     * @throws IOException
     */
    public static String readBase64FromResource(String path) throws IOException, URISyntaxException {
        System.out.println("----------readBase64FromResource----------"+path);
        URL url = ClassLoader.getSystemResource(path);
        //System.out.println("----------url----------"+url);
        return readBase64FromFile(path);
    }

    /**
     * 根据指定的文件路径读取文件内容
     * @param path  文件绝对路径或相对路径
     * @return  文件内容（base64编码)
     * @throws IOException
     * @throws URISyntaxException
     */
    public static String readBase64FromFile(String path) throws IOException {
        File file = new File(path);
        return readBase64FromFile(file);
    }
    
    /**
     * 读取指定文件的内容
     * @param file  文件
     * @return  文件内容(Base64编码)
     * @throws IOException
     */
    @SuppressWarnings("restriction")
    public static String readBase64FromFile(File file) throws IOException {
        int iBuffSize = (int)file.length();
        InputStream is = new FileInputStream(file);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(iBuffSize);
        byte[] buffer = new byte[iBuffSize];
        int readLength = 0;
        while( (readLength = is.read(buffer)) >= 0){
            baos.write(buffer, 0, readLength);
        }
        try {
            is.close();
        }catch (IOException ex){
            System.out.println("关闭文件流发生异常!{}"+file.getPath());
        }
        String fileContent = com.sun.org.apache.xerces.internal.impl.dv.util.Base64.encode(baos.toByteArray());
        return fileContent;
    }

    /**
     * 获取私钥
     * @param pfxContent   私钥证书Base64密文
     * @param password      私钥密码
     * @return  私钥
     * @throws Exception 解析异常
     */
    public static PrivateKey readPrivateKey(String pfxContent, String password, String keyStoreType) throws Exception{
        char[] pass = password.toCharArray();
        try {
            KeyStore ks = KeyStore.getInstance(keyStoreType);
            ks.load(new ByteArrayInputStream(Base64.getDecoder().decode(pfxContent)), pass);
            Enumeration<String> aliases = ks.aliases();
            String alias = aliases.nextElement();
            if (null == alias) {
                System.out.println("解析证书失败，找不到证书别名");
                throw new Exception("解析证书失败，找不到证书别名");
            }
            PrivateKey privateKey = (PrivateKey) ks.getKey(alias, pass);
            return privateKey;
        }catch(KeyStoreException e){
            System.out.println("读取私钥异常KeyStoreType[{}]"+keyStoreType);
            throw new Exception(e);
        } catch (CertificateException e) {
            throw new Exception(e);
        } catch (UnrecoverableKeyException e) {
            throw new Exception(e);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("读取私钥失败，找不到支持的算法");
            throw new Exception(e);
        } catch (IOException e) {
            throw new Exception(e);
        }
    }
}
