package dev.acrispycookie.utility;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static boolean isInt(String s){
        try{
            Integer.parseInt(s);
            return true;
        } catch(Exception e){
            return false;
        }
    }

    public static Color hex2Rgb(String colorStr) {
        if(isColor(colorStr)){
            return new Color(
                    Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
                    Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
                    Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
        }
        return null;
    }

    public static boolean isColor(String hexadecimal){
        Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
        Matcher m = colorPattern.matcher(hexadecimal);
        return m.matches();
    }
}
