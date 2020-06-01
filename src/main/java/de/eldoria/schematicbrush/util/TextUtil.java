package de.eldoria.schematicbrush.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TextUtil {
    public int countChars(String string, char count) {
        int i = 0;
        for (char c : string.toCharArray()) {
            if (c == count) i++;
        }
        return i;
    }
}
