package org.hyperfit.utils;

//Copied from commons-lang
//Don't want to bring it in for 2 functions
//also android complains a bit during compile about commons-lang
public class StringUtils {
    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0 || string.trim().length() == 0;
    }

    public static boolean equals(String left, String right) {
        return left == null ? right == null : left.equals(right);
    }


}
