package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.PersonDTO;
import ru.checkdev.notification.domain.TelegramUser;
import ru.checkdev.notification.service.TelegramUserService;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

import java.util.Calendar;
import java.util.Optional;

/**
 * 3. Мидл
 * Класс реализует восстановление пароля
 */
@AllArgsConstructor
@Slf4j
public class ForgetAction implements Action {
    private final TgAuthCallWebClint authCallWebClint;
    private final TelegramUserService telegramUserService;
    private static final String URL_AUTH_FIND_BY_ID = "/profiles/";
    private final static String URL_AUTH_FORGOT = "/forgot";
    private static final String ERROR_OBJECT = "error";
    private final TgConfig tgConfig = new TgConfig("tg/", 8);

    @Override
    public BotApiMethod<Message> handle(Message message) {
        return callback(message);
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        var chatId = message.getChatId().toString();
        var text = "";
        var sl = System.lineSeparator();
        Optional<TelegramUser> userOptional = telegramUserService.findByChatId(Long.parseLong(chatId));
        if (userOptional.isEmpty()) {
            text = "Вы не зарегистрированы" + sl
                    + "/new";
            return new SendMessage(chatId, text);
        }
        var password = tgConfig.getPassword();
        Object result;
        try {
            result = authCallWebClint.doGet(String.format(URL_AUTH_FIND_BY_ID + "%d", userOptional.get().getUserId())).block();
            var mapObject = tgConfig.getObjectToMapWithValueObject(result);
            var person = new PersonDTO(
                    (int) mapObject.get("id"),
                    (String) mapObject.get("username"),
                    (String) mapObject.get("email"),
                    password,
                    false,
                    null,
                    Calendar.getInstance(),
                    false);
            result = authCallWebClint.doPost(URL_AUTH_FORGOT, person).block();
        } catch (Exception e) {
            log.error("WebClient error: {}", e.getMessage());
            text = "Сервис не доступен попробуйте позже" + sl
                    + "/start";
            return new SendMessage(chatId, text);
        }

        var mapObject = tgConfig.getObjectToMap(result);
        if (mapObject.containsKey(ERROR_OBJECT)) {
            text = "Ошибка: " + mapObject.get(ERROR_OBJECT);
            return new SendMessage(chatId, text);
        }

        text = "Ваш новый пароль: " + password;
        return new SendMessage(chatId, text);
    }
}
