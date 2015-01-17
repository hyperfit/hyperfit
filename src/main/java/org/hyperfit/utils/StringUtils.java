package org.hyperfit.utils;

//Bringing in commons-lang for 1 function is silly
//also the android guys have come compile problems with it...
public class StringUtils {
    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    public static String toLowerCase(String string) {
        return string == null ? null : string.toLowerCase();
    }

}
