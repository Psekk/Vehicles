package me.psek.vehicles.psekutils.conversationapi.utils;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {
    //credit to Ankoki <3 ily!
    /**
     * Utility to format convert a string into a coloured string, supporting
     * rainbow (&t) and pastel rainbows (&u).
     *
     * @param text the unformatted text.
     * @return formatted text.
     */
    public static String formatted(String text) {
        double freq1 = 0.3, freq2 = 0.3, freq3 = 0.3;
        double amp1 = 0, amp2 = 2, amp3 = 4;
        int center = 0;
        int width = 0;
        Pattern pattern = Pattern.compile("(<#[\\da-fA-F]{6}>)");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String matched = matcher.group(1)
                    .replace("<", "")
                    .replace(">", "");
            text = text.replace("<" + matched + ">", ChatColor.of(matched).toString());
        }
        char[] b = text.toCharArray();
        for(int i = 0; i < b.length - 1; ++i) {
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXxTtUu".indexOf(b[i + 1]) > -1) {
                b[i] = 167;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        text = new String(b);
        StringBuilder builder = new StringBuilder();
        int i = 0;
        boolean skippingHex = false;
        boolean isRainbow = false;
        int skipNo = 0;
        String currentColourCode = "§r";
        String currentModifier = "";
        for (String s : text.split("")) {
            if (skippingHex) {
                if (skipNo == 11) {
                    skippingHex = false;
                    continue;
                }
                skipNo++;
                continue;
            } else if (s.equals("§")) {
                i++;
                continue;
            } else if (s.equals(" ")) {
                i++;
                builder.append(" ");
                continue;
            } else if (i > 0) {
                if (text.charAt(i - 1) == '§') {
                    if (s.equalsIgnoreCase("t")) {
                        center = 128;
                        width = 127;
                        currentColourCode = "";
                        currentModifier = "";
                        isRainbow = true;
                        i++;
                        continue;
                    } else if (s.equalsIgnoreCase("u")) {
                        center = 200;
                        width = 55;
                        currentColourCode = "";
                        currentModifier = "";
                        isRainbow = true;
                        i++;
                        continue;
                    } else if (s.equalsIgnoreCase("x")) {
                        int x = i + 14;
                        if (text.length() >= x) {
                            char[] arr = new char[12];
                            System.arraycopy(text.toCharArray(), i + 1, arr, 0, 12);
                            String following = new String(arr);
                            pattern = Pattern.compile("((?:§[\\da-fA-F]){6})");
                            matcher = pattern.matcher(following);
                            if (matcher.find()) {
                                currentColourCode = "§x" + matcher.group(1);
                                i = i + 13;
                                skippingHex = true;
                                skipNo = 0;
                                isRainbow = false;
                                continue;
                            }
                        }
                    } else if ("klmnoKLMNO".contains(s)) {
                        currentModifier += "§" + s;
                        i++;
                        continue;
                    } else if ("abcdefrABCDEFR0123456789".contains(s)) {
                        currentColourCode = "§" + s;
                        currentModifier = "";
                        isRainbow = false;
                        i++;
                        continue;
                    }
                }
            }
            if (!isRainbow) {
                builder.append(currentColourCode)
                        .append(currentModifier).append(s);
                i++;
                continue;
            }
            float red = (float) (Math.sin(freq1 * i + amp1) * width + center);
            float green = (float) (Math.sin(freq2 * i + amp2) * width + center);
            float blue = (float) (Math.sin(freq3 * i + amp3) * width + center);
            if (red > 255 || red < 0) red = 0;
            if (green > 255 || green < 0) green = 0;
            if (blue > 255 || blue < 0) blue = 0;
            builder.append(ChatColor.of(new Color((int) red, (int) green, (int) blue)))
                    .append(currentModifier)
                    .append(s);
            i++;
        }
        return builder.toString();
    }
}
