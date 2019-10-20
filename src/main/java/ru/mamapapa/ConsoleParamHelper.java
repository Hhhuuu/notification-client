package ru.mamapapa;

import org.apache.commons.lang3.StringUtils;
import ru.mamapapa.exeption.NotifyException;

/**
 * Хелпер для получения параметров из консоли
 *
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
public class ConsoleParamHelper {
    /**
     * Получение аргумента из консоли по индексу
     *
     * @param args  - список аргументов
     * @param index - индекс
     * @return значение параметра из консоли
     */
    public static String getArg(String[] args, int index) {
        return args.length - 1 >= index ? args[index] : "";
    }

    /**
     * Проверка параметра аргумента на пустоту
     * @param arg - аргумент
     * @param errorMessage - сообщение об ошибке
     * @throws NotifyException - исключение
     */
    public static void checkArg(String arg, String errorMessage) throws NotifyException {
        if (StringUtils.isEmpty(arg)) {
            throw new NotifyException(errorMessage);
        }
    }
}
