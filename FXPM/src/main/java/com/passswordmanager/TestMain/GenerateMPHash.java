package com.passswordmanager.TestMain;

import com.lambdaworks.crypto.SCryptUtil;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class GenerateMPHash {
    public static void main(String[] args) {

        System.out.print("Master Password: ");
        Scanner s = new Scanner(System.in);
        String password = s.nextLine();
        s.close();

        String hash = SCryptUtil.scrypt(password, 8, 8, 8);
        System.out.println("Hash:");
        System.out.println(hash);

        boolean matched = SCryptUtil.check(password, hash);
        System.out.println("Must be True: " + matched);

        matched = SCryptUtil.check("passwordno", hash);
        System.out.println("Must be false: " + matched);
    }
}
