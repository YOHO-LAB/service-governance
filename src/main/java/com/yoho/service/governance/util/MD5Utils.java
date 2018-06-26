package com.yoho.service.governance.util;

import com.google.common.base.Charsets;
import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by wujiexiang on 17/2/10.
 */
public class MD5Utils {

    private static  final  String MD5_SALT = "fd4ad5fcsa0de589af23234ks1923ks";

    /**
     * 对消息计算MD5， 结果用Hex（十六进制）编码
     *
     * @param message 消息
     * @return MD5之后的结果
     */
    public static String md5(String message) {
        return new String(Hex.encodeHex(md5Digest(MD5_SALT + ":" + message)));
    }

    /**
     * 计算MD5
     *
     * @param message 原始消息
     * @return MD5之后的记过
     */
    private static byte[] md5Digest(String message) {

        byte[] md5Bytes = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md5Bytes = md.digest(message.getBytes(Charsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return md5Bytes;
    }
}
