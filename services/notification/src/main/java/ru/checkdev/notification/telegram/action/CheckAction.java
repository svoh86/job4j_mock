package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.service.TelegramUserService;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;
import ru.checkdev.notification.telegram.util.TgConverterUtil;

/**
 * 3. Мидл
 * Класс реализует пункт меню выдачи информации о пользователе
 */
@AllArgsConstructor
@Slf4j
public class CheckAction implements Action {
    private final TgAuthCallWebClint authCallWebClint;
    private final TelegramUserService telegramUserService;
    private static final String URL_AUTH_FIND_BY_ID = "/profiles/";
    private final TgConverterUtil tgConverterUtil = new TgConverterUtil();

    @Override
    public BotApiMethod<Message> handle(Message message) {
        return callback(message);
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        var chatId = message.getChatId();
        var text = "";
        var sl = System.lineSeparator();
        var userOptional = telegramUserService.findByChatId(chatId);
        if (userOptional.isEmpty()) {
            text = "Вы не зарегистрированы" + sl
                    + "/new";
            return new SendMessage(chatId.toString(), text);
        }
        Object result;
        try {
            result = authCallWebClint.doGet(String.format(URL_AUTH_FIND_BY_ID + "%d", userOptional.get().getUserId())).block();
        } catch (Exception e) {
            log.error("WebClient doPost error: {}", e.getMessage());
            text = "Сервис не доступен попробуйте позже" + sl
                    + "/start";
            return new SendMessage(chatId.toString(), text);
        }
        var mapObject = tgConverterUtil.getObjectToMapWithValueObject(result);
        text = "Ваши данные: " + sl
                + "Username: " + mapObject.get("username") + sl
                + "Email: " + mapObject.get("email") + sl;
        return new SendMessage(chatId.toString(), text);
    }
}
