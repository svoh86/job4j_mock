package ru.checkdev.notification.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.checkdev.notification.service.TelegramUserService;
import ru.checkdev.notification.telegram.action.*;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

import java.util.List;
import java.util.Map;

/**
 * 3. Мидл
 * Инициализация телеграм бот,
 * username = берем из properties
 * token = берем из properties
 */
@Component
@Slf4j
public class TgRun {
    private final TgAuthCallWebClint tgAuthCallWebClint;
    @Value("${tg.username}")
    private String username;
    @Value("${tg.token}")
    private String token;
    @Value("${server.site.url.login}")
    private String urlSiteAuth;

    private final TelegramUserService telegramUserService;

    public TgRun(TgAuthCallWebClint tgAuthCallWebClint,
                 TelegramUserService telegramUserService) {
        this.tgAuthCallWebClint = tgAuthCallWebClint;
        this.telegramUserService = telegramUserService;
    }

    @Bean
    public void initTg() {
        Map<String, Action> actionMap = Map.of(
                "/start", new InfoAction(List.of(
                        "/start", "/new", "/check", "/forget", "/subscribe", "/unsubscribe")),
                "/new", new RegAction(tgAuthCallWebClint, urlSiteAuth, telegramUserService),
                "/check", new CheckAction(tgAuthCallWebClint, telegramUserService),
                "/forget", new ForgetAction(tgAuthCallWebClint, telegramUserService),
                "/subscribe", new SubscribeAction(tgAuthCallWebClint, telegramUserService),
                "/unsubscribe", new UnsubscribeAction(tgAuthCallWebClint, telegramUserService)
        );
        try {
            BotMenu menu = new BotMenu(actionMap, username, token);
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(menu);
        } catch (TelegramApiException e) {
            log.error("Telegram bot: {}, ERROR {}", username, e.getMessage());
        }
    }
}
