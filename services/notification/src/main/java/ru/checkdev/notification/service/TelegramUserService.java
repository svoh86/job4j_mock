package ru.checkdev.notification.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.checkdev.notification.domain.TelegramUser;
import ru.checkdev.notification.repository.TelegramUserRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class TelegramUserService {
    private final TelegramUserRepository telegramUserRepository;

    public TelegramUser save(TelegramUser telegramUser) {
        return telegramUserRepository.save(telegramUser);
    }

    public Optional<TelegramUser> findByChatId(long chatId) {
        return telegramUserRepository.findByChatId(chatId);
    }

    public boolean existsTelegramUserByChatId(long chatId) {
        return telegramUserRepository.existsTelegramUserByChatId(chatId);
    }
}
