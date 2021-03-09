package com.passswordmanager.TestMain;

import org.jasypt.util.text.AES256TextEncryptor;

public class En_Decrypt {
    public static void main (String[] args){
        AES256TextEncryptor aes256TextEncryptor = new AES256TextEncryptor();
        aes256TextEncryptor.setPassword("test");
        System.out.println(aes256TextEncryptor.encrypt("lol"));
        System.out.println(aes256TextEncryptor.encrypt("123"));
        System.out.println(aes256TextEncryptor.encrypt("ok"));
        System.out.println(aes256TextEncryptor.encrypt("222"));
    }
}
