package com.azeesoft.libs.httprequester.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Aziz on 6/29/2015.
 */
public class QT {

    public final static String boldItalic = "-BoldItalic";
    public final static String light = "-Light";
    public final static String lightItalic = "-LightItalic";
    public final static String thin = "-Thin";
    public final static String thinItalic = "-ThinItalic";

    public final static String URL_PREFIX="https://www.azeesoft.com/timecard/php/";

    public static Typeface getRoboticFont(Context context) {
        return getRoboticFont(context, "-Regular");
    }

    public static Typeface getRoboticFont(Context context, String extra) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto" + extra + ".ttf");
    }

    public static void log(String s) {
        log("ASHLOG", s);
    }

    public static void log(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static void print(String s) {
        System.out.println(s);
    }

    public static void toast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    public static String getHashedText(String text){
        return text;
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static int getCheckedRadioPosition(RadioGroup rg) {
        if (rg.getCheckedRadioButtonId() == rg.getChildAt(0).getId()) {
            return 0;
        } else {
            return 1;
        }
    }

    public static byte[] getCommonPostData(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        String pword = sharedPreferences.getString("assp_admin_password", "");
        try {
            pword = URLEncoder.encode(pword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String postData = "seamlessposmobile=1&login=Login&uname=admin&pword=" + pword;

        QT.print(postData);
        return postData.getBytes();
    }

    public static String convertUPCeToUPCa(String UPCe){
        String UPCa=UPCe;
        if(UPCe.length()==8){
            char[] eChars=UPCe.toCharArray();
            int decisiveDigit= Integer.parseInt(""+eChars[6]);
            System.out.println("BD Decisive Digit:"+decisiveDigit);
            if(decisiveDigit>=0 && decisiveDigit<=2){
                UPCa=""+eChars[0]+eChars[1]+eChars[2]+eChars[6]+"0000"+eChars[3]+eChars[4]+eChars[5]+eChars[7];
                System.out.println("BD UPCA 0 to 2:"+UPCa);
            }else if(decisiveDigit==3){
                UPCa=""+eChars[0]+eChars[1]+eChars[2]+eChars[3]+"00000"+eChars[4]+eChars[5]+eChars[7];
                System.out.println("BD UPCA 3:"+UPCa);
            }else if(decisiveDigit==4){
                UPCa=""+eChars[0]+eChars[1]+eChars[2]+eChars[3]+eChars[4]+"00000"+eChars[5]+eChars[7];
                System.out.println("BD UPCA 4:"+UPCa);
            } else if(decisiveDigit>=5 && decisiveDigit<=9){
                UPCa=""+eChars[0]+eChars[1]+eChars[2]+eChars[3]+eChars[4]+eChars[5]+"0000"+eChars[6]+eChars[7];
                System.out.println("BD UPCA 5 to 9:"+UPCa);
            }


            System.out.println("BD UPCA:"+UPCa);
        }

        return UPCa;
    }

    public static String convertEAN8ToEAN13(String EAN8){
        String EAN13=EAN8;
        if(EAN8.length()==8){
            EAN13="00000"+EAN8;
        }

        return EAN13;
    }
}
