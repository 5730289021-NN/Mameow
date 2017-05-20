package com.oaksmuth.mameow;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Oak on 18/3/2559.
 */
public class Helper {
    public static String reString(String string)
    {
        String encodedUrl = null;
        try {
            encodedUrl = URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
            // Can be safely ignored because UTF-8 is always supported
        }
        return encodedUrl;
    }

    public static String simplifyText(String text){
        text = text.trim();
        String cap = String.valueOf(text.charAt(0)).toUpperCase();
        text = text.substring(1,text.length());
        text = cap + text;
        return checkQuestion(text) ? (text + "?") : text;
    }

    public static boolean checkQuestion(String text){
        Scanner in = new Scanner(text);
        String[] questionList = {"What","Where","When","Why","Whom","Who","Whose","How","Have","Has","Is","Am","Are","Was","Were","Do","Does","Did","Will","Shall"};
        String focus = in.next();
        for(String s : questionList)
        {
            if (s.equals(focus))
            {
                in.close();
                return true;
            }
        }
        in.close();
        return false;
    }
}
