# PasswordManager

Java-Version: 1.8

Use StartInBackground.java to start the program.
The default account is "test".

To automatically write a account to the according program, press STRG+ALT+A.

To change the account, start GenerateMPHash.java to generate a new hashed account and replace the String "hash" at Controllers.LoginPageController.java with the new generated hash.

For less memory usage, use -XX:+UseG1GC as JVM option.

Documentation: https://locistar.github.io/PasswordManager/
