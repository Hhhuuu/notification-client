package ru.mamapapa.notify.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mamapapa.Message;
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
    public void sendNotify(Message message) {
        LOGGER.debug("Отправка уведомления в Windows. Заголовок: {},  тело: {}", message.getHeader(), message.getBody());
        try {
            notifyIfSupported(message);
        } catch (Exception e) {
            LOGGER.error("Ошибка при уведомлении в Windows", e);
        }
    }

    private void notifyIfSupported(Message message) throws Exception {
        LOGGER.debug("Проверка поддержки уведомлений");
        if (SystemTray.isSupported()) {
            LOGGER.debug("Уведомления доступны");
            displayMessage(message);
        }
    }

    private void displayMessage(Message message) throws Exception {
        LOGGER.debug("Уведомление пользователя");
        if (trayIcon == null) {
            Image image = Toolkit.getDefaultToolkit().getImage("images/tray.gif");
            trayIcon = new TrayIcon(image);
            tray = SystemTray.getSystemTray();
            tray.add(trayIcon);
            trayIcon.addActionListener(actionEvent -> tray.remove(trayIcon));
        }
        trayIcon.displayMessage(message.getHeader(), message.getBody(), TrayIcon.MessageType.INFO);
    }
}
