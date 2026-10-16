package dev.franek.customitems.shared.util;

import java.util.regex.Pattern;

public class ValidationsUtils {
    public static boolean isInteger(String argument) {
        try {
            Integer.parseInt(argument);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isFloat(String argument) {
        try {
            Float.parseFloat(argument);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isBoolean(String argument) {
        try {
            Boolean.parseBoolean(argument);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String argument) {
        try {
            Double.parseDouble(argument);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isStringAlphanumeric(String s) {
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
        return !pattern.matcher(s).find();
    }
}

