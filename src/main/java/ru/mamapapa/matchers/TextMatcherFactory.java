package ru.mamapapa.matchers;

import ru.mamapapa.Message;
import ru.mamapapa.TextMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.mamapapa.Message.EMPTY;

/**
 * Фабрика матчеров
 *
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
public class TextMatcherFactory implements TextMatcher {

    private List<TextMatcher> textMatchers = new ArrayList<>();

    /**
     * Конструктор
     * @param textMatchers - список матчеров
     */
    public TextMatcherFactory(TextMatcher... textMatchers) {
        this.textMatchers = Arrays.asList(textMatchers);
    }

    @Override
    public boolean matches(String text) {
        for (TextMatcher matcher : textMatchers) {
            if (matcher.matches(text))
                return true;
        }
        return false;
    }

    @Override
    public Message matchesWithMessage(String text) {
        for (TextMatcher matcher : textMatchers) {
            Message message = matcher.matchesWithMessage(text);
            if (message.isNeedSend())
                return message;
        }
        return EMPTY;
    }
}
