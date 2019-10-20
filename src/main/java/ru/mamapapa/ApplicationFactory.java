package ru.mamapapa;

import org.apache.commons.lang3.StringUtils;
import ru.mamapapa.cache.Cache;
import ru.mamapapa.property.Property;

import java.util.HashMap;
import java.util.Map;

import static ru.mamapapa.PropertyKey.APPLICATIONS;

/**
 * Фабрика всех приложений
 *
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
public class ApplicationFactory {

    private static Map<String, String> applications = new HashMap<>();
    private static ApplicationFactory instance;

    private ApplicationFactory(Property property) {
        initialize(property);
    }

    /**
     * Получить instance объекта
     * @param property - настройки
     * @return объект фабрики
     */
    public static ApplicationFactory getInstance(Property property) {
        if (instance == null) {
            synchronized (ApplicationFactory.class) {
                if (instance == null) {
                    instance = new ApplicationFactory(property);
                }
            }
        }
        return instance;
    }

    /**
     * Проверка, есть ли уже сообщение такое
     * @param applicationName - имя приложения
     * @param text - текст
     * @return true - сообщения не было, false - иначе
     */
    public synchronized boolean contains(String applicationName, String text) {
        boolean containsKey = applications.containsKey(applicationName);
        if (containsKey && !StringUtils.equals(applications.get(applicationName), text)) {
            applications.put(applicationName, text);
            Cache.saveApplications(applications);
            return true;
        }
        return false;
    }

    private void initialize(Property property) {
        String propertyValue = property.getString(APPLICATIONS);
        if (StringUtils.isEmpty(propertyValue)) {
            return;
        }

        Map<String, String> applicationsFromCache = Cache.getApplicationsFromCache();

        propertyValue = propertyValue.replace(" ", "");
        String[] applicationsList = propertyValue.split(",");

        for (String application : applicationsList) {
            String applicationFromCache = applicationsFromCache.get(application);
            applications.put(application, StringUtils.isNotEmpty(applicationFromCache) ? applicationFromCache : null);
        }

        Cache.saveApplications(applications);
    }
}
