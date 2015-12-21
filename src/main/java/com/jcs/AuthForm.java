package com.jcs;

import com.dropbox.core.*;
import com.dropbox.core.json.JsonReader;
import com.yandex.disk.client.Credentials;
import com.yandex.disk.client.TransportClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

/**
 * Created by Anderson on 01.11.2015.
 */

public class AuthForm extends  JFrame{
    static final String clientId = "0.1";
    static final String APP_KEY = "sesmmbymgegew78";
    static final String APP_SECRET = "jl1tqg9yliktnbb";
    static final String pathToAuthCodes = "JCS\\src\\main\\java\\com\\jcs\\authcodes.txt";
    static final String pathToAuthTokens = "JCS\\src\\main\\java\\com\\jcs\\tokens.txt";

    private MainFrame mainform;
    private JButton buttonAuth;
    private JTextField codeAuth[];
    static int anIntx = 0;
    static int anInty = 0;
    static final String dbxurl = "https://www.dropbox.com/1/oauth2/authorize?locale=ru_RU&client_id=sesmmbymgegew78&response_type=code";
    static final String yandexurl = "https://oauth.yandex.ru/authorize?response_type=token&client_id=5b9cc1c52a374da3a6f3f218c9d9ecc1";
    AuthForm() throws JsonReader.FileLoadException, IOException, DbxException {
           setTitle("Authorization window");
           setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
           setSize(1078, 720);
           setLocationRelativeTo(null);
           setLayout(new GridBagLayout());
           codeAuth = new JTextField[3];
           authdbx("dropbox", dbxurl);
           authdbx("google", "http://www.google.com");
           authdbx("yandex", yandexurl);

           JButton saveButton = new JButton("Save codes");
           saveButton.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   String dbxcode = codeAuth[0].getText();
                   String gdrivecode = codeAuth[1].getText();
                   String ydiskcode = codeAuth[2].getText();

                   PrintWriter writer = null;
                   try {
                       writer = new PrintWriter(pathToAuthCodes, "UTF-8");
                   } catch (FileNotFoundException e1) {
                       e1.printStackTrace();
                   } catch (UnsupportedEncodingException e1) {
                       e1.printStackTrace();
                   }
                   writer.println(dbxcode);
                   writer.println(gdrivecode);
                   writer.println(ydiskcode);
                   writer.close();
                   try {

                       DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
                       DbxRequestConfig config = new DbxRequestConfig(clientId, Locale.getDefault().toString());
                       DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);

                       // Have the user sign in and authorize your app.
                       String authorizeUrl = webAuth.start();
                       FileReader reader = new FileReader(pathToAuthCodes);
                       BufferedReader br = new BufferedReader(reader);
                       String code = br.readLine();
                       System.out.println(code);
                       DbxAuthFinish authFinish = webAuth.finish(code);

                       if (authFinish != null) {
                           System.out.println("Получаем Token");
                           String accessToken = authFinish.accessToken;
                           System.out.println("Token" + accessToken);
                           PrintWriter w = null;
                           try {
                               w = new PrintWriter(pathToAuthTokens, "UTF-8");
                           } catch (FileNotFoundException e1) {
                               e1.printStackTrace();
                           } catch (UnsupportedEncodingException e1) {
                               e1.printStackTrace();
                           }
                           System.out.println("Записываем токен в файл");
                           w.println(accessToken);
                           w.close();
                       }


                   } catch (Throwable s) {
                       System.out.println(s.toString());
                       return;
                   }
                   //setVisible(false);
                   try {
                       mainform = new MainFrame("JCS - multicloud system");
                   } catch (DbxException e1) {
                       e1.printStackTrace();
                   } catch (IOException e1) {
                       e1.printStackTrace();
                   } catch (JsonReader.FileLoadException e1) {
                       e1.printStackTrace();
                   }
                   dispose();
               }
           });
           pack();
           add(saveButton, new GridBagConstraints(anIntx, anInty++, 2, 1, 1, 1, GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
           setVisible(true);
    }

    public void authdbx(String name, final String url){
        buttonAuth = new JButton("Go to " + name + " authorization");
        if(name.equalsIgnoreCase("dropbox")) {
            buttonAuth.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //Execute when button is pressed
                    if (java.awt.Desktop.isDesktopSupported()) {
                        try {
                            System.out.println("Browser open!");
                            java.awt.Desktop.getDesktop().browse(new URI(url));
                        } catch (URISyntaxException ex) {

                        } catch (IOException ex) {
                            System.out.println("Go to " + url + "/");
                        }
                    }

                }
            });
        }
        if(name.equalsIgnoreCase("yandex")){
            buttonAuth.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //Execute when button is pressed
                    if (java.awt.Desktop.isDesktopSupported()) {
                        try {
                            System.out.println("Browser open!1");
                            java.awt.Desktop.getDesktop().browse(new URI(url));
                        } catch (URISyntaxException ex) {

                        } catch (IOException ex) {
                            System.out.println("Go to " + url + "/");
                        }
                    }

                }
            });
        }
        codeAuth[anInty] = new JTextField("Enter an authorization code");
        add(codeAuth[anInty],new GridBagConstraints(anIntx++, anInty, 1, 1, 1, 1, GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        add(buttonAuth,new GridBagConstraints(anIntx, anInty++, 1, 1, 1, 1, GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        anIntx = 0;
    }
    public static void main(String[] args) throws JsonReader.FileLoadException, IOException, DbxException {
        JFrame authFrame = new AuthForm();
    }
}
