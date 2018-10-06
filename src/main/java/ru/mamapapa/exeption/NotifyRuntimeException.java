package ru.mamapapa.exeption;

/**
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
public class NotifyRuntimeException extends RuntimeException {
    public NotifyRuntimeException(String s) {
        super(s);
    }

    public NotifyRuntimeException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
