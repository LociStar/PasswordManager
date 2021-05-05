package com.passswordmanager.Util;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

public class ActiveWindow {
    private final int MAX_TITLE_LENGTH;
    private final String[] browsers = {"Google Chrome", "Opera", "Mozilla Firefox"};

    public ActiveWindow(int MAX_TITLE_LENGTH) {
        this.MAX_TITLE_LENGTH = MAX_TITLE_LENGTH;
    }

    public String getActiveWindow() {
        char[] buffer = new char[MAX_TITLE_LENGTH * 2];
        WinDef.HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        User32.INSTANCE.GetWindowText(hwnd, buffer, MAX_TITLE_LENGTH);
        return filter(Native.toString(buffer).trim());
    }

    private String filter(String title) {
        for (int i = 0; i < browsers.length; i++) {
            if (title.endsWith(browsers[i])) {
                return title.substring(0, title.length() - browsers[i].length() - 2).trim();
            }
        }
        return title;
    }
}
