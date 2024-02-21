package ru.checkdev.notification.telegram.util;

import org.junit.jupiter.api.Test;
import ru.checkdev.notification.domain.PersonDTO;

import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;

class TgConverterUtilTest {
    private final TgConverterUtil tgConverterUtil = new TgConverterUtil();

    @Test
    void whenGetObjectToMapThenReturnObjectMap() {
        var personDto = new PersonDTO(0, "were", "mail", "pass", true, null, Calendar.getInstance(), false);
        var map = tgConverterUtil.getObjectToMap(personDto);
        assertThat(map.get("email")).isEqualTo(personDto.getEmail());
        assertThat(map.get("password")).isEqualTo(personDto.getPassword());
        assertThat(String.valueOf(map.get("privacy"))).isEqualTo(String.valueOf(true));
    }
}