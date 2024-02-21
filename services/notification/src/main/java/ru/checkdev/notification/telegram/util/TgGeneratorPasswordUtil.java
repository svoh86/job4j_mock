package ru.checkdev.notification.telegram.util;

import java.util.UUID;

/**
 * 3. Мидл
 * Класс генерация пароля.
 */
public class TgGeneratorPasswordUtil {
    private final String prefix;
    private final int passSize;

    public TgGeneratorPasswordUtil(String prefix, int passSize) {
        this.prefix = prefix;
        this.passSize = passSize;
    }

    public String getPassword() {
        String password = prefix + UUID.randomUUID();
        return password.substring(0, passSize);
    }
}
