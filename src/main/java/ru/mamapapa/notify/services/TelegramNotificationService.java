package ru.mamapapa.notify.services;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import ru.mamapapa.Message;
import ru.mamapapa.NotificationService;
import ru.mamapapa.exeption.NotifyException;
import ru.mamapapa.exeption.NotifyRuntimeException;
import ru.mamapapa.notify.NotificationResponse;
import ru.mamapapa.notify.services.dto.NotificationData;
import ru.mamapapa.property.Property;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static ru.mamapapa.ConsoleParamHelper.checkArg;
import static ru.mamapapa.ConsoleParamHelper.getArg;
import static ru.mamapapa.PropertyKey.TELEGRAM_CLIENT_IDS;
import static ru.mamapapa.PropertyKey.TELEGRAM_SERVER_NOTIFICATION;

/**
 * Уведомление в телеграм
 *
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
public class TelegramNotificationService implements NotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramNotificationService.class);
    private static final String SET_USER_ID = "Отсутствует обязательный параметр! Необходимо задать идентификатор пользователя телеграма!";

    private static final int DEFAULT_MAX_ATTEMPT = 3;
    private static AtomicInteger counterAttempt = new AtomicInteger(1);
    private final List<String> userIds;
    private final Property property;

    public TelegramNotificationService(String[] args, Property property) throws NotifyException {
        this.property = property;
        List<String> clientIds = getUserIds(args);
        if (CollectionUtils.isEmpty(clientIds)) {
            checkArg(null, SET_USER_ID);
        }
        this.userIds = clientIds;
    }

    private List<String> getUserIds(String[] args) throws NotifyException {
        if (ArrayUtils.isNotEmpty(args) && args.length == 2) {
            String arg = getArg(args, 1);
            checkArg(arg, SET_USER_ID);
            return Collections.singletonList(arg.trim());
        } else {
            String ids = property.getString(TELEGRAM_CLIENT_IDS, "");
            checkArg(ids, SET_USER_ID);
            ids = ids.replaceAll("\\s+", "");
            return Arrays.asList(ids.split(","));
        }
    }

    @Override
    public void sendNotify(Message message) {
        NotificationData postData = getPostData(message.getHeader(), message.getBody());
        LOGGER.debug("Отправка уведомления в телеграм. Данные для отправки: {}", postData);
        try {
            String url = property.getString(TELEGRAM_SERVER_NOTIFICATION);
            NotificationResponse response = getRestTemplate().postForObject(url.trim(), getHttpEntity(postData, getHttpHeaders()), NotificationResponse.class);
            resendingNotify(message, response);
        } catch (Exception e) {
            LOGGER.error("Не удалось отправить уведомление в телеграм", e);
        }
    }

    private void resendingNotify(Message message, NotificationResponse response) {
        if (!response.getResult()) {
            LOGGER.debug("Повторная отправка уведомления, ошибка предыдущей попытки: {}", response.getError());
            int attempt = counterAttempt.incrementAndGet();
            if (attempt < DEFAULT_MAX_ATTEMPT) {
                sendNotify(message);
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

    private RestTemplate getRestTemplate() throws Exception {
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
