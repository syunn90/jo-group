package com.jo.rpc.comm.utils;

import java.awt.*;

/**
 * 本地windows toast 推送
 * @author Jo
 * @date 2024/9/3
 */
public class WindowsToastUtil {

    public  static void showNotification(String title, String content) throws AWTException {
        if (SystemTray.isSupported()){
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage("path_to_icon");
            TrayIcon trayIcon = new TrayIcon(image, "Notification");
            trayIcon.setImageAutoSize(true);

            tray.add(trayIcon);
            trayIcon.displayMessage(title, content, TrayIcon.MessageType.INFO);
        }
    }

    public static void main(String[] args) throws AWTException {
        showNotification("34","aahahh");
    }

}
