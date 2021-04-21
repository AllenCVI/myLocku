package com.lockulockme.locku.base.utils;

public class MyPasswordUtils {
    private static final String[] chars = new String[]{"a", "b", "c", "d", "e", "f", "g",
            "h", "i", "j", "k", "l", "m", "n",
            "o", "p", "q", "r", "s", "t", "u",
            "v", "w", "x", "y", "z"};
    private static final String[] numbers = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    public static String getMyParamsPwd() {
        StringBuffer sb = new StringBuffer();
        sb.append(chars[21].toUpperCase()).append(chars[1].toUpperCase()).append(chars[14].toUpperCase()).append(chars[15].toUpperCase()).append(chars[20].toUpperCase())
                .append(numbers[1]).append(numbers[4]).append(chars[1]).append(chars[6].toUpperCase()).append(chars[7].toUpperCase()).append(chars[23])
                .append(chars[25]).append(chars[21].toUpperCase()).append(chars[15]).append(chars[23]).append(chars[22]);
        return sb.toString();
    }
}
