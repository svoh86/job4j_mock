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
import java.util.Map;

/**
 * 3. Мидл
 * Класс реализует пункт меню регистрации нового пользователя в телеграм бот
 */
@AllArgsConstructor
@Slf4j
public class RegAction implements Action {
    private static final String ERROR_OBJECT = "error";
    private static final String URL_AUTH_REGISTRATION = "/registration";
    private final TgConfig tgConfig = new TgConfig("tg/", 8);
    private final TgAuthCallWebClint authCallWebClint;
    private final String urlSiteAuth;
    private final TelegramUserService telegramUserService;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatId = message.getChatId().toString();
        var sl = System.lineSeparator();
        if (telegramUserService.existsTelegramUserByChatId(message.getChatId())) {
            var validation = "Вы уже зарегистрированы!" + sl
                    + "/check";
            return new SendMessage(chatId, validation);
        }
        var text = "Введите username/email для регистрации:";
        return new SendMessage(chatId, text);
    }

    /**
     * Метод формирует ответ пользователю.
     * Весь метод разбит на 4 этапа проверки.
     * 1. Проверка на соответствие формату Username/Email введенного текста.
     * 2. Отправка данных в сервис Auth и если сервис не доступен сообщаем
     * 3. Если сервис доступен, получаем от него ответ и обрабатываем его.
     * 3.1 ответ при ошибке регистрации
     * 3.2 ответ при успешной регистрации + добавляем TelegramUser в БД
     *
     * @param message Message
     * @return BotApiMethod<Message>
     */
    @Override
    public BotApiMethod<Message> callback(Message message) {
        var chatId = message.getChatId().toString();
        var sourceString = message.getText();
        var text = "";
        var sl = System.lineSeparator();
        if (!tgConfig.isUsernameAndEmail(sourceString)) {
            text = "Введите данные в формате username/email" + sl
                    + "попробуйте снова." + sl
                    + "/new";
            return new SendMessage(chatId, text);
        }
        String[] strings = sourceString.split("/");
        var username = strings[0];
        var email = strings[1];

        var password = tgConfig.getPassword();
        var person = new PersonDTO(0, username, email, password, true, null,
                Calendar.getInstance(), false);
        Object result;
        try {
            result = authCallWebClint.doPost(URL_AUTH_REGISTRATION, person).block();
        } catch (Exception e) {
            log.error("WebClient doPost error: {}", e.getMessage());
            text = "Сервис не доступен попробуйте позже" + sl
                    + "/start";
            return new SendMessage(chatId, text);
        }

        var mapObject = tgConfig.getObjectToMap(result);
        if (mapObject.containsKey(ERROR_OBJECT)) {
            text = "Ошибка регистрации: " + mapObject.get(ERROR_OBJECT);
            return new SendMessage(chatId, text);
        }

        Map<String, Object> personMap = tgConfig.getObjectToMapWithValueObject(mapObject.get("person"));
        telegramUserService.save(
                new TelegramUser(Long.parseLong(chatId), (int) personMap.get("id")));
        text = "Вы зарегистрированы: " + sl
                + "Логин: " + email + sl
                + "Username: " + username + sl
                + "Пароль: " + password + sl
                + urlSiteAuth;
        return new SendMessage(chatId, text);
    }
}
