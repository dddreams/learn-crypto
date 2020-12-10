package com.shure.crypto.sm2;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

public class SM2Utils {
    //生成随机秘钥对
    public static String prik = "";
    // 国密规范正式公钥
    public static String pubk = "";


    public static void generateKeyPair() {
        SM2 sm2 = SM2.Instance();
        AsymmetricCipherKeyPair key = sm2.ecc_key_pair_generator.generateKeyPair();
        ECPrivateKeyParameters ecpriv = (ECPrivateKeyParameters) key.getPrivate();
        ECPublicKeyParameters ecpub = (ECPublicKeyParameters) key.getPublic();
        BigInteger privateKey = ecpriv.getD();
        ECPoint publicKey = ecpub.getQ();

        prik = Util.byteToHex(privateKey.toByteArray());
        pubk = Util.byteToHex(publicKey.getEncoded());
        System.out.println("公钥: " + pubk);
        System.out.println("私钥: " + prik);
    }

    //数据加密
    public static String encrypt(byte[] publicKey, byte[] data) {
        if (publicKey == null || publicKey.length == 0) {
            return null;
        }

        if (data == null || data.length == 0) {
            return null;
        }

        byte[] source = new byte[data.length];
        System.arraycopy(data, 0, source, 0, data.length);

        Cipher cipher = new Cipher();
        SM2 sm2 = SM2.Instance();
        ECPoint userKey = sm2.ecc_curve.decodePoint(publicKey);

        ECPoint c1 = cipher.Init_enc(sm2, userKey);
        cipher.Encrypt(source);
        byte[] c3 = new byte[32];
        cipher.Dofinal(c3);

        //C1 C2 C3拼装成加密字串
        return Util.byteToHex(c1.getEncoded()) + Util.byteToHex(source) + Util.byteToHex(c3);

    }

    //数据解密
    public static byte[] decrypt(byte[] privateKey, byte[] encryptedData) {
        if (privateKey == null || privateKey.length == 0) {
            return null;
        }

        if (encryptedData == null || encryptedData.length == 0) {
            return null;
        }
        //加密字节数组转换为十六进制的字符串 长度变为encryptedData.length * 2
        String data = Util.byteToHex(encryptedData);
        /***分解加密字串
         * （C1 = C1标志位2位 + C1实体部分128位 = 130）
         * （C3 = C3实体部分64位  = 64）
         * （C2 = encryptedData.length * 2 - C1长度  - C2长度）
         */
        byte[] c1Bytes = Util.hexToByte(data.substring(0, 130));
        int c2Len = encryptedData.length - 97;
        byte[] c2 = Util.hexToByte(data.substring(130, 130 + 2 * c2Len));
        byte[] c3 = Util.hexToByte(data.substring(130 + 2 * c2Len, 194 + 2 * c2Len));

        SM2 sm2 = SM2.Instance();
        BigInteger userD = new BigInteger(1, privateKey);

        //通过C1实体字节来生成ECPoint
        ECPoint c1 = sm2.ecc_curve.decodePoint(c1Bytes);
        Cipher cipher = new Cipher();
        cipher.Init_dec(userD, c1);
        cipher.Decrypt(c2);
        cipher.Dofinal(c3);

        //返回解密结果
        return c2;
    }

    public static void main(String[] args) throws Exception {
        //生成密钥对
        generateKeyPair();
//        pubk = "04E257CA30BBAC9EC4DD6D3A49F054C5EB405D8DECDE19CC0FC0A9C76686647C86989632ECB8345C384EF35361AFD7C00683A0A9C77D0E811D9788BAB7892F9760";
//        prik = "00C2170BEABEBD8C7FF75B8A7CEF15A75504061A55E98D5C75F72761F53C854CAE";

        String plainText = "王群";
        byte[] sourceData = plainText.getBytes();

        System.out.println("加密: ");
        String cipherText = SM2Utils.encrypt(Util.hexToByte(pubk), sourceData);
        System.out.println(cipherText);
        System.out.println("解密: ");
        plainText = new String(SM2Utils.decrypt(Util.hexToByte(prik), Util.hexToByte(cipherText)));
        System.out.println(plainText);


    }
}
