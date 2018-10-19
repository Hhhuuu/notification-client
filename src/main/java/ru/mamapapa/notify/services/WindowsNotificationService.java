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
public class WindowsNotificationService implements NotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsNotificationService.class);
    private TrayIcon trayIcon;
    private SystemTray tray;

    @Override
    public void sendNotify(String header, String body) {
        LOGGER.debug("Отправка уведомления в Windows. Заголовок: {},  тело: {}", header, body);
        try {
            notifyIfSupported(header, body);
        } catch (Exception e) {
            LOGGER.error("Ошибка при уведомлении в Windows", e);
        }

    }

    private void notifyIfSupported(String header, String body) throws Exception {
        LOGGER.debug("Проверка поддержки уведомлений");
        if (SystemTray.isSupported()) {
            LOGGER.debug("Уведомления доступны");
            displayMessage(header, body);
        }
    }

    private void displayMessage(String header, String body) throws Exception {
        LOGGER.debug("Уведомление пользователя");
        if (trayIcon == null) {
            Image image = Toolkit.getDefaultToolkit().getImage("images/tray.gif");
            trayIcon = new TrayIcon(image);
            tray = SystemTray.getSystemTray();
            tray.add(trayIcon);
        }
        trayIcon.displayMessage(header, body, TrayIcon.MessageType.INFO);
    }
}
