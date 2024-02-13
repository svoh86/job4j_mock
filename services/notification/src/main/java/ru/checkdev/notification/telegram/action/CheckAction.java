package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.service.TelegramUserService;

/**
 * 3. Мидл
 * Класс реализует пункт меню выдачи информации о пользователе
 */
@AllArgsConstructor
@Slf4j
public class CheckAction implements Action {
    private final TelegramUserService telegramUserService;

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
        text = "Ваши данные: " + sl
                + "Username: " + userOptional.get().getUsername() + sl
                + "Email: " + userOptional.get().getEmail() + sl;
        return new SendMessage(chatId.toString(), text);
    }
}
