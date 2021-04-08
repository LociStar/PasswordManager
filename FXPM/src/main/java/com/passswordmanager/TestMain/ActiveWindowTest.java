package com.passswordmanager.TestMain;

import com.passswordmanager.Util.FileCrypt;
import com.passswordmanager.Util.Keyboard;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import javafx.scene.input.KeyCode;

import java.awt.*;
import java.awt.event.KeyEvent;

// see http://java-native-access.github.io/jna/4.0/javadoc/

public class ActiveWindowTest {
    private static final int MAX_TITLE_LENGTH = 1024;

    public static void main(String[] args) throws Exception {
        System.out.println(getActiveWindow());

    }

    public static String getActiveWindow() {
        //only Windows
        char[] buffer = new char[MAX_TITLE_LENGTH * 2];
        WinDef.HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        User32.INSTANCE.GetWindowText(hwnd, buffer, MAX_TITLE_LENGTH);
        System.out.println("Active window title: " + Native.toString(buffer));
        return Native.toString(buffer);
    }
}

