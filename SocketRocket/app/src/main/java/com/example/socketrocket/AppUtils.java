package com.example.socketrocket;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.math.BigInteger;
import java.security.MessageDigest;

public class AppUtils {


    // MARK: UI

    public static void showAskIfContinueAlert(Context context, String title, final Runnable onContinue) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(title);
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface arg0, int arg1) {
                        onContinue.run();
                    }
                }
        );
        alertDialogBuilder.setNegativeButton("No", null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    // MARK: Security

    public static String md5(String message) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        md.reset();
        md.update(message.getBytes());
        byte[] digest = md.digest();
        BigInteger bigInt = new BigInteger(1,digest);
        String hashtext = bigInt.toString(16);
        while(hashtext.length() < 32 ){
            hashtext = "0"+hashtext; // zero padding
        }
        return hashtext;
    }

}
