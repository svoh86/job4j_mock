package ru.checkdev.notification.telegram.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 3. Мидл
 * Класс проверки почты, пароля и username.
 */
public class TgValidatorUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,4}");
    private static final Pattern USERNAME_EMAIL_PATTERN = Pattern.compile("\\w+/" + EMAIL_PATTERN);
    private static final Pattern EMAIL_PASSWORD_PATTERN = Pattern.compile(EMAIL_PATTERN + ":.+");

    /**
     * Метод проверяет входящую строку на соответствие формату email
     *
     * @param email String
     * @return boolean
     */
    public boolean isEmail(String email) {
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    /**
     * Метод проверяет входящую строку на соответствие формату username/email
     *
     * @param sourceString String
     * @return boolean
     */
    public boolean isUsernameAndEmail(String sourceString) {
        Matcher matcher = USERNAME_EMAIL_PATTERN.matcher(sourceString);
        return matcher.matches();
    }

    public boolean isEmailAndPassword(String sourceString) {
        Matcher matcher = EMAIL_PASSWORD_PATTERN.matcher(sourceString);
        return matcher.matches();
    }
}
