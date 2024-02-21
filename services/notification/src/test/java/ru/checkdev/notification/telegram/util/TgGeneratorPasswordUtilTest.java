package ru.checkdev.notification.telegram.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TgGeneratorPasswordUtilTest {
    private final String prefix = "pr/";
    private final int passSize = 10;
    private final TgGeneratorPasswordUtil tgGeneratorPasswordUtil = new TgGeneratorPasswordUtil(prefix, passSize);

    @Test
    void whenGetPasswordThenLengthPassSize() {
        var pass = tgGeneratorPasswordUtil.getPassword();
        assertThat(pass.length()).isEqualTo(passSize);
    }

    @Test
    void whenGetPasswordThenStartWishPrefix() {
        var pass = tgGeneratorPasswordUtil.getPassword();
        assertThat(pass.startsWith(prefix)).isTrue();
    }
}