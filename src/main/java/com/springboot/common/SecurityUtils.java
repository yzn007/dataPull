package com.springboot.common;


import com.alibaba.druid.sql.ast.statement.SQLIfStatement;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by yzn00 on 2021/3/26.
 */
public class SecurityUtils {
    private static final Logger log=LoggerFactory.getLogger(SecurityUtils.class);
    //字符串进行加密算法的名称
    public static final String ALGORITHM = "RSA";
    //字符串进行加密填充的名称
    public static final String PADDING = "RSA/NONE/NoPadding";
    //字符串持有安全提供者的名称
    public static final String PROVIDER = "BC";
    //私钥
    public static final String PRIVATE_KEY ="MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCj9UPewqIu3HbDN" +
            "kW24+Ja+YfDUg14kNg4BvC5L/CtgYg2qp9Zz16Si2mxVtcN1WJid0cNV80vTwUbdpER4NqtAYAg+VxTc4TqAKQ" +
            "sSP58lp3PY0qIsTgGE4WjCRow5dtGoUV5Cl+rUyOoTa/KGXc7i6XMHJBVXol3l6c7vlThVTbCbbAdbzsYCTmFrP" +
            "3mw3qmdr0AMInN6SkPWbCcp/sT3Dslw0bSbLCcfyinO/ksTnBYIPfcNoCN1V7UlWu+iSFtRXYhTVfw8ARM2lWXV5" +
            "ZbBNZ7yQ2Hh+JEumN/XIrXt3d2yKRkvMMSwoQIw9jFqNnek14YqndPgp00dkHhl4N7AgMBAAECggEBAKOM3xzwjhO" +
            "su5NTKgucGomhbDn8OY/Q/L42vh4cojqRgaz2WmqeHrqT8uSBuZgFCVUroma+9OCCNJgGmAN7hucZHhTGacCc9TL" +
            "vXNUII0NogxcC9rCQB1CYXLAbPlMUlr1H1iM9o90k2+bhR7+T04pEbSjEPX3LBdmUH6/K1qTGYRZYq8xfEsc7" +
            "Pbey+N6FjuBxA2NB8uua1eScdYCMnrAsvm6oSiMzn6qOlazY4/3W5lNW3Xm7Nkxrda2EHnoAk5nvV35vC+fZtYA" +
            "I2PG+7NvbfzzALYDswiOr9rb8BnSbdxG6DjN3+J7hA2vgr2pdSIzvl9tQITwfEFdEUL7NdAECgYEA0/zuZqD1t/cUJ" +
            "368dBNg6QCpmF82HT05gBvD9+XXVYmJoBEpKPvN0UiKqVlSKk1r9caWdmUBc25J4xDoQdvVf1Y5CjJcTPd0yqLjhcDxk" +
            "It5amRGkJmAB3susIL0157qUIVLir8Kx0TXsjPCBkSWLPkL7Vp+zaBEcnsytCkNLnsCgYEAxf+QRhdImwfHHf+TMC8qO" +
            "JdyKzriezxqk7y2pW+bdqXamIeWKInmj9N2752No7TW+yCn4T03qr+pDGA2bMDlL9g9u7WmP02y54xBqjoBL2UTsTsDildqS" +
            "AI7d9QFZ5RNIkFUgAb1XFop73jfaW+I4Mgks3d88pMeEfNBmHg5bwECgYBYRECfyvWFHAPwTfz59/eqDPSQ1VQa6JtAlE8MsU" +
            "7v9syvMXYJ8o7ITYQu71oe8w240G9l7Lil744kVa3ffkFvO9+UN1PnWEHw/dHup1/t56Uuf4JCQyfj9Y6DQIPiI/E8hcPmiZVb" +
            "U3bcZGCta3F8WFZ3Be82fz0AzftWuKEccQKBgQCDZYp1OVZxq8O0YLtpKqO2UqrtjXJnnjyMNpxvRZXCM2bR+ojn9jwQDqHLe" +
            "p9YmNZiadV/YITkx7SEBgWYPpraGYM+YtolAIdNqoKZWU0YCtf6sdMzp7dcej7E0uuTRZO6u+g73mJOG3fg+DIrYl1P7wKMJyv" +
            "AP2mhN/G8IWzcAQKBgDECXP8eD5Hr0YHQDhxygIpWAKskcs1waJfN/+T5Nhkmdzr7VjkPtB5aOcHHCTw5ur2hCdeMRbde1Yp0" +
            "h05ah43PHtkPj2eQULg5ptk0yve3ww7v4TsLkTHIJdB7teCf9Q++v0fkBk6G97M9FcrlvsXVq7cwIqJ/dzSGbM+xggm9";
    //公钥
    public static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAo/VD3sKiLtx2wzZF" +
            "tuPiWvmHw1INeJDYOAbwuS/wrYGINqqfWc9ekotpsVbXDdViYndHDVfNL08FG3aREeDarQGAIPlc" +
            "U3OE6gCkLEj+fJadz2NKiLE4BhOFowkaMOXbRqFFeQpfq1MjqE2vyhl3O4ulzByQVV6Jd5enO75U4VU2w" +
            "m2wHW87GAk5haz95sN6pna9ADCJzekpD1mwnKf7E9w7JcNG0mywnH8opzv5LE5wWCD33DaAjdVe1JVrvokhbU" +
            "V2IU1X8PAETNpVl1eWWwTWe8kNh4fiRLpjf1yK17d3dsikZLzDEsKECMPYxajZ3pNeGKp3T4KdNHZB4ZeDewIDAQAB";


    /**
     * 私钥
     * 将字符串进行RSA解密
     *
     * @param text
     * @return
     */
    public static String decryptBase16(String text) {
        String cipherTextBase64 = "";
        try {
//            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = null;
//            if(StringUtils.isEmpty(key))
                keySpec = new PKCS8EncodedKeySpec(Base64.decode(PRIVATE_KEY));
//            else
//                keySpec = new PKCS8EncodedKeySpec(Base64.decode(key));
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] cipherText = cipher.doFinal(text.getBytes());
            //16进制转字符
            cipherTextBase64 = bytesToHexString(cipherText);
        } catch (Exception e) {
            log.info("[字符串进行RSA解密出现异常:{}]", e);
        }
        return cipherTextBase64;
    }

    /**
     * 公钥
     * 将字符串进行RSA加密
     *
     * @param str
     * @return
     */
    public static String encryptBase16(String str,String publikKey) {
        String hexEncryPass = "";
        try {
            BASE64Decoder base64Decoder= new BASE64Decoder();
//            byte[] buffer= base64Decoder.decodeBuffer(PUBLIC_KEY);
            KeyFactory keyFactory= KeyFactory.getInstance("RSA");
//            X509EncodedKeySpec keySpec= new X509EncodedKeySpec(buffer);
            if(StringUtils.isEmpty(publikKey))
                publikKey = PUBLIC_KEY;
            X509EncodedKeySpec keySpec=  new X509EncodedKeySpec(Base64.decode(publikKey
                    .replaceAll("\n","").replaceAll("-----BEGIN PUBLIC KEY-----","")
                    .replaceAll("-----END PUBLIC KEY-----","")));

            PublicKey publickey=keyFactory.generatePublic(keySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publickey);
//            //字符转16进制
//            byte[] text = hexStringToByteArray(str);
            byte[] text = cipher.doFinal(str.getBytes());
            //16进制转字符
            hexEncryPass = bytesToHexString(text).toLowerCase();
//            dectyptedText = cipher.doFinal(str.getBytes());
        } catch (Exception e) {
            log.info("[字符串进行RSA加密出现异常:{}]", e);
        }
        return hexEncryPass;
    }

    /**
     * 16进制表示的字符串转换为字节数组
     *
     * @param s 16进制表示的字符串
     * @return byte[] 字节数组
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] b = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个字节
            b[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return b;
    }

    /**
     * byte数组转16进制字符串
     * @param bArray
     * @return
     */
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 字符串转换为16进制字符串
     *
     * @param s
     * @return
     */
    public static String stringToHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    /**
     * 16进制字符串转换为字符串
     *
     * @param s
     * @return
     */
    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "gbk");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    public static void main(String [] args){
        System.out.println("hex encrypt start……");
        String passEncrypt = encryptBase16(stringToHexString("Shanghai"),"");
        System.out.println(passEncrypt);
        System.out.println(hexStringToString(decryptBase16(passEncrypt)));
        System.out.println("no hex start");
        passEncrypt = encryptBase16("Shanghai","");
        System.out.println(passEncrypt);
        System.out.println(decryptBase16(passEncrypt));
    }
}

