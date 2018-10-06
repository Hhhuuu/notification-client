package ru.mamapapa.notify.services.dto;

/**
 * Данные уведомления
 *
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
public class NotificationData {
    private String header;
    private String body;
    private String userId;

    public void setHeader(String header) {
        this.header = header;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "{" +
              "header: " + header + " " +
              "body: " + body + " " +
              "userId: " + userId + " " +
              '}';
    }
}
