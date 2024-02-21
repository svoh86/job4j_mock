package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.PersonDTO;
import ru.checkdev.notification.service.TelegramUserService;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;
import ru.checkdev.notification.telegram.util.TgConverterUtil;

/**
 * 3. Мидл
 * Класс реализует пункт меню отмены подписки
 */
@AllArgsConstructor
@Slf4j
public class UnsubscribeAction implements Action {
    private static final String ERROR_OBJECT = "error";
    private static final String URL_AUTH_PERSON_UNSUBSCRIBE = "/person/unsubscribe";
    private final TgAuthCallWebClint authCallWebClint;
    private final TelegramUserService telegramUserService;
    private final TgConverterUtil tgConverterUtil = new TgConverterUtil();

    @Override
    public BotApiMethod<Message> handle(Message message) {
        return callback(message);
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        var chatId = message.getChatId().toString();
        var text = "";
        var sl = System.lineSeparator();
        var userOptional = telegramUserService.findByChatId(Long.parseLong(chatId));
        if (userOptional.isEmpty()) {
            text = "Вы не зарегистрированы" + sl
                    + "/new";
            return new SendMessage(chatId, text);
        }

        var person = new PersonDTO();
        person.setId(userOptional.get().getUserId());
        Object result;
        try {
            result = authCallWebClint.doPost(URL_AUTH_PERSON_UNSUBSCRIBE, person).block();
        } catch (Exception e) {
            log.error("WebClient doPost error: {}", e.getMessage());
            text = "Сервис не доступен попробуйте позже" + sl
                    + "/start";
            return new SendMessage(chatId, text);
        }

        var mapObject = tgConverterUtil.getObjectToMap(result);
        if (mapObject.containsKey(ERROR_OBJECT)) {
            text = "Ошибка отмены подписки: " + mapObject.get(ERROR_OBJECT);
            return new SendMessage(chatId, text);
        }

        text = "Вы отменили подписку!";
        return new SendMessage(chatId, text);
    }
}
