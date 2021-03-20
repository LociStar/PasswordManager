package com.passswordmanager.TestMain;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GlobalKeyListenerExample implements NativeKeyListener {

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if ((e.getModifiers() & NativeKeyEvent.CTRL_MASK) != 0
                && (e.getModifiers() & NativeKeyEvent.SHIFT_MASK) != 0
                && e.getKeyCode() == NativeKeyEvent.VC_A) {
            System.out.println("PERFECT");
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {

    }

    public static void main(String[] args) {
        try {
            // Get the logger for "org.jnativehook" and set the level to warning.
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.WARNING);

// Don't forget to disable the parent handlers.
            logger.setUseParentHandlers(false);
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(new GlobalKeyListenerExample());
    }
}
