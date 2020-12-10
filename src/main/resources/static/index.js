/**
 * 工具类
 */
import CryptoJS from "./js/crypto-js.js";
import { sm2Encrypt } from './sm2/sm2.js'
import { sm4 } from './sm4/sm4.js';
import { SM3Digest } from "./sm3/sm3.js";
import { Hexs } from "./sm3/hex.js";

export const utils = {
    /**
     * md5 加密方法
     * @param str
     * @returns {*}
     */
    md5: str => {
        return CryptoJS.MD5(str).toString();
    },
    /**
     * AES加密方法
     * @param text
     * @returns {string}
     */
    encryptText: (text) => {
        let key = CryptoJS.enc.Utf8.parse(AES_KEY);
        let readyText = CryptoJS.enc.Utf8.parse(text);
        let encryptedText = CryptoJS.AES.encrypt(readyText, key, {
            mode: CryptoJS.mode.ECB,
            padding: CryptoJS.pad.Pkcs7
        });
        return encryptedText.toString();
    },
    /**
     * AES 解密方法
     * @param text
     * @returns {*}
     */
    decryptText: (text) => {
        let key = CryptoJS.enc.Utf8.parse(AES_KEY);
        let decryptText = CryptoJS.AES.decrypt(text, key, {
            mode: CryptoJS.mode.ECB,
            padding: CryptoJS.pad.Pkcs7
        });
        return CryptoJS.enc.Utf8.stringify(decryptText).toString();
    },

    /**
     * SM2 加密
     */
    encryptSM2Text: text => {
        return sm2Encrypt(text, PUBKEY_HEX, CIPHER_MODE)
    },

    /**
     * SM3 加密
     */
    encryptSM3Text: text => {
        let byteText = Hexs.utf8StrToBytes(text);
        let sm3 = new SM3Digest();
        sm3.update(dataBy, 0, byteText.length);
        let sm3Hash = sm3.doFinal();
        let sm3HashHex = Hexs.encode(sm3Hash,0, sm3Hash.length);
        return sm3HashHex;
    },

    /**
     * SM4 加密
     */
    encryptSM4Text: text => {
        return sm4.encrypt_ecb(text, SM4_KEY);
    },


};