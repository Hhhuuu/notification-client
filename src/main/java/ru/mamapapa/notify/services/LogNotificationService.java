package ru.mamapapa.notify.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mamapapa.NotificationService;

/**
 * Вывод в лог
 *
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
public class LogNotificationService implements NotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogNotificationService.class);

    @Override
    public void sendNotify(String header, String body) {
        LOGGER.debug("Отправка уведомления в лог. Заголовок: {},  тело: {}", header, body);
    }
}
