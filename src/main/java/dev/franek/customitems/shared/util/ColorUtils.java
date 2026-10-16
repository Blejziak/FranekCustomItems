package dev.franek.customitems.shared.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;

public class ColorUtils {
    private static final Pattern hexPattern = Pattern.compile("(#[a-fA-F0-9]{6})");

    public static String fixColors(String string) {
        Matcher matcher = hexPattern.matcher(string);
        while (matcher.find()) {
            String hexCode = string.substring(matcher.start(), matcher.end());
            string = string.replace(hexCode, String.valueOf(ChatColor.of((String)hexCode)));
            matcher = hexPattern.matcher(string);
        }
        return ChatColor.translateAlternateColorCodes((char)'&', (String)string).replace(">>", "»");
    }

    public static List<String> fixColors(List<String> list) {
        ArrayList<String> output = new ArrayList<String>();
        for (String string : list) {
            output.add(ColorUtils.fixColors(string));
        }
        return output;
    }
}

