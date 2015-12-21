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
    private JTextField nameUploadFile;
    private String createFolderName;
    private String uploadingFileName;
    private JButton addFolderBtn;
    private JButton uploadFileBtn;
    private JButton downloadFileBtn;
    private Panels LeftPanel;
    private Panels RightPanel;
    private JList accountList;
    private JList folderList;
    private String currentPath = "";
    private int li = -1;
    private String path;
    DbxClientV2 client;
    private DefaultListModel listModel = new DefaultListModel();
    private ArrayList<Files.Metadata> entries;
    static final String clientId = "0.1";
    static String pathToAuthTokens;
    boolean acceptDonwload = false;
    String fileDir = "";
    String projectPath;
    String adp;
    String additionalPath;
    String dir = "";
    String accessChooser = "";
    boolean access = false;
    String nameFile;
    MainFrame(String title) throws DbxException, IOException, JsonReader.FileLoadException {
        projectPath = new File(".").getCanonicalPath();
        adp = "\\src\\main\\java\\com\\jcs";
        additionalPath = "\\src\\main\\java\\com\\jcs\\tokens.txt";
        pathToAuthTokens = projectPath + additionalPath;
        FileReader r = new FileReader(pathToAuthTokens);
        BufferedReader b = new BufferedReader(r);
        String token = b.readLine();
        if (token == null) {
            auth = new AuthForm();
        }else {
            DbxRequestConfig config = new DbxRequestConfig(clientId, Locale.getDefault().toString());
            client = new DbxClientV2(config, token);



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
            // Скачивание файла из дропбокс
           // OutputStream outStream = new FileOutputStream(new File(projectPath + adp + "/out.txt"));  // загрузка с дропбокса
            //client.files.downloadBuilder(dir + "/gg.txt").run(outStream);
            // dir + "/gg.txt" - откуда скачивать, или куда закачивать
            // Загрузка файлв в дропбокс
            /*
            InputStream inputStream = new FileInputStream(new File(projectPath + adp + "/gg.txt"));
            client.files.uploadBuilder(dir+"/gg.txt").run(inputStream);
            */
            folderList = new JList(listModel);
            folderList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    JList list = (JList) e.getSource();
                    acceptDonwload = false;
                    if (e.getClickCount() == 2) {
                        // Double-click detected
                        int index = list.locationToIndex(e.getPoint());
                        if (index == 0){
                            if (currentPath == "" ) {

                            }
                            else{
                                 li = currentPath.lastIndexOf("/");
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
                            acceptDonwload = false;
                            ListModel lm = list.getModel();
                            path = lm.getElementAt(index).toString();
                            boolean m = path.contains("."); // если в пути есть точка, значит это файл с расширением,а не папка
                            if (!m) {
                                try {
                                    getUpdateFolders(path);
                                } catch (DbxException e1) {
                                    e1.printStackTrace();
                                }
                                currentPath = path;
                            }else
                            {
                                int los = path.lastIndexOf("/");
                                fileDir = path.substring(los+1,path.length());
                                fileDir = "\\"+fileDir;
                                dir = currentPath;
                                OutputStream outStream = null;
                                try {
                                    outStream = new FileOutputStream(new File(projectPath + adp + fileDir));
                                } catch (FileNotFoundException e1) {
                                    e1.printStackTrace();
                                }
                                try {
                                    los = fileDir.lastIndexOf(fileDir);
                                    fileDir = "/" + fileDir.substring(los+1,fileDir.length());
                                    dir = currentPath;
                                    if (!dir.isEmpty()) {

                                        System.out.println(dir + fileDir);
                                        try {
                                            client.files.downloadBuilder(dir+fileDir).run(outStream);
                                            getUpdateFolders(currentPath);
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    }else
                                    {
                                        try {
                                            client.files.downloadBuilder(fileDir).run(outStream);
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                        getUpdateFolders(currentPath);
                                    }


                                } catch (DbxException e1) {
                                    e1.printStackTrace();
                                }
                            }

                        }
                    }
                }
            });
            uploadFileBtn = new JButton("upload file");
            uploadFileBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    dir = currentPath;
                    JFileChooser fileopen = new JFileChooser();
                    int ret = fileopen.showDialog(null, "Open file");
                    InputStream inputStream = null;
                    File file = null;
                    nameFile = null;
                    if (ret == JFileChooser.APPROVE_OPTION) {
                         file = fileopen.getSelectedFile();
                        nameFile = file.getPath();
                        int lastIndex = nameFile.lastIndexOf("\\");
                        nameFile = nameFile.substring(lastIndex + 1, nameFile.length());
                        nameFile = "/"+nameFile;
                        System.out.println(nameFile);

                        try {
                            inputStream = new FileInputStream(file);
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }
                    }
                    try {
                        System.out.println(dir+nameFile);
                        client.files.uploadBuilder(dir + nameFile ).run(inputStream);
                        getUpdateFolders(currentPath);
                    } catch (DbxException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
            });
            addFolderBtn = new JButton("add folder");
            btnNameField = new JTextField("name_new_folder");
            addFolderBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        createFolderName = btnNameField.getText();
                        client.files.createFolder(currentPath + "/" + createFolderName);
                        getUpdateFolders(currentPath);
                    } catch (DbxException e1) {
                        e1.printStackTrace();
                    }
                }
            });


            RightPanel.add(folderList, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
            //add(LeftPanel);
            add(RightPanel,new GridBagConstraints(1, 0, 1, 2, 1, 1, GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
            add(btnNameField,new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
            add(addFolderBtn,new GridBagConstraints(0, 2, 4, 1, 1, 1, GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
            add(uploadFileBtn,new GridBagConstraints(0, 4, 4, 1, 1, 1, GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
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
