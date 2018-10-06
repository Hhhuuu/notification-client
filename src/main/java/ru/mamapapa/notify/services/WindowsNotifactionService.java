package ru.mamapapa.notify.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mamapapa.NotificationService;

import java.awt.*;

/**
 * Уведомление в Windows
 *
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
public class WindowsNotifactionService implements NotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsNotifactionService.class);

    @Override
    public void sendNotify(String header, String body) {
        LOGGER.debug("Отправка уведомления в Windows. Заголовок: {},  тело: {}", header, body);
        try {
            notifyIfSupported(header, body);
        } catch (Exception e) {
            LOGGER.error("Ошибка при уведомлении в Windows", e);
        }

    }

    private static void notifyIfSupported(String header, String body) throws Exception {
        LOGGER.debug("Проверка поддержки уведомлений");
        if (SystemTray.isSupported()) {
            LOGGER.debug("Уведомления доступны");
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage("images/tray.gif");
            displayMessage(tray, image, header, body);
        }
    }

    private static void displayMessage(SystemTray tray, Image image, String header, String body) throws AWTException {
        LOGGER.debug("Уведомление пользователя");
        TrayIcon trayIcon = new TrayIcon(image);
        tray.add(trayIcon);
        trayIcon.displayMessage(header, body, TrayIcon.MessageType.INFO);
    }
}
