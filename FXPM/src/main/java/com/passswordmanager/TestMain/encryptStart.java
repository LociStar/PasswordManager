package com.passswordmanager.TestMain;

import com.lambdaworks.crypto.SCryptUtil;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class encryptStart {
    public static void main(String[] args) {

        Scanner s = new Scanner(System.in);
        String password = s.nextLine();
        s.close();

        String hash = SCryptUtil.scrypt(password, 8, 8, 8);
        System.out.println(hash);

        boolean matched = SCryptUtil.check(password, hash);
        System.out.println(matched);

        matched = SCryptUtil.check("passwordno", hash);
        System.out.println(matched);
    }
}
