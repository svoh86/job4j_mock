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

/**
 * 3. Мидл
 * Класс реализует пункт меню подписки через ввод логина и пароля
 */
@AllArgsConstructor
@Slf4j
public class SubscribeAction implements Action {
    private static final String ERROR_OBJECT = "error";
    private static final String URL_AUTH_PERSON_CHECK = "/person/check";
    private final TgConfig tgConfig = new TgConfig("tg/", 8);
    private final TgAuthCallWebClint authCallWebClint;
    private final TelegramUserService telegramUserService;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatId = message.getChatId().toString();
        var sl = System.lineSeparator();
        var text = "";
        if (!telegramUserService.existsTelegramUserByChatId(message.getChatId())) {
            text = "Вы не зарегистрированы" + sl
                    + "/new";
            return new SendMessage(chatId, text);
        }
        text = "Введите почту и пароль в формате:" + sl
                + "email:password";
        return new SendMessage(chatId, text);
    }

    /**
     * Метод формирует ответ пользователю.
     * Весь метод разбит на 4 этапа проверки.
     * 1. Проверка на соответствие формату Email/Password введенного текста.
     * 2. Отправка данных в сервис Auth и если сервис не доступен сообщаем
     * 3. Если сервис доступен, получаем от него ответ и обрабатываем его.
     * 3.1 ответ при ошибке оформления подписки
     * 3.2 ответ при успешной подписке + обновляем TelegramUser в БД
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
        if (!tgConfig.isEmailAndPassword(sourceString)) {
            text = "Введите данные в формате email:password" + sl
                    + "попробуйте снова." + sl
                    + "/subscribe";
            return new SendMessage(chatId, text);
        }

        String[] strings = sourceString.split(":");
        var person = new PersonDTO();
        person.setEmail(strings[0]);
        person.setPassword(strings[1]);
        Object result;
        try {
            result = authCallWebClint.doPost(URL_AUTH_PERSON_CHECK, person).block();
        } catch (Exception e) {
            log.error("WebClient doPost error: {}", e.getMessage());
            text = "Сервис не доступен попробуйте позже" + sl
                    + "/start";
            return new SendMessage(chatId, text);
        }

        var mapObject = tgConfig.getObjectToMap(result);
        if (mapObject.containsKey(ERROR_OBJECT)) {
            text = "Ошибка оформления подписки: " + mapObject.get(ERROR_OBJECT);
            return new SendMessage(chatId, text);
        }

        TelegramUser telegramUser = telegramUserService.findByChatId(Long.parseLong(chatId)).get();
        telegramUser.setNotify(true);
        telegramUserService.save(telegramUser);
        text = "Подписка оформлена!";
        return new SendMessage(chatId, text);
    }
}
