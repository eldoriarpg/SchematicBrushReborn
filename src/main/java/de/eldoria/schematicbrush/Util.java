package de.eldoria.schematicbrush;

import lombok.experimental.UtilityClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class Util {
    public Matcher findInArray(String[] strings, Pattern contains) {
        for (String string : strings) {
            Matcher matcher = contains.matcher(string);
            if (matcher.find()) return matcher;
        }
        return null;
    }

    public boolean arrayContains(String[] strings, String... contains) {
        for (String string : strings) {
            for (String contain : contains) {
                if (string.equalsIgnoreCase(contain)) return true;
            }
        }
        return false;
    }
}
