package com.passswordmanager.Util;

import com.passswordmanager.Datatypes.KeyShoutCut;

public abstract class KeyShortCutUtil {

    public static boolean validate(String keyBehaviour) {
        String[] statements = transform(keyBehaviour).split("\\+");
        for (String statement : statements) {
            if (!(statement.equals(KeyShoutCut.ENTER.name()) ||
                    statement.equals(KeyShoutCut.TAB.name()) ||
                    statement.equals(KeyShoutCut.PASSWORD.name()) ||
                    statement.equals(KeyShoutCut.USERNAME.name())
            ))
                return false;
        }
        return true;
    }

    public static String transform(String keyBehaviour) {
        return keyBehaviour.trim().toUpperCase();
    }

}
