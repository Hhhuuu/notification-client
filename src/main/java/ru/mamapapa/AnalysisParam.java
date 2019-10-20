package ru.mamapapa;

import ru.mamapapa.property.*;

import static ru.mamapapa.PropertyKey.*;
import static ru.mamapapa.utils.PathUtils.deleteSlashIfNeed;

/**
 * Параметры для анализа файла
 *
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
public class AnalysisParam {
    private static final long DEFAULT_DELAY = 5L;

    private final Property property;
    private final String[] args;

    /**
     * Параметры для анализа файла
     * @param args - список аргументов консоли
     * @param property - настройки
     */
    public AnalysisParam(String[] args, Property property) {
        this.property = property;
        this.args = args;
    }

    public Long getInitialDelay() {
        return property.getLong(INITIAL_DELAY, DEFAULT_DELAY);
    }

    public Long getDelay() {
        return property.getLong(DELAY, DEFAULT_DELAY);
    }

    public String getPathToLogFile() {
        return deleteSlashIfNeed(args[0]) + property.getString(RELATIVE_PATH);
    }
}
