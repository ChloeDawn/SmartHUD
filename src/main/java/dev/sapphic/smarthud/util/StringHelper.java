package dev.sapphic.smarthud.util;

public final class StringHelper {

    private StringHelper() {}

    /**
     * Abbreviates a number with a suffix after a certain length, e.g. 1500 -> 1.5k
     * @param value The number you want to abbreviate
     * @return The abbreviated number
     */
    public static String getAbbreviatedValue(int value) {
        StringBuilder abbr = new StringBuilder();
        int magnitude = (int) Math.floor(Math.log(value) / Math.log(1000.0D));
        int num = (int) (value / Math.pow(1000.0D, magnitude) * 10.0D);
        int integer = num / 10;
        int fractional = num % 10;
        abbr.append(integer);
        if (integer < 10 && fractional > 0) {
            abbr.append('.').append(fractional);
        }
        if (magnitude > 0) {
            abbr.append("kmbtpe".charAt(magnitude - 1));
        }
        return abbr.toString();
    }

}
