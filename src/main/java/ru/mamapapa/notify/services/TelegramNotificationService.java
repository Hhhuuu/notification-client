package ru.mamapapa.notify.services;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import ru.mamapapa.Notification;
import ru.mamapapa.NotificationService;
import ru.mamapapa.exeption.NotifyRuntimeException;
import ru.mamapapa.notify.NotificationResponse;
import ru.mamapapa.notify.services.dto.NotificationData;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.util.List;
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
    private static final int DEFAULT_MAX_ATTEMPT = 3;
    private static AtomicInteger counterAttempt = new AtomicInteger(1);
    private final List<String> userIds;

    public TelegramNotificationService(List<String> userIds) {
        this.userIds = userIds;
    }

    @Override
    public void sendNotify(String header, String body) {
        NotificationData postData = getPostData(header, body);
        LOGGER.debug("Отправка уведомления в телеграм. Данные для отправки: {}", postData);
        try {
            String url = Notification.getProperty().getString(TELEGRAM_SERVER_NOTIFICATION);
            NotificationResponse response = getRestTemplate().postForObject(url.trim(), getHttpEntity(postData, getHttpHeaders()), NotificationResponse.class);
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
        request.setUserId(userIds);
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

    private RestTemplate getRestTemplate() throws Exception
    {
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);
        return new RestTemplate(requestFactory);
    }
}
