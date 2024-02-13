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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CheckActionTest {
    @MockBean
    private TelegramUserService telegramUserService;
    private CheckAction checkAction;
    private final Message message = new Message();
    private final Long chatId = 123L;
    private final Chat chat = new Chat(chatId, "group");


    @BeforeEach
    void beforeEach() {
        checkAction = new CheckAction(telegramUserService);
        message.setChat(chat);
    }

    @Test
    public void callbackWhenUserNotExist() {
        when(telegramUserService.findByChatId(anyLong())).thenReturn(Optional.empty());
        SendMessage actual = (SendMessage) checkAction.handle(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText()).contains("Вы не зарегистрированы");
    }

    @Test
    public void callbackWhenUserExist() {
        when(telegramUserService.findByChatId(anyLong())).thenReturn(Optional.of(
                new TelegramUser(chatId, 321, "email", "username", false)));
        SendMessage actual = (SendMessage) checkAction.handle(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText()).contains("Ваши данные:");
        assertThat(actual.getText()).contains("email");
        assertThat(actual.getText()).contains("username");
    }
}