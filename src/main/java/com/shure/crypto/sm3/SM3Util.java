package com.shure.crypto.sm3;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.Security;
import java.util.Arrays;


public class SM3Util {

    private static final Logger logger = LoggerFactory.getLogger(SM3Util.class);

    private static final String ENCODING = "UTF-8";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }


    /**
     * 返回长度=32的byte数组
     *
     * @param srcData
     * @return 数组
     * @explain 生成对应的hash值
     */
    public static byte[] hash(byte[] srcData) {
        try {
            SM3Digest digest = new SM3Digest();
            digest.update(srcData, 0, srcData.length);
            byte[] hash = new byte[digest.getDigestSize()];
            digest.doFinal(hash, 0);
            return hash;
        } catch (Exception e) {
            logger.error("返回byte数组失败：", e);
            throw new IllegalStateException("返回byte数组异常");
        }
    }

    /**
     * 通过密钥进行加密(暂时不用)
     *
     * @param key     密钥
     * @param srcData 被加密的byte数组
     * @return
     * @explain 指定密钥进行加密
     */
    public static byte[] hmac(byte[] key, byte[] srcData) {
        try {
            KeyParameter keyParameter = new KeyParameter(key);
            SM3Digest digest = new SM3Digest();
            HMac mac = new HMac(digest);
            mac.init(keyParameter);
            mac.update(srcData, 0, srcData.length);
            byte[] result = new byte[mac.getMacSize()];
            mac.doFinal(result, 0);
            return result;
        } catch (Exception e) {
            logger.error("通过秘钥加密失败：", e);
            throw new IllegalStateException("通过秘钥加密异常");
        }
    }

    /**
     * sm3算法加密
     *
     * @param paramStr 待加密字符串
     * @return 返回加密后，固定长度=32的16进制字符串
     */
    public static String encrypt(String paramStr) {
        // 将返回的hash值转换成16进制字符串
        String resultHexString = "";
        try {
            // 将字符串转换成byte数组
            byte[] srcData = paramStr.getBytes(ENCODING);
            // 调用hash()
            byte[] resultHash = hash(srcData);
            // 将返回的hash值转换成16进制字符串
            resultHexString = ByteUtils.toHexString(resultHash);
            return resultHexString.toUpperCase();
        } catch (UnsupportedEncodingException e) {
            logger.error("SM3算法加密失败：", e);
            throw new IllegalStateException("SM3算法加密异常");
        }

    }


    /**
     * sm3算法加密
     *
     * @param file 待加密文件
     * @return
     */
    public static String encrypt(File file) {
        try (
                FileInputStream fis = new FileInputStream(file);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ) {
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            byte[] buffer = bos.toByteArray();

            //1.摘要
            byte[] md = new byte[32];
            SM3Digest sm = new SM3Digest();
            sm.update(buffer, 0, buffer.length);
            sm.doFinal(md, 0);
            String s = new String(Hex.encode(md));
            return s.toUpperCase();
        } catch (FileNotFoundException e) {
            logger.error(e.toString());
            throw new IllegalStateException("加密异常！");
        } catch (IOException e) {
            logger.error(e.toString());
            throw new IllegalStateException("加密异常！");
        }
    }

    /**
     * 判断源数据与加密数据是否一致
     *
     * @param srcStr       原字符串
     * @param sm3HexString 16进制字符串
     * @return 校验结果
     * @explain 通过验证原数组和生成的hash数组是否为同一数组，验证2者是否为同一数据
     */
    public static boolean verify(String srcStr, String sm3HexString) {
        boolean flag = false;
        try {
            byte[] srcData = srcStr.getBytes(ENCODING);
            byte[] sm3Hash = ByteUtils.fromHexString(sm3HexString);
            byte[] newHash = hash(srcData);
            if (Arrays.equals(newHash, sm3Hash)) {
                flag = true;
            }
            return flag;
        } catch (UnsupportedEncodingException e) {
            logger.error("判断源数据与加密数据错误：", e);
            throw new IllegalStateException("判断源数据与加密数据异常");
        }
    }

    /**
     * 判断源数据与加密数据是否一致
     *
     * @param file   原字符串
     * @param sm3Hex 16进制字符串
     * @return 校验结果
     * @explain 通过验证原数组和生成的hash数组是否为同一数组，验证2者是否为同一数据
     */
    public static boolean verify(File file, String sm3Hex) {
        boolean flag = false;
        try {
            String newStr = encrypt(file);
            if (StringUtils.isNotBlank(newStr) && newStr.equals(sm3Hex)) {
                flag = true;
            }
            return flag;
        } catch (Exception e) {
            logger.error("判断源数据与加密数据错误：", e);
            throw new IllegalStateException("判断源数据与加密数据异常");
        }
    }

    public static void main(String[] args) {
        String text = "hello world !";
        String str = encrypt(text);
        System.out.println(str);
    }

}
