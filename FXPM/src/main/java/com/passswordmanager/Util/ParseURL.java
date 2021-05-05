package com.passswordmanager.Util;

import com.passswordmanager.Datatypes.Account;

import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class ParseURL extends Thread {

    private final String[] line;
    private final int posUrl;
    private final int posUName;
    private final int posPwd;
    private String title;
    private Account account;


    public ParseURL(String[] line, int posUrl, int posUName, int posPwd) {
        this.line = line;
        this.posUrl = posUrl;
        this.posUName = posUName;
        this.posPwd = posPwd;
    }

    @Override
    public void run() {
        System.out.println("started: " + this.getId());
        title = getTitleOfWebpage(line[posUrl]);
        account = new Account(line[posUName].replaceAll("\"", ""), line[posPwd].replaceAll("\"", ""));
        System.out.println("stoped: " + this.getId());
    }

    private String getTitleOfWebpage(String url) {
        HTMLEditorKit htmlKit = new HTMLEditorKit();
        HTMLDocument htmlDoc = (HTMLDocument) htmlKit.createDefaultDocument();
        HTMLEditorKit.Parser parser = new ParserDelegator();
        try {
            parser.parse(new InputStreamReader(new URL(url).openStream()),
                    htmlDoc.getReader(0), true);
            if (htmlDoc.getProperty("title") == null) return "";
            return htmlDoc.getProperty("title").toString();
        } catch (IOException e) {
            //urlErrors.add(url);
            e.printStackTrace();
        }
        return "";
    }

    public String getTitle() {
        return title;
    }

    public Account getAccount() {
        return account;
    }

    public String getUrl() {
        return line[posUrl];
    }
}
