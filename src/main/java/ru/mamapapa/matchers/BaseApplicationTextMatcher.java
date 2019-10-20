package ru.mamapapa.matchers;

import org.apache.commons.lang3.StringUtils;
import ru.mamapapa.Message;
import ru.mamapapa.TextMatcher;
import ru.mamapapa.property.Property;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.mamapapa.NotificationRun.getApplicationFactory;

/**
 * Базовый матчер для поиска приложений
 *
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
public abstract class BaseApplicationTextMatcher implements TextMatcher {

    private static final Pattern NAME_APPLICATION_PATTERN = Pattern.compile("[a-zA-Z]+");
    private final Property property;

    protected BaseApplicationTextMatcher(Property property) {
        this.property = property;
    }

    protected Property getProperty() {
        return property;
    }

    protected abstract Pattern getPattern();

    @Override
    public boolean matches(String text) {
        if (StringUtils.isEmpty(text)) {
            return false;
        }
        return getMatcher(text).matches();
    }

    private Matcher getMatcher(String text) {
        return getPattern().matcher(text);
    }

    @Override
    public Message matchesWithMessage(String text) {
        Matcher matcher = getMatcher(text);
        if (matcher.find()) {
            String group = matcher.group(0);
            if (StringUtils.isEmpty(group)) {
                return Message.EMPTY;
            }
            Matcher matcherApplicationName = NAME_APPLICATION_PATTERN.matcher(group);
            if (matcherApplicationName.find()) {
                String applicationName = matcherApplicationName.group(0).trim();
                if (getApplicationFactory().contains(applicationName, text)) {
                    return new Message(group, getProperty()).setStatus(true);
                }
            }
            return Message.EMPTY;
        }
        return Message.EMPTY;
    }
}
