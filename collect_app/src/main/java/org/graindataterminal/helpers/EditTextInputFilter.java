package org.graindataterminal.helpers;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditTextInputFilter implements InputFilter {
    public final static String PHONE_PATTERN = "\\+\\d{1}\\-\\d{3}\\-\\d{3}\\-\\d{2}\\-\\d{2}";
    public final static String DATE_PATTERN = "\\d{2}\\-\\d{2}\\-\\d{4}";

    private Pattern pattern = null;

    public EditTextInputFilter (String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String checkingText = dest.subSequence(0, dstart).toString() + source.subSequence(start, end).toString() + dest.subSequence(dend, dest.length()).toString();

        if (checkingText.length() > 0) {
            Matcher matcher = pattern.matcher(checkingText);
            if (!matcher.matches()) {
                if (!matcher.hitEnd()) {
                    if (checkingText.length() > 10)
                        return "";
                    else
                        return "-" + source.subSequence(start, end);
                }
            }
        }

        return null;
    }
}
