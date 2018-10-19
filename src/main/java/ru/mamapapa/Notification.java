package ru.mamapapa;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mamapapa.exeption.NotifyException;
import ru.mamapapa.exeption.NotifyRuntimeException;
import ru.mamapapa.notify.services.*;
import ru.mamapapa.property.*;

import java.io.*;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.*;

import static ru.mamapapa.Notification.PropertyKey.*;
import static ru.mamapapa.utils.PathUtils.deleteSlashIfNeed;
import static ru.mamapapa.utils.ReaderUtils.getReader;

/**
 * Уведомления о запуске
 *
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
public class Notification {
    private static final Logger LOGGER = LoggerFactory.getLogger(Notification.class);
    private static final String LIBERTY_NOT_FOUND = "Отсутствует обязательный параметр! Необходимо передать путь до папки";
    private static final String SET_USER_ID = "Отсутствует обязательный параметр! Необходимо задать идентификатор пользователя телеграма!";
    private static final long DEFAULT_DELAY = 5L;
    private static Property property;
    private static String previousStart;
    private static Map<NotificationChannel, NotificationService> servicesNotification = new EnumMap<>(NotificationChannel.class);

    public enum PropertyKey implements Key {
        HEADER("header.message"),
        RELATIVE_PATH("relative.path.to.log.file"),
        INITIAL_DELAY("initial.delay"),
        DELAY("delay.before.next.read.file"),
        CHANNEL("channel.notification"),
        SEARCH_CONTAINS("search.contains"),
        TELEGRAM_SERVER_NOTIFICATION("TELEGRAM.server.notification");

        private final String value;

        PropertyKey(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static void main(String[] args) throws Exception {
        checkArg(getArg(args, 0), LIBERTY_NOT_FOUND);

        initializeProperty();
        initializeNotificationServices(args);

        Long initialDelay = property.getLong(INITIAL_DELAY, DEFAULT_DELAY);
        Long delay = property.getLong(DELAY, DEFAULT_DELAY);
        String pathToLogFile = deleteSlashIfNeed(args[0]) + property.getString(RELATIVE_PATH);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(() -> {
            try {
                File file = new File(pathToLogFile);
                if (!file.exists()) {
                    LOGGER.warn("Файл не существует, продолжаем работать: {}", pathToLogFile);
                } else {
                    readFile(new FileInputStream(file));
                }
            } catch (Exception e) {
                LOGGER.error("Произошла ошибка: ", e);
            }
        }, initialDelay, delay, TimeUnit.SECONDS);
    }

    private static void initializeProperty() throws Exception {
        property = new PropertyService();
        property.load();
    }

    private static void initializeNotificationServices(String[] args) {
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
                            String arg = getArg(args, 1);
                            checkArg(arg, SET_USER_ID);
                            notificationService = new TelegramNotificationService(arg);
                            break;
                        default:
                            notificationService = new LogNotificationService();
                            LOGGER.info("Уведомления для типа '{}' не поддерживаются", notificationChannel);
                            break;
                    }
                    servicesNotification.put(notificationChannel, notificationService);
                } catch (Exception e) {
                    throw new NotifyRuntimeException("Неизвестный тип канала!", e);
                }
            }
        } else {
            throw new NotifyRuntimeException("Не заданы каналы для уведомлений!");
        }
    }

    private static void checkArg(String arg, String errorMessage) throws NotifyException {
        if (StringUtils.isEmpty(arg)) {
            throw new NotifyException(errorMessage);
        }
    }

    private static void readFile(InputStream inputStream) {
        LOGGER.trace("Чтение файла");
        String search = property.getString(SEARCH_CONTAINS);
        try {
            BufferedReader reader = new BufferedReader(getReader(inputStream));
            try {
                String s;
                while ((s = reader.readLine()) != null) {
                    if (s.contains(search) && !s.equals(previousStart)) {
                        LOGGER.debug("Найдено совпадение, уведомляем пользователя по всем каналам");
                        previousStart = s;
                        servicesNotification.forEach((key, value) -> value.sendNotify(getHeaderText(), getBodyText(search)));
                        break;
                    }
                }
            } finally {
                reader.close();
            }
        } catch (Exception e) {
            LOGGER.error("Произошла ошибка при чтении файла: ", e);
        }
    }

    private static String getHeaderText() {
        return property.getString(HEADER);
    }

    private static String getBodyText(String search) {
        return previousStart.substring(previousStart.indexOf(search), previousStart.length());
    }

    private static String getArg(String[] args, int index) {
        return args.length - 1 >= index ? args[index] : "";
    }

    public static Property getProperty() {
        return property;
    }
}
