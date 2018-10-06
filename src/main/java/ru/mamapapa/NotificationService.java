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
     * @param header - заголовок сообщения
     * @param body   - тело сообщения
     */
    void sendNotify(String header, String body);
}
