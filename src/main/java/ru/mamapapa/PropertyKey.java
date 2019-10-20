package ru.mamapapa;

import ru.mamapapa.property.Key;

/**
 * Настройки
 *
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
public enum PropertyKey implements Key {
    HEADER("header.message"),
    RELATIVE_PATH("relative.path.to.log.file"),
    INITIAL_DELAY("initial.delay"),
    DELAY("delay.before.next.read.file"),
    CHANNEL("channel.notification"),
    SEARCH_CONTAINS("search.contains"),
    TELEGRAM_SERVER_NOTIFICATION("TELEGRAM.server.notification"),
    TELEGRAM_CLIENT_IDS("client.ids"),
    APPLICATIONS("applications"),
    TEXT_APPLICATION_IS_RUNNING("text.application.is.running"),
    TEXT_APPLICATION_IS_NOT_RUNNING("text.application.is.not.running");


    private final String value;

    PropertyKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

