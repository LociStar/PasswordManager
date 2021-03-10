# PasswordManager

Java-Version: 1.8

use StartInBackground.java to start the program
The default password is "test".

To change the password, start GenerateMPHash.java to generate a new hashed password and replace the String "hash" at Controllers.LoginPageController.java with the new generated hash.

For less memory usage, use -XX:+UseG1GC as JVM option.