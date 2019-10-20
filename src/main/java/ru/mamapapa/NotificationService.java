package ru.mamapapa;

/**
 * Сервис уведомлений
 *
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
public interface NotificationService {
    /**
     * Отправка уведомлений
     *
     * @param message - сообщение
     */
    void sendNotify(Message message);
}
