package org.hyperfit.utils;

//Bringing in commons-lang for 2 functions is silly
//also android complains a bit during compile
public class StringUtils {
    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0 || string.trim().length() == 0;
    }

    public static boolean equals(String left, String right) {
        return left == null ? right == null : left.equals(right);
    }


}
