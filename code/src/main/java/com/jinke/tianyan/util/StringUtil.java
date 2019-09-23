package com.jinke.tianyan.util;

import java.io.ByteArrayInputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import com.google.common.base.Joiner;

/**
 * Created by shanfq on 2018/8/31.
 */
public class StringUtil {


    /**
     * 反转字符串
     * @param str
     * @return
     */
    public static String reverse(String str){
        StringBuffer sb = new StringBuffer(str);
        sb = sb.reverse();
        return sb.toString();
    }



    /**
     * 获取参数的字典排序
     *
     * @param map 参数key-value map集合
     * @return String 排序后的字符串
     */
    public static String getOrderByLexicographic(Map<String, Object> map) throws Exception {
        //字典序排序
        map = new TreeMap<String, Object>(map);//map =  Maps.newTreeMap();
        // 拼接成字符串
        String join = Joiner.on('&').useForNull("").withKeyValueSeparator("=").join(map) + "&";
        System.out.println("data after sort and join:"+join);

        // sha256 哈希，并转16进制（大写）
        join = encodeHex(EncryptUtils.getSha256Data(join)).toUpperCase();
        System.out.println("data after sha256 and hex:"+join);

        return join;
    }

    public static String encodeHex(byte[] input) {
        return Hex.encodeHexString(input);
    }

    public static String getMd5Digest(String msg){
        return Hex.encodeHexString(DigestUtils.getMd5Digest().digest(msg.getBytes()));
    }

    /**
     * 获取公钥证书
     * @param pemContent    公钥证书Base64密文
     * @return  公钥证书
     * @throws Exception 解析异常
     */
    public static X509Certificate loadCertificate(String pemContent) throws Exception {
        CertificateFactory factory = null;
        try {
            factory = CertificateFactory.getInstance("X.509");
            ByteArrayInputStream is = new ByteArrayInputStream(Base64.getDecoder().decode(pemContent));
            X509Certificate certificate = (X509Certificate)factory.generateCertificate(is);
            return certificate;
        } catch (CertificateException e) {
            System.out.println("加载证书失败,{}"+pemContent+e);
            throw new Exception(e);
        }
    }

    /**
     * 验签
     * @param plainText 签名原文
     * @param signedText 签名
     * @param algorithm  签名算法
     * @param publicKey 公钥证书
     * @return  验签结果    通过返回true,不通过返回false
     * @throws DupException 验签异常
     */
    public static boolean verify(String plainText, String signedText, String algorithm, PublicKey publicKey ) throws Exception{
        try {
            Signature sig = Signature.getInstance(algorithm);
            sig.initVerify(publicKey);
            sig.update(plainText.getBytes());
            byte[] b = Base64.getDecoder().decode(signedText);
            return sig.verify(b);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("不支持的算法[{}]"+algorithm);
            throw new Exception(e);
        } catch (SignatureException e) {
            System.out.println("验签失败,原文[{}]，签名[{}]"+plainText+signedText);
            throw new Exception(e);
        } catch (InvalidKeyException e) {
            System.out.println("证书不可用[{}]"+publicKey);
            throw new Exception(e);
        }catch(Exception e){
            System.out.println("验签失败："+e);
            throw new Exception(e);
        }
    }
}
