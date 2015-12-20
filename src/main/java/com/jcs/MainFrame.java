package com.jcs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by Anderson on 31.10.2015.
 */
import com.dropbox.core.*;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.Files;

public class MainFrame extends JFrame {
    AuthForm auth = null;
    private JTextField btnNameField;
    private String createFolderName;
    private JButton addFolderBtn;
    private Panels LeftPanel;
    private Panels RightPanel;
    private JList accountList;
    private JList folderList;
    private String currentPath = "";
    private String path;
    DbxClientV2 client;
    private DefaultListModel listModel = new DefaultListModel();
    private ArrayList<Files.Metadata> entries;
    static final String clientId = "0.1";
    static final String pathToAuthTokens = "JCS\\src\\main\\java\\com\\jcs\\tokens.txt";
    MainFrame(String title) throws DbxException, IOException, JsonReader.FileLoadException {

        FileReader r = new FileReader(pathToAuthTokens);

        BufferedReader b = new BufferedReader(r);
        String token = b.readLine();
        if (token == null) {
            auth = new AuthForm();
        }else {
            DbxRequestConfig config = new DbxRequestConfig(clientId, Locale.getDefault().toString());
            client = new DbxClientV2(config, token);

           // OutputStream outStream = new FileOutputStream("dd"); - загрузка с дропбокса
           // client.files.downloadBuilder("lol").run(outStream);

            //InputStream inputStream = new FileInputStream("gg"); - загрузка на дропбокс
            // client.files.uploadBuilder("gg").run(inputStream);

            //client.files.delete("path"); удаление файлов

            //дизайн + дописать эти функции


            entries = client.files.listFolder("").entries;

            setTitle(title);
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setSize(1078, 720);
            setLocationRelativeTo(null);
            setLayout(new GridBagLayout());

            LeftPanel = new Panels();
            LeftPanel.setLayout(new GridBagLayout());

            accountList = new JList();
            LeftPanel.add(accountList, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

            RightPanel = new Panels();
            RightPanel.setLayout(new GridBagLayout());


            getUpdateFolders();

            folderList = new JList(listModel);
            //listModel.add(0,"...");
            folderList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    JList list = (JList) e.getSource();

                    if (e.getClickCount() == 2) {
                        // Double-click detected
                        int index = list.locationToIndex(e.getPoint());
                        if (index == 0){


                            if (currentPath == "" ) {

                            }
                            else{
                                int li = currentPath.lastIndexOf("/");
                                if (li == -1)
                                    currentPath = "";
                                else {
                                    currentPath = currentPath.substring(0,li);
                                }
                                try {
                                    getUpdateFolders(currentPath);
                                } catch (DbxException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        else {


                            ListModel lm = list.getModel();
                            path = lm.getElementAt(index).toString();
                            try {
                                getUpdateFolders(path);
                            } catch (DbxException e1) {
                                e1.printStackTrace();
                            }
                            currentPath = path;
                        }
                    }
                }
            });
            addFolderBtn = new JButton("add folder");
            btnNameField = new JTextField("name_new_folder");
            addFolderBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        createFolderName = btnNameField.getText();
                        client.files.createFolder(currentPath+"/"+createFolderName);
                        getUpdateFolders(currentPath);
                    } catch (DbxException e1) {
                        e1.printStackTrace();
                    }
                }
            });

            RightPanel.add(folderList,new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));


            //add(LeftPanel);
            add(RightPanel,new GridBagConstraints(1, 0, 1, 2, 1, 1, GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
            add(btnNameField,new GridBagConstraints(2, 0, 2, 2, 1, 1, GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
            add(addFolderBtn,new GridBagConstraints(0, 2, 4, 1, 1, 1, GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
            pack();
            setVisible(true);
        }


    }

    private void getUpdateFolders() throws DbxException {
        listModel.clear();
        listModel.add(0, "...");
        entries = client.files.listFolder("").entries;
        for (Files.Metadata metadata : entries) {
            listModel.addElement(metadata.pathLower);
        }
       // super.pack();
    }
    private void getUpdateFolders(String path) throws DbxException{
        listModel.clear();
        listModel.add(0, "...");
        entries = client.files.listFolder(path).entries;
        for (Files.Metadata metadata : entries) {
            listModel.addElement(metadata.pathLower);
        }
    }

    public static void main(String[] args) throws JsonReader.FileLoadException, IOException, DbxException {
        MainFrame mainform = new MainFrame("JCS - multicloud system");

    }
}
