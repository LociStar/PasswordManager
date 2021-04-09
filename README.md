# PasswordManager

Java-Version: 16 <br />
Language Level: 14

**Start PM:**<br />
Executing StartInBackgroud.java without adding JavaFX moduls and path, will not start PM.

PM is configured with maven:
FXPM is the working directory for maven.
To start the PM use maven-command "javafx:run -X -f pom.xml" in FXPM
<br /><br />
**Informations:**<br />
The default password is "test".

To automatically write username + TAB + password to the according program, press STRG+ALT+A.<br />
To only write the password, press STRG+ALT+Y.
To create a new entry press STRG+ALT+X

To change the account, start GenerateMPHash.java to generate a new hashed account and replace the String "hash" at Controllers.LoginPageController.java with the new generated hash.

For less memory usage, use -XX:+UseG1GC as JVM option.

Documentation: https://locistar.github.io/PasswordManager/
