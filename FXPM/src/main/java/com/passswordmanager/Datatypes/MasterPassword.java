package com.passswordmanager.Datatypes;

import org.identityconnectors.common.security.GuardedString;

import java.security.SecureRandom;

public class MasterPassword {
    private boolean empty;
    private char[] password;
    private GuardedString guardedString;

    public MasterPassword(char[] password) {
        this.guardedString = new GuardedString(password);
        clearChar(password);
        empty = false;
    }

    public void generateGuardedString(char[] password) {
        this.guardedString = new GuardedString(password);
        clearChar(password);
        empty = false;
    }

    public void clearChar(char[] password) {
        if (password == null) return;
        /*
         * Securely wipe the char array by storing random values in it.
         * Some standards require multiple rounds of overwriting; see:
         * https://en.wikipedia.org/wiki/Data_erasure#Standards
         */
        SecureRandom sr = new SecureRandom();
        for (int i = 0; i < password.length; i++)
            password[i] = (char) sr.nextInt(Character.MAX_VALUE + 1);
        //noinspection UnusedAssignment
        password = null;
    }

    public void clearPasswordCache() {
        clearChar(this.password);
    }

    public void clearGuardedString() {
        this.guardedString.dispose();
        empty = true;
    }

    /**
     * !!!CALL clearPasswordCache AFTER THIS METHOD!!!
     *
     * @return char[] password
     */
    public String getPassword() {
        clearChar(this.password);
        this.guardedString.access(chars -> {
            this.password = new char[chars.length];
            System.arraycopy(chars, 0, this.password, 0, chars.length);
            clearChar(chars);
        });
        return String.valueOf(this.password);
    }

    public boolean isEmpty() {
        return empty;
    }
}