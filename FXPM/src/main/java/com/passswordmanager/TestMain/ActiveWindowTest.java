package com.passswordmanager.TestMain;

import com.passswordmanager.Util.FileCrypt;
import com.passswordmanager.Util.Keyboard;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import javafx.scene.input.KeyCode;

import java.awt.*;
import java.awt.event.KeyEvent;

// see http://java-native-access.github.io/jna/4.0/javadoc/

public class ActiveWindowTest {
    private static final int MAX_TITLE_LENGTH = 1024;

    public static void main(String[] args) throws Exception {
        Keyboard keyboard = new Keyboard();
        String p = FileCrypt.generatePassword();
        //keyboard.type(FileCrypt.generatePassword());
        //keyboard.type("{asdf}{}{}{asdf}asdxnvn{}{}{}{}{}{}{}??ßß");
        keyboard.type(p);
        keyboard.press(KeyEvent.VK_ENTER);
        keyboard.type(p);
        keyboard.press(KeyEvent.VK_ENTER);
        keyboard.type(p);
        keyboard.press(KeyEvent.VK_ENTER);
        keyboard.type(p);
        keyboard.press(KeyEvent.VK_ENTER);
        keyboard.type(p);
        keyboard.press(KeyEvent.VK_ENTER);
        keyboard.type(p);
        keyboard.press(KeyEvent.VK_ENTER);
        p = FileCrypt.generatePassword();
        //keyboard.type(FileCrypt.generatePassword());
        //keyboard.type("{asdf}{}{}{asdf}asdxnvn{}{}{}{}{}{}{}??ßß");
        keyboard.type(p);
        keyboard.press(KeyEvent.VK_ENTER);
        keyboard.type(p);
        keyboard.press(KeyEvent.VK_ENTER);
        keyboard.type(p);
        keyboard.press(KeyEvent.VK_ENTER);
        keyboard.type(p);
        keyboard.press(KeyEvent.VK_ENTER);
        keyboard.type(p);
        keyboard.press(KeyEvent.VK_ENTER);
        keyboard.type(p);
        keyboard.press(KeyEvent.VK_ENTER);
        p = FileCrypt.generatePassword();
        //keyboard.type(FileCrypt.generatePassword());
        //keyboard.type("{asdf}{}{}{asdf}asdxnvn{}{}{}{}{}{}{}??ßß");
        keyboard.type(p);
        keyboard.press(KeyEvent.VK_ENTER);
        keyboard.type(p);
        keyboard.press(KeyEvent.VK_ENTER);
        keyboard.type(p);
        keyboard.press(KeyEvent.VK_ENTER);
        keyboard.type(p);
        keyboard.press(KeyEvent.VK_ENTER);
        keyboard.type(p);
        keyboard.press(KeyEvent.VK_ENTER);
        keyboard.type(p);
        keyboard.press(KeyEvent.VK_ENTER);
        p = FileCrypt.generatePassword();
        //keyboard.type(FileCrypt.generatePassword());
        //keyboard.type("{asdf}{}{}{asdf}asdxnvn{}{}{}{}{}{}{}??ßß");
        keyboard.type(p);
        keyboard.press(KeyEvent.VK_ENTER);
        keyboard.type(p);
        keyboard.press(KeyEvent.VK_ENTER);
        keyboard.type(p);
        keyboard.press(KeyEvent.VK_ENTER);
        keyboard.type(p);
        keyboard.press(KeyEvent.VK_ENTER);
        keyboard.type(p);
        keyboard.press(KeyEvent.VK_ENTER);
        keyboard.type(p);
        keyboard.press(KeyEvent.VK_ENTER);


    }
}

