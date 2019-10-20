package ru.mamapapa.matchers;

import ru.mamapapa.property.Property;

import java.util.regex.Pattern;

import static ru.mamapapa.PropertyKey.TEXT_APPLICATION_IS_NOT_RUNNING;

/**
 * Матчер для поиска не запущенных приложений
 *
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
public class ApplicationNotRunTextMatcher extends BaseApplicationTextMatcher {

    private final Pattern pattern;

    /**
     * Конструктор
     * @param property - настройки
     */
    public ApplicationNotRunTextMatcher(Property property) {
        super(property);
        this.pattern = Pattern.compile(property.getString(TEXT_APPLICATION_IS_NOT_RUNNING));
    }

    protected Pattern getPattern(){
        return pattern;
    }
}
