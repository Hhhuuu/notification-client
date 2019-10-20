package ru.mamapapa.notify.services;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mamapapa.Message;
import ru.mamapapa.NotificationChannel;
import ru.mamapapa.NotificationService;
import ru.mamapapa.exeption.NotifyRuntimeException;
import ru.mamapapa.property.Property;

import java.util.EnumMap;
import java.util.Map;

import static ru.mamapapa.PropertyKey.CHANNEL;

/**
 * Фабрика сервисов рассылки
 *
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
public class NotificationServiceFactory implements NotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceFactory.class);
    private Map<NotificationChannel, NotificationService> servicesNotification = new EnumMap<>(NotificationChannel.class);

    /**
     * Конструктор
     * @param args - список аргументов
     * @param property - настройки
     */
    public NotificationServiceFactory(String[] args, Property property) {
        initialize(args, property);
    }

    private void initialize(String[] args, Property property) {
        LOGGER.info("Инициализация каналов для уведомлений");
        String channels = property.getString(CHANNEL);
        if (!StringUtils.isEmpty(channels)) {
            for (String channel : channels.split(",")) {
                try {
                    NotificationChannel notificationChannel = NotificationChannel.valueOf(channel.trim());
                    NotificationService notificationService = null;
                    switch (notificationChannel) {
                        case WINDOWS:
                            notificationService = new WindowsNotificationService();
                            break;
                        case TELEGRAM:
                            notificationService = new TelegramNotificationService(args, property);
                            break;
                        default:
                            notificationService = new LogNotificationService();
                            LOGGER.info("Уведомления для типа '{}' не поддерживаются", notificationChannel);
                            break;
                    }
                    servicesNotification.put(notificationChannel, notificationService);
                } catch (Exception e) {
                    throw new NotifyRuntimeException(String.format("Неизвестный тип канала! Значение: %s", channel), e);
                }
            }
        } else {
            throw new NotifyRuntimeException("Не заданы каналы для уведомлений!");
        }
    }

    @Override
    public void sendNotify(Message message) {
        servicesNotification.forEach((key, value) -> value.sendNotify(message));
    }
}
