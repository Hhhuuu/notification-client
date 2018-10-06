package ru.mamapapa.notify.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import ru.mamapapa.Notification;
import ru.mamapapa.NotificationService;
import ru.mamapapa.exeption.NotifyRuntimeException;
import ru.mamapapa.notify.NotificationResponse;
import ru.mamapapa.notify.services.dto.NotificationData;

import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static ru.mamapapa.Notification.PropertyKey.TELEGRAM_SERVER_NOTIFICATION;

/**
 * Уведомление в телеграм
 *
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
public class TelegramNotificationService implements NotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramNotificationService.class);
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    private static final int DEFAULT_MAX_ATTEMPT = 3;
    private static AtomicInteger counterAttempt = new AtomicInteger(1);
    private final String userId;

    public TelegramNotificationService(String userId) {
        this.userId = userId;
    }

    @Override
    public void sendNotify(String header, String body) {
        NotificationData postData = getPostData(header, body);
        LOGGER.debug("Отправка уведомления в телеграм. Данные для отправки: {}", postData);
        try {
            String url = Notification.getProperty().getString(TELEGRAM_SERVER_NOTIFICATION);
            NotificationResponse response = REST_TEMPLATE.postForObject(url.trim(), getHttpEntity(postData, getHttpHeaders()), NotificationResponse.class);
            resendingNotify(header, body, response);
        } catch (Exception e) {
            LOGGER.error("Не удалось отправить уведомление в телеграм", e);
        }
    }

    private void resendingNotify(String header, String body, NotificationResponse response) {
        if (!response.getResult()) {
            LOGGER.debug("Повторная отправка уведомления, ошибка предыдущей попытки: {}", response.getError());
            int attempt = counterAttempt.incrementAndGet();
            if (attempt < DEFAULT_MAX_ATTEMPT) {
                sendNotify(header, body);
            } else {
                counterAttempt.set(1);
                throw new NotifyRuntimeException("Количество попыток исчерпано!");
            }
        } else {
            counterAttempt.set(1);
            LOGGER.debug("Уведомление успешно отправлено");
        }
    }

    private NotificationData getPostData(String header, String body) {
        NotificationData request = new NotificationData();
        request.setHeader(header);
        request.setBody(body);
        request.setUserId(userId);
        return request;
    }

    @SuppressWarnings("unchecked")
    private HttpEntity getHttpEntity(Object postData, HttpHeaders headers) {
        return new HttpEntity(postData, headers);
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        return headers;
    }
}
