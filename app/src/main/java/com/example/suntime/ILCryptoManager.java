package com.example.suntime;

import android.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ILCryptoManager {

    final private static String aesKey = "com.unvls0.1sync";
    final private static String transformation = "AES/CFB/NoPadding";
    final private static byte[] iv = new byte[] { 0x3f, (byte )0x8d, 0x73, 0x49, 0x58, 0x29, (byte) 0xaf, (byte) 0xb4, 0x6e, 0x30, 0x28, (byte) 0xdb, 0x6a, 0x7d, 0x19, (byte) 0xb3};
    final private static IvParameterSpec ivSpec = new IvParameterSpec(iv);
    final private static SecretKeySpec skeySpec = new SecretKeySpec(aesKey.getBytes(), "AES");

    static String aesEncrypted(String string) {
        if (string == null) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
            byte[] bytes = cipher.doFinal(string.getBytes());
            String encrypted = Base64.encodeToString(bytes, Base64.NO_WRAP);
            return encrypted;
        } catch (Exception error) {
            return null;
        }
    }

    static String aesDecrypted(String string) {
        if (string == null) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);
            byte[] bytes = cipher.doFinal(Base64.decode(string, Base64.NO_WRAP));
            String decrypted = new String(bytes);
            return decrypted;
        } catch (Exception error) {
            return null;
        }
    }
}
