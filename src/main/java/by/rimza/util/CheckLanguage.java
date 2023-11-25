package by.rimza.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckLanguage {

    public static boolean isEn (String word) {
        boolean result = false;
        Pattern pattern = Pattern.compile("[a-zA-Z]");
        Matcher matcher = pattern.matcher(word);

        if (matcher.find())
            result = true;

        return result;
    }

    public static boolean isRu (String word) {
        boolean result = false;
        Pattern pattern = Pattern.compile("[а-яА-я]");
        Matcher matcher = pattern.matcher(word);

        if (matcher.find())
            result = true;

        return result;
    }
}
