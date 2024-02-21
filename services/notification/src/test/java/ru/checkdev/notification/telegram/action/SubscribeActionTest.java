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
import ru.checkdev.notification.domain.TelegramUser;
import ru.checkdev.notification.service.TelegramUserService;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;
import ru.checkdev.notification.telegram.util.TgConverterUtil;
import ru.checkdev.notification.telegram.util.TgValidatorUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class SubscribeActionTest {
    @MockBean
    private TelegramUserService telegramUserService;
    @MockBean
    private TgAuthCallWebClint authCallWebClint;
    private SubscribeAction subscribeAction;
    private final Message message = new Message();
    private final Long chatId = 123L;
    private final Chat chat = new Chat(chatId, "group");
    @MockBean
    private TgValidatorUtil tgValidatorUtil;
    @MockBean
    private TgConverterUtil tgConverterUtil;

    @BeforeEach
    void beforeEach() {
        subscribeAction = new SubscribeAction(authCallWebClint, telegramUserService);
        message.setChat(chat);
    }

    @Test
    void handleWhenUserNotExist() {
        when(telegramUserService.existsTelegramUserByChatId(123L)).thenReturn(false);
        SendMessage actual = (SendMessage) subscribeAction.handle(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText()).contains("Вы не зарегистрированы");
    }

    @Test
    void handleWhenUserExist() {
        when(telegramUserService.existsTelegramUserByChatId(123L)).thenReturn(true);
        SendMessage actual = (SendMessage) subscribeAction.handle(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText()).contains("Введите почту и пароль в формате:");
    }

    @Test
    void callbackWhenNotCorrectEmailOrPassword() {
        message.setText("email:password");
        when(!tgValidatorUtil.isEmailAndPassword(message.getText())).thenReturn(true);
        SendMessage actual = (SendMessage) subscribeAction.callback(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText()).contains("Введите данные в формате email:password");
    }

    @Test
    void callbackWhenServiceUnavailable() {
        message.setText("email@ya.ru:password");
        when(!tgValidatorUtil.isEmailAndPassword(message.getText())).thenReturn(false);
        when(authCallWebClint.doPost(anyString(), any())).thenThrow(new RuntimeException());
        SendMessage actual = (SendMessage) subscribeAction.callback(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText()).contains("Сервис не доступен попробуйте позже");
    }

    @Test
    void callbackWhenSubscribeError() {
        message.setText("email@ya.ru:password");
        when(!tgValidatorUtil.isEmailAndPassword(message.getText())).thenReturn(false);
        when(authCallWebClint.doPost(anyString(), any())).thenReturn(Mono.just(new LinkedHashMap<String, String>() {{
            put("error", "Пользователь не найден!");
        }}));
        when(tgConverterUtil.getObjectToMap(any())).thenReturn(Map.of("error", "error"));
        SendMessage actual = (SendMessage) subscribeAction.callback(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText()).contains("Ошибка оформления подписки: Пользователь не найден!");
    }

    @Test
    void callbackWhenSubscribeSuccess() {
        message.setText("email@ya.ru:password");
        when(!tgValidatorUtil.isEmailAndPassword(message.getText())).thenReturn(false);
        when(authCallWebClint.doPost(anyString(), any())).thenReturn(Mono.just(new LinkedHashMap<String, String>() {{
            put("ok", "ok");
        }}));
        when(tgConverterUtil.getObjectToMap(any())).thenReturn(Map.of("ok", "ok"));
        when(telegramUserService.findByChatId(chatId)).thenReturn(Optional.of(new TelegramUser()));
        SendMessage actual = (SendMessage) subscribeAction.callback(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText()).contains("Подписка оформлена!");
    }
}