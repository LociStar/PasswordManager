package com.passswordmanager.Util;

import org.jasypt.util.text.AES256TextEncryptor;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

import java.io.*;
import java.util.*;

public abstract class FileCrypt {
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

    public static boolean addPwToFile(String name, String password, String masterPassword) {
        try {
            File file = new File(FileCrypt.class.getResource("/pw.txt").getPath());
            FileOutputStream fw = new FileOutputStream(file, true);

            AES256TextEncryptor aes256TextEncryptor = new AES256TextEncryptor();
            aes256TextEncryptor.setPassword(masterPassword);
            fw.write((aes256TextEncryptor.encrypt(name) + " : " + aes256TextEncryptor.encrypt(password) + "\n").getBytes());
            fw.close();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public static String generatePassword(){
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
                return "!@#$%^&*()_+";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(2);

        return gen.generatePassword(32, splCharRule, lowerCaseRule,
                upperCaseRule, digitRule);
    }
}
