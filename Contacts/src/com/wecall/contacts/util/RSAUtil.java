package com.wecall.contacts.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import android.annotation.SuppressLint;
import android.util.Base64;

import com.wecall.contacts.constants.Constants;

public class RSAUtil {
	
	private static final String RSA_PUBLICE = Constants.PUBLIC_KEY;
	private static final String ALGORITHM = "RSA";  
	   
    /** 
     * �õ���Կ 
     * @param algorithm 
     * @param bysKey 
     * @return 
     */  
    private static PublicKey getPublicKeyFromX509(String algorithm,  
            String bysKey) throws NoSuchAlgorithmException, Exception {  
        byte[] decodedKey = Base64.decode(bysKey,Base64.DEFAULT);  
        X509EncodedKeySpec x509 = new X509EncodedKeySpec(decodedKey);  
   
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);  
        return keyFactory.generatePublic(x509);  
    }  
   
    /** 
     * ʹ�ù�Կ���� 
     * @param content 
     * @param key 
     * @return 
     */  
    @SuppressLint("TrulyRandom") public static String encryptByPublic(String content) {  
        try {  
            PublicKey pubkey = getPublicKeyFromX509(ALGORITHM, RSA_PUBLICE);  
   
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");  
            cipher.init(Cipher.ENCRYPT_MODE, pubkey);  
   
            byte plaintext[] = content.getBytes("UTF-8");  
            byte[] output = cipher.doFinal(plaintext);  
   
            String s = new String(Base64.encode(output,Base64.DEFAULT));  
   
            return s;  
   
        } catch (Exception e) {  
            return null;  
        }  
    }  
   
    /** 
    * ʹ�ù�Կ���� 
    * @param content ���� 
    * @param key �̻�˽Կ 
    * @return ���ܺ���ַ��� 
    */  
    public static String decryptByPublic(String content) {  
        try {  
            PublicKey pubkey = getPublicKeyFromX509(ALGORITHM, RSA_PUBLICE);  
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");  
            cipher.init(Cipher.DECRYPT_MODE, pubkey);  
            InputStream ins = new ByteArrayInputStream(Base64.decode(content,Base64.DEFAULT));  
            ByteArrayOutputStream writer = new ByteArrayOutputStream();  
            byte[] buf = new byte[128];  
            int bufl;  
            while ((bufl = ins.read(buf)) != -1) {  
                byte[] block = null;  
                if (buf.length == bufl) {  
                block = buf;  
                } else {  
                block = new byte[bufl];  
                for (int i = 0; i < bufl; i++) {  
                    block[i] = buf[i];  
                }  
                }  
                writer.write(cipher.doFinal(block));  
            }  
            return new String(writer.toByteArray(), "utf-8");  
        } catch (Exception e) {  
            return null;  
        }  
    }  
}