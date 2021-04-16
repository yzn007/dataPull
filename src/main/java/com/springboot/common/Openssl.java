package com.springboot.common;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;
import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;

/**
 * Created by yzn00 on 2021/4/15.
 */
public class Openssl {
    static final String PRIVATE_PATH = "I:\\workspace\\test\\file\\rsa_1024_priv.pem";
    static final String PUBLIC_PATH = "I:\\workspace\\test\\file\\rsa_1024_pub.pem";


    private static byte[] encrypt(Key pubkey, String text) {
        try {
            Cipher rsa;
            rsa = Cipher.getInstance("RSA");
            rsa.init(Cipher.ENCRYPT_MODE, pubkey);
            return rsa.doFinal(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static String decrypt(Key decryptionKey, byte[] buffer) {
        try {
            Cipher rsa;
            rsa = Cipher.getInstance("RSA");
            rsa.init(Cipher.DECRYPT_MODE, decryptionKey);
            byte[] utf8 = rsa.doFinal(buffer);
            return new String(utf8, "UTF8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static KeyPair readKeyPair(File privateKey, String keyPassword) throws IOException {
        FileReader fileReader = new FileReader(privateKey);
        PEMReader r = new PEMReader(fileReader, new DefaultPasswordFinder(keyPassword.toCharArray()));
        try {
            return (KeyPair) r.readObject();
        } catch (IOException ex) {
            throw ex;
        } finally {
            r.close();
            fileReader.close();
        }
    }


    private static Key readPublicKey(File privateKey, String keyPassword) throws IOException {
        FileReader fileReader = new FileReader(privateKey);
        PEMReader r = new PEMReader(fileReader, new DefaultPasswordFinder(keyPassword.toCharArray()));
        try {
            return (RSAPublicKey) r.readObject();
        } catch (IOException ex) {
            throw ex;
        } finally {
            r.close();
            fileReader.close();
        }
    }


    private static class DefaultPasswordFinder implements PasswordFinder {


        char result[];


        public DefaultPasswordFinder(char psdarray[]) {
            result = psdarray;
        }


        @Override
        public char[] getPassword() {
            // TODO Auto-generated method stub
            return result;
        }


    }


    public static void main(String[] args) throws IOException {


        Security.addProvider(new BouncyCastleProvider());


        KeyPair keyPair = readKeyPair(new File(PRIVATE_PATH), "pass");
        // if the private key is not encripted, pass can be anything.
        Key publickey = readPublicKey(new File(PUBLIC_PATH), "pass");
        Base64 base64 = new Base64();
        String text = "this is the input text";
        byte[] encripted;
        System.out.println("input:\n" + text);
        encripted = encrypt(publickey, text);
        System.out.println("cipher:\n" + base64.encodeAsString(encripted));
        System.out.println("decrypt:\n" + decrypt(keyPair.getPrivate(), encripted));
    }

}
