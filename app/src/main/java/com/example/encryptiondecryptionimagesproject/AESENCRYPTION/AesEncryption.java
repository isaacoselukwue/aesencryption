package com.example.encryptiondecryptionimagesproject.AESENCRYPTION;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesEncryption {
    private final static int READ_WRITE_BLOCK_BUFFER = 1024;
    private final static String AlgoImageEncrpytor = "AES/CBC/PKCS5Padding";
    private final static String AlgoSecretKey = "AES";

    public static void encrypt_file(String keyString, String keySpec, InputStream in, OutputStream out) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        try{
            IvParameterSpec iv = new IvParameterSpec(keySpec.getBytes("UTF-8"));

            SecretKeySpec keySpec1 = new SecretKeySpec(keyString.getBytes("UTF-8"),AlgoSecretKey);

            Cipher c = Cipher.getInstance(AlgoImageEncrpytor);
            c.init(Cipher.ENCRYPT_MODE, keySpec1,iv);
            out = new CipherOutputStream(out,c);
            int count = 0;
            byte[] buffer = new byte[READ_WRITE_BLOCK_BUFFER];
            while ((count = in.read(buffer)) > 0){
                out.write(buffer,0,count);

            }

        } finally {
            out.close();
        }

    }

    public static void decrypt_file(String keyString, String keySpec, InputStream in, OutputStream out) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        try{
            IvParameterSpec iv = new IvParameterSpec(keySpec.getBytes("UTF-8"));

            SecretKeySpec keySpec1 = new SecretKeySpec(keyString.getBytes("UTF-8"),AlgoSecretKey);

            Cipher c = Cipher.getInstance(AlgoImageEncrpytor);
            c.init(Cipher.DECRYPT_MODE, keySpec1,iv);
            out = new CipherOutputStream(out,c);
            int count = 0;
            byte[] buffer = new byte[READ_WRITE_BLOCK_BUFFER];
            while ((count = in.read(buffer)) > 0){
                out.write(buffer,0,count);

            }

        } finally {
            out.close();
        }

    }

}
