package com.passswordmanager.TestMain;

import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;


public class Test {
    private static final int MAX_TITLE_LENGTH = 1024;

    public static void main(String[] args) throws IOException {
        HTMLEditorKit htmlKit = new HTMLEditorKit();
        HTMLDocument htmlDoc = (HTMLDocument) htmlKit.createDefaultDocument();
        HTMLEditorKit.Parser parser = new ParserDelegator();
        parser.parse(new InputStreamReader(new URL("https://webmail.server.uni-frankfurt.de/login.php").openStream()),
                htmlDoc.getReader(0), true);

        System.out.println(htmlDoc.getProperty("title"));
    }


}

