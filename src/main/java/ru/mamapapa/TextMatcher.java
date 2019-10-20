package ru.mamapapa;

/**
 * Интерфейс для поиска совпадений
 *
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
public interface TextMatcher {
    /**
     * Поиск совпадения
     * @param text - исходный текст
     * @return true - совпадение найдено, false - иначе
     */
    boolean matches(String text);

    /**
     * Поиск совпадения и вернуть сообщение для отправки
     * @param text - исходный текст
     * @return сообщение и статус для отправки сообщения
     */
    Message matchesWithMessage(String text);
}
