package ru.checkdev.notification.telegram.action;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.domain.PersonDTO;
import ru.checkdev.notification.domain.TelegramUser;
import ru.checkdev.notification.service.TelegramUserService;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

import java.util.LinkedHashMap;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class UnsubscribeActionTest {
    @MockBean
    private TelegramUserService telegramUserService;
    @MockBean
    private TgAuthCallWebClint authCallWebClint;
    private UnsubscribeAction unsubscribeAction;
    private final Message message = new Message();
    private final Long chatId = 123L;
    private final Chat chat = new Chat(chatId, "group");

    @BeforeEach
    void beforeEach() {
        unsubscribeAction = new UnsubscribeAction(authCallWebClint, telegramUserService);
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
        var personDTO = new PersonDTO();
        when(telegramUserService.findByChatId(chatId)).thenReturn(Optional.of(new TelegramUser()));
        when(authCallWebClint.doPost(anyString(), any())).thenReturn(Mono.just(new LinkedHashMap<String, Object>() {{
            put("person", personDTO);
        }}));
        SendMessage actual = (SendMessage) unsubscribeAction.handle(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText()).contains("Вы отменили подписку!");
    }
}