package ru.mamapapa.matchers;

import org.apache.commons.lang3.StringUtils;
import ru.mamapapa.Message;
import ru.mamapapa.TextMatcher;
import ru.mamapapa.property.Property;

import static ru.mamapapa.PropertyKey.SEARCH_CONTAINS;

/**
 * Поиск подстроки в строке
 *
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
public class AnyTextMatcher implements TextMatcher {

    private String previousValue = "";
    private final Property property;
    private final String anyString;

    /**
     * Конструктор
     * @param property - настройки
     */
    public AnyTextMatcher(Property property) {
        this.property = property;
        this.anyString = property.getString(SEARCH_CONTAINS);
    }

    @Override
    public boolean matches(String text) {
        if (StringUtils.isEmpty(anyString) || StringUtils.isEmpty(text)) {
            return false;
        }

        if (text.contains(anyString) && !text.equals(previousValue)) {
            previousValue = text;
            return true;
        }

        return false;
    }

    @Override
    public Message matchesWithMessage(String text) {
        boolean matches = matches(text);
        if (matches) {
            return new Message(getBodyText(text), property).setStatus(true);
        }
        return Message.EMPTY;
    }

    private String getBodyText(String search) {
        return search.substring(search.indexOf(anyString));
    }
}
