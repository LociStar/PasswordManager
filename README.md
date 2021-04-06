# PasswordManager

Java-Version: 1.8

Use StartInBackground.java to start the entry.
The default password is "test".

To automatically write a password to the according entry, press STRG+ALT+A.

To change the password, start GenerateMPHash.java to generate a new hashed password and replace the String "hash" at Controllers.LoginPageController.java with the new generated hash.

For less memory usage, use -XX:+UseG1GC as JVM option.

Documentation: https://locistar.github.io/PasswordManager/
