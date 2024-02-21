package ru.checkdev.notification.telegram.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TgValidatorUtilTest {
    private final TgValidatorUtil tgValidatorUtil = new TgValidatorUtil();

    @Test
    void whenIsEmailThenReturnTrue() {
        var email = "mail@mail.ru";
        var actual = tgValidatorUtil.isEmail(email);
        assertThat(actual).isTrue();
    }

    @Test
    void whenIsEmailThenReturnFalse() {
        var email = "mail.ru";
        var actual = tgValidatorUtil.isEmail(email);
        assertThat(actual).isFalse();
    }

    @Test
    void whenIsUsernameAndEmailThenReturnTrue() {
        var str = "user/mail@mail.ru";
        var actual = tgValidatorUtil.isUsernameAndEmail(str);
        assertThat(actual).isTrue();
    }

    @Test
    void whenIsUsernameAndEmailThenReturnFalse() {
        var str = "user-mail@mail.ru";
        var actual = tgValidatorUtil.isUsernameAndEmail(str);
        assertThat(actual).isFalse();
    }

    @Test
    void whenIsEmailAndPasswordThenReturnTrue() {
        var str = "mail@mail.ru:qwerty";
        var actual = tgValidatorUtil.isEmailAndPassword(str);
        assertThat(actual).isTrue();
    }

    @Test
    void whenIsEmailAndPasswordThenReturnFalse() {
        var str = "mail@mail.ru/qwerty";
        var actual = tgValidatorUtil.isEmailAndPassword(str);
        assertThat(actual).isFalse();
    }
}