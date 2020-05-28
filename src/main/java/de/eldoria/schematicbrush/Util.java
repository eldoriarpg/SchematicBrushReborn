package de.eldoria.schematicbrush;

import com.google.common.collect.ObjectArrays;
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

    public boolean arrayContains(char[] chars, char... contains) {
        for (char character : chars) {
            for (char contain : contains) {
                if (character == contain) return true;
            }
        }
        return false;
    }

    public String[] combineArrays(String[]... arrays) {
        String[] result = new String[0];
        for (String[] array : arrays) {
            result = ObjectArrays.concat(array, result, String.class);
        }
        return result;
    }
}
