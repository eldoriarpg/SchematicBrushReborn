package de.eldoria.schematicbrush.util;

import com.google.common.collect.ObjectArrays;
import lombok.experimental.UtilityClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class ArrayUtil {
    /**
     * Checks if a string in a array matches a pattern.
     *
     * @param strings  strings to check
     * @param contains pattern to check against
     * @return matcher instance wich matches the string
     */
    public Matcher findInArray(String[] strings, Pattern contains) {
        for (String string : strings) {
            Matcher matcher = contains.matcher(string);
            if (matcher.find()) return matcher;
        }
        return null;
    }

    /**
     * Checks if a array contains any of the values.
     *
     * @param strings string to check
     * @param values  one or more value to check agains
     * @return true if a match was found
     */
    public boolean arrayContains(String[] strings, String... values) {
        for (String string : strings) {
            for (String contain : values) {
                if (string.equalsIgnoreCase(contain)) return true;
            }
        }
        return false;
    }

    /**
     * Checks if a array contains any of the values.
     *
     * @param chars  chars to check
     * @param values one or more value to check agains
     * @return true if a match was found
     */
    public boolean arrayContains(char[] chars, char... values) {
        for (char character : chars) {
            for (char contain : values) {
                if (character == contain) return true;
            }
        }
        return false;
    }

    /**
     * Combines two or more arrays.
     *
     * @param array  array to combine
     * @param arrays arrays to combine
     * @return one array
     */
    public String[] combineArrays(String[] array, String[]... arrays) {
        String[] result = array;
        for (String[] arr : arrays) {
            result = ObjectArrays.concat(arr, result, String.class);
        }
        return result;
    }
}
