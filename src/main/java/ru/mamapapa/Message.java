package ru.mamapapa;

import ru.mamapapa.property.Property;

import static ru.mamapapa.PropertyKey.HEADER;

/**
 * Сообщение, готовое к отправке
 *
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
public class Message {
    public static final Message EMPTY = new Message("", "");

    private final String header;
    private final String body;
    private boolean needSend = false;

    /**
     * Конструктор
     * @param body - тело сообщения
     * @param property - настройки для заполнения заголовка
     */
    public Message(String body, Property property) {
        this.header = property.getString(HEADER);
        this.body = body;
    }

    /**
     * Конструктор
     * @param header - заголовок
     * @param body - тело
     */
    private Message(String header, String body) {
        this.header = header;
        this.body = body;
    }

    /**
     * @return заголовок сообщения
     */
    public String getHeader() {
        return header;
    }

    /**
     * @return тело сообщения
     */
    public String getBody() {
        return body;
    }

    /**
     * @return true - сообщение необходимо отправлять
     */
    public boolean isNeedSend() {
        return needSend;
    }

    /**
     * Задать статус отправки
     * @param needSend - флаг
     * @return сообщение для отправки
     */
    public Message setStatus(boolean needSend) {
        this.needSend = needSend;
        return this;
    }
}
