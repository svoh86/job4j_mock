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
import ru.checkdev.notification.telegram.util.TgConverterUtil;
import ru.checkdev.notification.telegram.util.TgGeneratorPasswordUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ForgetActionTest {
    @MockBean
    private TgAuthCallWebClint authCallWebClint;
    @MockBean
    private TelegramUserService telegramUserService;
    private ForgetAction forgetAction;
    private final Message message = new Message();
    private final Long chatId = 123L;
    private final Chat chat = new Chat(chatId, "group");
    @MockBean
    private TgGeneratorPasswordUtil tgGeneratorPasswordUtil;
    @MockBean
    private TgConverterUtil tgConverterUtil;

    @BeforeEach
    void beforeEach() {
        forgetAction = new ForgetAction(authCallWebClint, telegramUserService);
        message.setChat(chat);
    }

    @Test
    public void callbackWhenUserNotExist() {
        when(telegramUserService.findByChatId(anyLong())).thenReturn(Optional.empty());
        SendMessage actual = (SendMessage) forgetAction.handle(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText()).contains("Вы не зарегистрированы");
    }

    @Test
    void callbackWhenServiceUnavailable() {
        when(telegramUserService.findByChatId(anyLong())).thenReturn(Optional.of(new TelegramUser()));
        when(authCallWebClint.doPost(anyString(), any())).thenThrow(new RuntimeException());
        SendMessage actual = (SendMessage) forgetAction.callback(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText()).contains("Сервис не доступен попробуйте позже");
    }

    @Test
    void callbackWhenSuccess() {
        PersonDTO personDTO = new PersonDTO();
        var password = "qwerty";
        when(telegramUserService.findByChatId(anyLong())).thenReturn(Optional.of(new TelegramUser()));
        when(tgGeneratorPasswordUtil.getPassword()).thenReturn(password);
        when(authCallWebClint.doGet(anyString())).thenReturn(Mono.just(personDTO));
        when(authCallWebClint.doPost(anyString(), any())).thenReturn(Mono.just(new LinkedHashMap<String, Object>() {{
            put("person", personDTO);
        }}));
        when(tgConverterUtil.getObjectToMap(any())).thenReturn(Map.of("person", "person"));
        SendMessage actual = (SendMessage) forgetAction.callback(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText()).contains("Ваш новый пароль:");
    }
}