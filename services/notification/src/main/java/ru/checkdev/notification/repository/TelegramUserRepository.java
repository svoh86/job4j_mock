package ru.checkdev.notification.repository;

import org.springframework.data.repository.CrudRepository;
import ru.checkdev.notification.domain.TelegramUser;

import java.util.Optional;

public interface TelegramUserRepository extends CrudRepository<TelegramUser, Integer> {
    Optional<TelegramUser> findByChatId(long chatId);

    boolean existsTelegramUserByChatId(long chatId);
}
