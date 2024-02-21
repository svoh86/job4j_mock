package ru.checkdev.notification.telegram.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 3. Мидл
 * Класс конвертирует объекты в мапу.
 */
@NoArgsConstructor
public class TgConverterUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Метод преобразовывает Object в карту Map<String,String>
     *
     * @param object Object or Person(Auth)
     * @return Map
     */
    public Map<String, String> getObjectToMap(Object object) {
        return MAPPER.convertValue(object, Map.class);
    }

    /**
     * Метод преобразовывает Object в карту Map<String,Object>
     *
     * @param object Object or Person(Auth)
     * @return Map
     */
    public Map<String, Object> getObjectToMapWithValueObject(Object object) {
        return MAPPER.convertValue(object, Map.class);
    }
}
