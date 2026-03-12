package com.revpasswordmanager.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionUtil {

    private static final String SECRET_KEY = "MySuperSecretKey";

    public static String encrypt(String data) {

        try {

            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encrypted = cipher.doFinal(data.getBytes());

            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {

            throw new RuntimeException("Encryption error");

        }

    }

    public static String decrypt(String data) {

        try {

            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(data));

            return new String(decrypted);

        } catch (Exception e) {

            throw new RuntimeException("Decryption error");

        }

    }

}