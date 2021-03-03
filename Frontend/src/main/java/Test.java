import Util.FileCrypt;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.jasypt.util.text.AES256TextEncryptor;

import java.io.FileNotFoundException;

public class Test {
    public static void main(String[] args) throws FileNotFoundException {
        //FileCrypt.getPasswords("L04M19TSenh").forEach((s, s2) -> System.out.println(s + ": " + s2));
        //System.out.println(FileCrypt.addPwToFile("Test", "test123", "L04M19TSenh"));
        //FileCrypt.getPasswords("L04M19TSenh").forEach((s, s2) -> System.out.println(s + ": " + s2));
        //System.out.println(FileCrypt.generatePassword());

        AES256TextEncryptor aes256TextEncryptor = new AES256TextEncryptor();
        aes256TextEncryptor.setPassword("L04M19TSenh");
        String password = FileCrypt.generatePassword();
        System.out.println("pw: " + password);
        String encrypted = aes256TextEncryptor.encrypt(password);
        System.out.println(encrypted);
        System.out.println(aes256TextEncryptor.decrypt(encrypted));

    }
}
