package ru.checkdev.notification.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "cd_telegram_user")
public class TelegramUser {
    @EqualsAndHashCode.Include
    @Id
    @Column(name = "chat_id")
    private long chatId;
    @Column(name = "user_id")
    private int userId;
    private String email;
    private String username;
    private boolean notify;
}
