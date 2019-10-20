package ru.mamapapa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mamapapa.matchers.AnyTextMatcher;
import ru.mamapapa.matchers.ApplicationNotRunTextMatcher;
import ru.mamapapa.matchers.ApplicationRunTextMatcher;
import ru.mamapapa.matchers.TextMatcherFactory;
import ru.mamapapa.notify.services.NotificationServiceFactory;
import ru.mamapapa.property.Property;
import ru.mamapapa.property.PropertyService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static ru.mamapapa.ConsoleParamHelper.checkArg;
import static ru.mamapapa.ConsoleParamHelper.getArg;
import static ru.mamapapa.utils.ReaderUtils.getReader;

/**
 * Уведомления о запуске
 *
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
public class NotificationRun {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationRun.class);
    private static final String LIBERTY_NOT_FOUND = "Отсутствует обязательный параметр! Необходимо передать путь до папки";

    private static Property property;
    private static TextMatcherFactory textMatcherFactory;
    private static NotificationServiceFactory notificationServiceFactory;
    private static ApplicationFactory applicationFactory;

    /**
     * Оснвной метод для запуска
     * @param args - список аргументов
     * @throws Exception - исключение
     */
    public static void main(String[] args) throws Exception {
        checkArg(getArg(args, 0), LIBERTY_NOT_FOUND);
        initializeProperty();

        applicationFactory = ApplicationFactory.getInstance(property);

        initializeMatchers();

        notificationServiceFactory = new NotificationServiceFactory(args, property);

        startAnalyzingLog(new AnalysisParam(args, property));
    }

    private static void initializeProperty() throws Exception {
        property = new PropertyService();
        property.load();
    }

    private static void initializeMatchers() {
        textMatcherFactory = new TextMatcherFactory(
                new AnyTextMatcher(property),
                new ApplicationRunTextMatcher(property),
                new ApplicationNotRunTextMatcher(property)
        );
    }

    private static void startAnalyzingLog(final AnalysisParam analysisParam) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(() ->
                analyzeFile(analysisParam), analysisParam.getInitialDelay(), analysisParam.getDelay(), TimeUnit.SECONDS);
    }

    private static void analyzeFile(AnalysisParam analysisParam) {
        try {
            File file = new File(analysisParam.getPathToLogFile());
            if (!file.exists()) {
                LOGGER.warn("Файл не существует, продолжаем работать: {}", analysisParam.getPathToLogFile());
            } else {
                readFile(new FileInputStream(file));
            }
        } catch (Exception e) {
            LOGGER.error("Произошла ошибка: ", e);
        }
    }

    private static void readFile(InputStream inputStream) {
        LOGGER.trace("Чтение файла");
        try {
            try (BufferedReader reader = new BufferedReader(getReader(inputStream))) {
                String s;
                while ((s = reader.readLine()) != null) {
                    Message message = textMatcherFactory.matchesWithMessage(s);
                    if (message.isNeedSend()) {
                        LOGGER.debug("Найдено совпадение, уведомляем пользователя по всем каналам");
                        notificationServiceFactory.sendNotify(message);
//                        break;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Произошла ошибка при чтении файла: ", e);
        }
    }

    /**
     * @return список всех доступных приложений
     */
    public static ApplicationFactory getApplicationFactory() {
        return applicationFactory;
    }
}
