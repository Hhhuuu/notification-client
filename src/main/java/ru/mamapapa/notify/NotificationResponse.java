package ru.mamapapa.notify;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Обертка над ответом
 *
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
public class NotificationResponse<T> {
    private Boolean result;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;

    public NotificationResponse() {
    }

    public static <T> NotificationResponse<T> goodResponse(T message) {
        final NotificationResponse<T> response = new NotificationResponse<>();
        response.result = true;
        response.message = message;
        return response;
    }

    public static NotificationResponse failResponse(Error error) {
        final NotificationResponse response = new NotificationResponse();
        response.result = false;
        response.error = error.getLocalizedMessage();
        return response;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public T getMessage() {
        return message;
    }

    public void setMessage(T message) {
        this.message = message;
    }
}
