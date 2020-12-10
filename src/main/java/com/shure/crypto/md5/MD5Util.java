package com.shure.crypto.md5;

import java.security.MessageDigest;
import java.util.Arrays;

/**
 * MD5
 *
 * @author
 */
public class MD5Util {

    private static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (byte value : b) {
            resultSb.append(byteToHexString(value));
        }

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n += 256;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return HEX_DIGITS[d1] + HEX_DIGITS[d2];
    }

    public static String md5Encode(String origin, String charsetname) {
        String resultString = null;
        char[] array = origin.toCharArray();
        try {
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < array.length; i++) {
                sb.append(array[i]);
            }
            resultString = sb.toString();
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname)) {

                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes()));
            } else {
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes(charsetname)));
            }
            Arrays.fill(array, ' ');

        } catch (Exception exception) {
        }
        return resultString;
    }

    private static final String[] HEX_DIGITS = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

}
