package com.passswordmanager.Util;

import com.passswordmanager.Database.DatabaseConnectionHandler;
import org.jasypt.util.text.AES256TextEncryptor;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * abstract class to Handle the encryption/ decryption and hashing
 */
public abstract class FileCrypt {

    /**
     * generate a decrypted Map of name-password
     *
     * @param masterPassword master password for decryption
     * @return Map(name, password)
     * @throws FileNotFoundException password list not found
     */
    public static Map<String, String> getPasswords(String masterPassword) throws FileNotFoundException {
        Map<String, String> hashMap = new HashMap<>();
        //ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        File file = new File(FileCrypt.class.getResource("/pw.txt").getPath());
        Scanner fileReader = new Scanner(file);
        while (fileReader.hasNextLine()) {
            String[] data = fileReader.nextLine().trim().split(":");
            AES256TextEncryptor aes256TextEncryptor = new AES256TextEncryptor();
            aes256TextEncryptor.setPassword(masterPassword);
            hashMap.put(aes256TextEncryptor.decrypt(data[0]), aes256TextEncryptor.decrypt(data[1]));
        }
        return hashMap;
    }

    /**
     * generate a decrypted Map of name-password
     *
     * @param masterPassword master password for decryption
     * @param db             DatabaseConnectionHandler
     * @return Map(name, password)
     */
    public static Map<String, Map<String, String>> getListDB(String masterPassword, DatabaseConnectionHandler db) {
        Map<String, String> pNames = db.getProgramNames();
        Map<String, Map<String, String>> hashMap = new HashMap<>();
        pNames.forEach((s, s2) -> hashMap.put(s + ":" + s2, db.getUsrPwNames(s)));
//        Map<String, String> output = new HashMap<>();
//        AES256TextEncryptor aes256TextEncryptor = new AES256TextEncryptor();
//        aes256TextEncryptor.setPassword(masterPassword);
//        hashMap.forEach((name, pw) -> output.put(name, aes256TextEncryptor.decrypt(pw)));
//        return output;
        return hashMap;
    }

    /**
     * adds a new encrypted name-password mapping to the password list file
     *
     * @param name           new name
     * @param password       new password
     * @param masterPassword master password for encryption
     * @return true if success
     */
    public static boolean addPwToFile(String name, String password, String masterPassword) {
        try {
            File file = new File(FileCrypt.class.getResource("/pw.txt").getPath());
            FileOutputStream fw = new FileOutputStream(file, true);

            fw.write((name + " : " + encryptText(password, masterPassword) + "\n").getBytes());
            fw.close();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * adds a new encrypted name-password mapping to the password list file
     *
     * @param name           new name
     * @param password       new password
     * @param masterPassword master password for encryption
     * @param db             DatabaseConnectionHandler
     * @return true if success
     */
    public static boolean addPwToDatabase(String name, String password, String masterPassword, String programName, String nickname, DatabaseConnectionHandler db) {
        try {
            db.insert(name, encryptText(password, masterPassword), programName, nickname);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public static String encryptText(String name, String masterPassword) {
        AES256TextEncryptor aes256TextEncryptor = new AES256TextEncryptor();
        aes256TextEncryptor.setPassword(masterPassword);
        return aes256TextEncryptor.encrypt(name);
    }

    public static String decryptText(String name, String masterPassword) {
        AES256TextEncryptor aes256TextEncryptor = new AES256TextEncryptor();
        aes256TextEncryptor.setPassword(masterPassword);
        return aes256TextEncryptor.decrypt(name);
    }

    /**
     * generate a new secure password
     *
     * @return password
     */
    public static String generatePassword() {
        PasswordGenerator gen = new PasswordGenerator();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(2);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(2);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(2);

        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return "ERROR_CODE";
            }

            public String getCharacters() {
                return "!@#$%^&*()_+§$&";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(2);

        return gen.generatePassword(32, splCharRule, lowerCaseRule,
                upperCaseRule, digitRule);
    }
}
