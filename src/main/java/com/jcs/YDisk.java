package com.jcs;

import com.yandex.disk.client.Credentials;
import com.yandex.disk.client.ProgressListener;
import com.yandex.disk.client.TransportClient;
import com.yandex.disk.client.exceptions.*;

import java.io.File;
import java.io.IOException;

/**
 * Created by homie on 21.12.2015.
 */
public class YDisk {
    public static void main(String[] args) {
        String ydiskcode = "37835a456eb541e59916de10388a5150";/*OAuth-токен получаемые при помощи формы AuthForm*/
        String userID = "5b9cc1c52a374da3a6f3f218c9d9ecc1";
        MyYaListParsing myListParsing = new MyYaListParsing();

        Credentials cr = new Credentials("nikitamslv",ydiskcode);/*Передаёшь своё имя и OAuth-токен полученный ранее*/

        /*Большинство информации можно найти на
        https://tech.yandex.ru/disk/doc/dg/sdk/java-docpage/
        */



        TransportClient clientUploadInstance = null;
        TransportClient clientInstance = null;

        try {
            clientUploadInstance = TransportClient.getUploadInstance(cr);/*создает объекты для загрузки файлов на Диск*/
        } catch (WebdavClientInitException e) {
            e.printStackTrace();
        }

        try {
            clientInstance = TransportClient.getInstance(cr);/*создаётся объект для работы
            со всеми остальными функциями API*/
        } catch (WebdavClientInitException e) {
            e.printStackTrace();
        }

        try {
            clientInstance.getList("/NewFolder1",myListParsing);/* Выводится содержимое католога и свойства файла
            Второй параметр это класс унаследованный от ListParsingHandler,
            в котором реализована функция handleItem(ListItem item) которая позволяет получать необходимую информацию от
            содержимого католога
            */
        } catch (WebdavException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            clientInstance.makeFolder("/NewFolder1/Моя папка");/*Создаёт католог в уже существующем катологе*/
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DuplicateFolderException e) {
            e.printStackTrace();
        } catch (IntermediateFolderNotExistException e) {
            e.printStackTrace();
        } catch (WebdavUserNotInitialized webdavUserNotInitialized) {
            webdavUserNotInitialized.printStackTrace();
        } catch (PreconditionFailedException e) {
            e.printStackTrace();
        } catch (WebdavNotAuthorizedException e) {
            e.printStackTrace();
        } catch (ServerWebdavException e) {
            e.printStackTrace();
        } catch (UnsupportedMediaTypeException e) {
            e.printStackTrace();
        } catch (UnknownServerWebdavException e) {
            e.printStackTrace();
        }


        try {
            clientInstance.move("Добро Пожаловать.pdf","/NewFolder1/Добро Пожаловать1.pdf");/*Перемещает файл из одной
             директории в другую. Также функция может быть исползована для переименовывания.*/
        } catch (WebdavException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            clientInstance.delete("/NewFolder1/Docic.docx");/*Удалить файл или каталог с Яндекс.
            Согласно протоколу, каталоги всегда удаляются вместе со всеми вложенными файлами и каталогами.*/
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WebdavFileNotFoundException e) {
            e.printStackTrace();
        } catch (WebdavUserNotInitialized webdavUserNotInitialized) {
            webdavUserNotInitialized.printStackTrace();
        } catch (UnknownServerWebdavException e) {
            e.printStackTrace();
        } catch (PreconditionFailedException e) {
            e.printStackTrace();
        } catch (WebdavNotAuthorizedException e) {
            e.printStackTrace();
        } catch (ServerWebdavException e) {
            e.printStackTrace();
        }

        ProgressListener pl = new ProgressListener() {
            public void updateProgress(long loaded, long total) {

            }

            public boolean hasCancelled() {
                return false;
            }
        };

        try {
            clientInstance.downloadFile("/NewFolder1/Добро Пожаловать1.pdf", new File("F:\\Добро пожаловать1.pdf"), pl);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WebdavUserNotInitialized webdavUserNotInitialized) {
            webdavUserNotInitialized.printStackTrace();
        } catch (PreconditionFailedException e) {
            e.printStackTrace();
        } catch (WebdavNotAuthorizedException e) {
            e.printStackTrace();
        } catch (ServerWebdavException e) {
            e.printStackTrace();
        } catch (CancelledDownloadException e) {
            e.printStackTrace();
        } catch (UnknownServerWebdavException e) {
            e.printStackTrace();
        }

        try {
            clientUploadInstance.uploadFile("F:\\lastwork.lsp","",pl);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnknownServerWebdavException e) {
            e.printStackTrace();
        } catch (PreconditionFailedException e) {
            e.printStackTrace();
        } catch (IntermediateFolderNotExistException e) {
            e.printStackTrace();
        } catch (WebdavUserNotInitialized webdavUserNotInitialized) {
            webdavUserNotInitialized.printStackTrace();
        } catch (ServerWebdavException e) {
            e.printStackTrace();
        } catch (WebdavNotAuthorizedException e) {
            e.printStackTrace();
        }
    }
}
