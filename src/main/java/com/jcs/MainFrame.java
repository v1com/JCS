package com.jcs;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.Files;
import com.yandex.disk.client.*;
import com.yandex.disk.client.exceptions.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.*;

import static javax.swing.JFileChooser.DIRECTORIES_ONLY;

/**
 * Created by Anderson on 31.10.2015.
 */

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
    private JList folderListDb;
    private JList folderListYd;
    private JList startList;
    private JOptionPane jOptionPane;
    private JScrollPane scroll;
    private String currentPath = "";
    private int li = -1;
    private String path;
    DbxClientV2 client;
    private DefaultListModel listModel = new DefaultListModel();
    private ArrayList<Files.Metadata> entries;
    private Vector<String> dataYandex;
    static final String clientId = "0.1";
    static String pathToAuthTokens;
    boolean acceptDonwload = false;
    String fileDir = "";
    String projectPath;
    String adp;
    String additionalPath;
    String dir = "";
    String accessChooser = "";
    String [] disks = {"Yandexdisk","Dropbox"};
    boolean access = false;
    String nameFile;
    String ydisktoken;
    String dbtoken;
    TransportClient clientUploadInstance = null;
    TransportClient clientInstance = null;
    private java.util.List<ListItem> fileItemList;
    int currentDisk;

    MainFrame(String title) throws DbxException, IOException, JsonReader.FileLoadException {
        projectPath = new File(".").getCanonicalPath();
        adp = "\\src\\main\\java\\com\\jcs";
        additionalPath = "\\src\\main\\java\\com\\jcs\\tokens.txt";
        String pathAuthCodes = "\\src\\main\\java\\com\\jcs\\authcodes.txt";
        pathToAuthTokens = projectPath + additionalPath;

        FileReader r = new FileReader(pathToAuthTokens);
        BufferedReader b = new BufferedReader(r);
        dbtoken = b.readLine();
        r.close();
        FileReader r1 = new FileReader(projectPath + pathAuthCodes);
        b = new BufferedReader(r1);
        b.readLine();
        ydisktoken = b.readLine();

        if (dbtoken == null) {
            auth = new AuthForm();
        }else {
            DbxRequestConfig config = new DbxRequestConfig(clientId, Locale.getDefault().toString());
            client = new DbxClientV2(config, dbtoken);

            Credentials cr = new Credentials("name",ydisktoken);

            try {
                clientUploadInstance = TransportClient.getUploadInstance(cr);/*������� ������� ��� �������� ������ �� ����*/
            } catch (WebdavClientInitException e) {
                e.printStackTrace();
            }

            try {
                clientInstance = TransportClient.getInstance(cr);/*�������� ������ ��� ������
            �� ����� ���������� ��������� API*/
            } catch (WebdavClientInitException e) {
                e.printStackTrace();
            }

            try {
                fileItemList = new ArrayList<ListItem>();
                clientInstance.getList("", new ListParsingHandler() {

                    @Override
                    public boolean handleItem(ListItem item) {
                            fileItemList.add(item);
                            return true;
                    }
                });
            } catch (WebdavException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            for(ListItem item : fileItemList){
                listModel.addElement(item.getDisplayName());
            }

            //client.files.delete("path"); �������� ������

            //������ + �������� ��� �������


            entries = client.files.listFolder("").entries;

            setTitle(title);
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setSize(550, 300);
            setLocationRelativeTo(null);
            setLayout(new GridBagLayout());

            LeftPanel = new Panels();
            LeftPanel.setLayout(new GridBagLayout());

            accountList = new JList();
            LeftPanel.add(accountList, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

            RightPanel = new Panels();
            RightPanel.setLayout(new GridBagLayout());



         //   getUpdateYandexFolders();
         //   loadFolderListYd();
            // ���������� ����� �� ��������
           // OutputStream outStream = new FileOutputStream(new File(projectPath + adp + "/out.txt"));  // �������� � ���������
            //client.files.downloadBuilder(dir + "/gg.txt").run(outStream);
            // dir + "/gg.txt" - ������ ���������, ��� ���� ����������
            // �������� ����� � ��������
            /*
            InputStream inputStream = new FileInputStream(new File(projectPath + adp + "/gg.txt"));
            client.files.uploadBuilder(dir+"/gg.txt").run(inputStream);
            */
            loadButtons();
            gotoStart();

            RightPanel.add(startList, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
            //add(LeftPanel);
            add(RightPanel, new GridBagConstraints(1, 0, 1, 2, 1, 1, GridBagConstraints.PAGE_START,
                    GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
            add(addFolderBtn, new GridBagConstraints(0, 2, 4, 1, 1, 1, GridBagConstraints.PAGE_START,
                    GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
            add(uploadFileBtn, new GridBagConstraints(0, 4, 4, 1, 1, 1, GridBagConstraints.PAGE_START,
                    GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

            setVisible(true);
        }


    }

    private void gotoDropBox(){
        try {
            getUpdateFolders();
        } catch (DbxException e) {
            e.printStackTrace();
        }
        loadFolderListDb();
        RightPanel.removeAll();
        scroll = new JScrollPane(folderListDb);
        RightPanel.add(scroll, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.PAGE_START,
                GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        RightPanel.updateUI();
    //    btnNameField.show();
        addFolderBtn.show();
        uploadFileBtn.show();
        setSize(550, 300);
        currentDisk = 0;
    }

    private void gotoStart() {
        loadStartList();
        RightPanel.removeAll();
        RightPanel.add(startList, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.PAGE_START,
                GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        RightPanel.updateUI();
        addFolderBtn.hide();
        uploadFileBtn.hide();
        setSize(550, 300);
    }

    private void gotoYandexDisk() {
        getUpdateYandexFolders();
        loadFolderListYd();
        RightPanel.removeAll();
        scroll = new JScrollPane(folderListYd);
        RightPanel.add(scroll, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.PAGE_START,
                GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        RightPanel.updateUI();
        addFolderBtn.show();
        uploadFileBtn.show();

        setSize(550, 300);
        currentDisk = 1;
    }

    private void loadFolderListDb(){
        folderListDb = new JList(listModel);
        folderListDb.addMouseListener(new MouseAdapter() {
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
                            gotoStart();
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
                        boolean m = path.contains("."); // ���� � ���� ���� �����, ������ ��� ���� � �����������,� �� �����
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
                            JFileChooser saveFile = new JFileChooser();
                            int ret = saveFile.showDialog(null, "Save file");
                            File file = null;
                            String pathToSave = "";

                            if (ret == JFileChooser.APPROVE_OPTION) {
                                file = saveFile.getSelectedFile();
                                pathToSave = file.getAbsolutePath();
                            }
                            try {
                                outStream = new FileOutputStream(new File(pathToSave));
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
    }

    private void loadFolderListYd(){
        folderListYd = new JList(listModel);
        folderListYd.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JList list = (JList) e.getSource();
                if (e.getClickCount() == 2) {
                    // Double-click detected
                    int index = list.locationToIndex(e.getPoint());
                    if (index == 0) {
                        if (currentPath == "") {
                            gotoStart();
                        } else {
                            li = currentPath.lastIndexOf("/");
                            if (li == -1)
                                currentPath = "";
                            else {
                                currentPath = currentPath.substring(0, li);
                            }
                            getUpdateYandexFolders(currentPath);
                        }
                    } else {
                        ListModel lm = list.getModel();
                        path = lm.getElementAt(index).toString();
                        boolean m = path.contains("."); // ���� � ���� ���� �����, ������ ��� ���� � �����������,� �� �����
                        if (!m) {
                            getUpdateYandexFolders(path);
                            currentPath = path;
                        } else {
                            int los = path.lastIndexOf("/");
                            fileDir = path.substring(los+1,path.length());
                            fileDir = "\\"+fileDir;
                            dir = currentPath;
                            JFileChooser saveFile = new JFileChooser();
                            saveFile.setFileSelectionMode(DIRECTORIES_ONLY);
                            System.out.println();
                            saveFile.setApproveButtonText("Choose");
                            int ret = saveFile.showOpenDialog(null);
                            File file = null;
                            String pathToSave = "";

                            if (ret == JFileChooser.APPROVE_OPTION) {
                                file = saveFile.getSelectedFile();
                                pathToSave = file.getAbsolutePath();
                                System.out.println(currentPath + "/" + lm.getElementAt(index).toString());
                            }

                            ProgressListener pl = new ProgressListener() {
                                public void updateProgress(long loaded, long total) {

                                }

                                public boolean hasCancelled() {
                                    return false;
                                }
                            };

                            try {
                                clientInstance.downloadFile(currentPath + "/" + lm.getElementAt(index).toString(),
                                        new File(pathToSave + "/" + lm.getElementAt(index).toString()), pl);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            } catch (WebdavUserNotInitialized webdavUserNotInitialized) {
                                webdavUserNotInitialized.printStackTrace();
                            } catch (PreconditionFailedException e1) {
                                e1.printStackTrace();
                            } catch (WebdavNotAuthorizedException e1) {
                                e1.printStackTrace();
                            } catch (ServerWebdavException e1) {
                                e1.printStackTrace();
                            } catch (CancelledDownloadException e1) {
                                e1.printStackTrace();
                            } catch (UnknownServerWebdavException e1) {
                                e1.printStackTrace();
                            }
                        }

                    }
                }
            }
        });
    }

    private void loadStartList() {
        startList = new JList(disks);
        startList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JList list = (JList) e.getSource();
                acceptDonwload = false;
                if (e.getClickCount() == 2) {
                    int index = list.locationToIndex(e.getPoint());
                    //chose yandex
                    if (index == 0) {
                        gotoYandexDisk();
                    }
                    //chose dropbox
                    else if (index == 1) {
                        gotoDropBox();
                    }
                }
            }
        });

    }

    private void loadButtons(){
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

                    if (currentDisk == 0) {
                        nameFile = nameFile.substring(lastIndex + 1, nameFile.length());
                        nameFile = "/" + nameFile;
                        System.out.println(nameFile);

                        try {
                            inputStream = new FileInputStream(file);
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        try {
                            System.out.println(dir + nameFile);
                            client.files.uploadBuilder(dir + nameFile).run(inputStream);
                            getUpdateFolders(currentPath);
                        } catch (DbxException e1) {
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    else if(currentDisk == 1){
                        try {
                            clientUploadInstance.uploadFile(file.getPath(), currentPath, null);
                            getUpdateYandexFolders(currentPath);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (UnknownServerWebdavException e1) {
                            e1.printStackTrace();
                        } catch (PreconditionFailedException e1) {
                            e1.printStackTrace();
                        } catch (IntermediateFolderNotExistException e1) {
                            e1.printStackTrace();
                        } catch (WebdavUserNotInitialized webdavUserNotInitialized) {
                            webdavUserNotInitialized.printStackTrace();
                        } catch (ServerWebdavException e1) {
                            e1.printStackTrace();
                        } catch (WebdavNotAuthorizedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });

        addFolderBtn = new JButton("add folder");

        addFolderBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = (String) jOptionPane.showInputDialog(null, "New folder", "Enter name of new folder:",
                        jOptionPane.INFORMATION_MESSAGE);
                createFolderName = name;
                if(currentDisk == 0) {
                    try {
                        client.files.createFolder(currentPath + "/" + createFolderName);
                        getUpdateFolders(currentPath);
                    } catch (DbxException e1) {
                        e1.printStackTrace();
                    }
                }
                else if(currentDisk == 1){
                    try {
                        clientInstance.makeFolder(currentPath + "/" + createFolderName);/*������ ������� � ��� ������������ ��������*/
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (DuplicateFolderException e1) {
                        e1.printStackTrace();
                    } catch (IntermediateFolderNotExistException e1) {
                        e1.printStackTrace();
                    } catch (WebdavUserNotInitialized webdavUserNotInitialized) {
                        webdavUserNotInitialized.printStackTrace();
                    } catch (PreconditionFailedException e1) {
                        e1.printStackTrace();
                    } catch (WebdavNotAuthorizedException e1) {
                        e1.printStackTrace();
                    } catch (ServerWebdavException e1) {
                        e1.printStackTrace();
                    } catch (UnsupportedMediaTypeException e1) {
                        e1.printStackTrace();
                    } catch (UnknownServerWebdavException e1) {
                        e1.printStackTrace();
                    }
                    getUpdateYandexFolders(currentPath);
                }

            }
        });

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

    private void getUpdateYandexFolders(){
        listModel.clear();
        listModel.add(0, "...");
        try {
            fileItemList = new ArrayList<ListItem>();
            clientInstance.getList("", new ListParsingHandler() {

                @Override
                public boolean handleItem(ListItem item) {
                    fileItemList.add(item);
                    return true;
                }
            });
        } catch (WebdavException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int i = 0;
        for(ListItem item : fileItemList){
            if(i != 0)
                listModel.addElement(item.getDisplayName());
            i++;
        }

    }

    private void getUpdateYandexFolders(String path){
        listModel.clear();
        listModel.add(0, "...");
        try {
            fileItemList = new ArrayList<ListItem>();
            clientInstance.getList(path, new ListParsingHandler() {

                @Override
                public boolean handleItem(ListItem item) {
                    fileItemList.add(item);
                    return true;
                }
            });
        } catch (WebdavException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int i = 0;
        for(ListItem item : fileItemList){
            if(i != 0)
                listModel.addElement(item.getDisplayName());
            i++;
        }
    }

    public static void main(String[] args) throws JsonReader.FileLoadException, IOException, DbxException {
        MainFrame mainform = new MainFrame("JCS - multicloud system");
    }
}
