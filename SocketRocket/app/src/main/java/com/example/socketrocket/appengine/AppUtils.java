package com.example.socketrocket.appengine;

import java.math.BigInteger;
import java.security.MessageDigest;

public class AppUtils {

    public static String md5(String message) {
        String plaintext = "your text here";
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        m.reset();
        m.update(plaintext.getBytes());
        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1,digest);
        String hashtext = bigInt.toString(16);
        // Now we need to zero pad it if you actually want the full 32 chars.
        while(hashtext.length() < 32 ){
            hashtext = "0"+hashtext;
        }
        return hashtext;
    }

}
