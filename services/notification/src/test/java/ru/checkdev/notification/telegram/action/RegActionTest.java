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
import ru.checkdev.notification.service.TelegramUserService;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class RegActionTest {
    @MockBean
    private TelegramUserService telegramUserService;
    @MockBean
    private TgAuthCallWebClint authCallWebClint;
    private RegAction regAction;
    private final Message message = new Message();
    private final Long chatId = 123L;
    private final Chat chat = new Chat(chatId, "group");
    @MockBean
    private TgConfig tgConfig;

    @BeforeEach
    void beforeEach() {
        String urlSiteAuth = "http://localhost:8080/login";
        regAction = new RegAction(authCallWebClint, urlSiteAuth, telegramUserService);
        message.setChat(chat);
    }

    @Test
    void handleWhenUserExist() {
        when(telegramUserService.existsTelegramUserByChatId(123L)).thenReturn(true);
        SendMessage actual = (SendMessage) regAction.handle(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText()).contains("Вы уже зарегистрированы!");
    }

    @Test
    void handleWhenUserNotExist() {
        when(telegramUserService.existsTelegramUserByChatId(123L)).thenReturn(false);
        SendMessage actual = (SendMessage) regAction.handle(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText()).contains("Введите username/email для регистрации:");
    }

    @Test
    void callbackWhenNotCorrectUsernameOrEmail() {
        message.setText("user/email");
        when(!tgConfig.isUsernameAndEmail(message.getText())).thenReturn(true);
        SendMessage actual = (SendMessage) regAction.callback(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText()).contains("Введите данные в формате username/email");
    }

    @Test
    void callbackWhenServiceUnavailable() {
        message.setText("user/email@ya.ru");
        when(!tgConfig.isUsernameAndEmail(message.getText())).thenReturn(false);
        when(authCallWebClint.doPost(anyString(), any())).thenThrow(new RuntimeException());
        SendMessage actual = (SendMessage) regAction.callback(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText()).contains("Сервис не доступен попробуйте позже");
    }

    @Test
    void callbackWhenRegistrationError() {
        message.setText("user/email@ya.ru");
        when(!tgConfig.isUsernameAndEmail(message.getText())).thenReturn(false);
        when(authCallWebClint.doPost(anyString(), any())).thenReturn(Mono.just(new LinkedHashMap<String, String>(){{
            put("error", "Пользователь с такой почтой уже существует");
        }}));
        when(tgConfig.getObjectToMap(any())).thenReturn(Map.of("error", "error"));
        SendMessage actual = (SendMessage) regAction.callback(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText()).contains("Ошибка регистрации");
    }

    @Test
    void callbackWhenRegistrationSuccess() {
        PersonDTO personDTO = new PersonDTO();
        message.setText("user/email@ya.ru");
        when(!tgConfig.isUsernameAndEmail(message.getText())).thenReturn(false);
        when(authCallWebClint.doPost(anyString(), any())).thenReturn(Mono.just(new LinkedHashMap<String, Object>(){{
            put("person", personDTO);
        }}));
        when(tgConfig.getObjectToMap(any())).thenReturn(Map.of("person", "person"));
        when(tgConfig.getObjectToMapWithValueObject(any())).thenReturn(Map.of("person", personDTO));
        SendMessage actual = (SendMessage) regAction.callback(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText()).contains("Вы зарегистрированы:");
        assertThat(actual.getText()).contains("user");
        assertThat(actual.getText()).contains("email@ya.ru");
    }
}