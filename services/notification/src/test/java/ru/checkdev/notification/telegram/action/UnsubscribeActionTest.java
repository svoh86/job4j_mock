package ru.checkdev.notification.telegram.action;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.TelegramUser;
import ru.checkdev.notification.service.TelegramUserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class UnsubscribeActionTest {
    @MockBean
    private TelegramUserService telegramUserService;
    private UnsubscribeAction unsubscribeAction;
    private final Message message = new Message();
    private final Long chatId = 123L;
    private final Chat chat = new Chat(chatId, "group");

    @BeforeEach
    void beforeEach() {
        unsubscribeAction = new UnsubscribeAction(telegramUserService);
        message.setChat(chat);
    }

    @Test
    void callbackWhenUserNotExist() {
        when(telegramUserService.findByChatId(chatId)).thenReturn(Optional.empty());
        SendMessage actual = (SendMessage) unsubscribeAction.handle(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText()).contains("Вы не зарегистрированы");
    }

    @Test
    void callbackWhenUnsubscribeSuccess() {
        when(telegramUserService.findByChatId(chatId)).thenReturn(Optional.of(new TelegramUser()));
        SendMessage actual = (SendMessage) unsubscribeAction.handle(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText()).contains("Вы отменили подписку!");
    }
}