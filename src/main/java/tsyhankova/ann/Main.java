package tsyhankova.ann;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import tsyhankova.ann.ftpclient.ImplementedFTPClient;
import tsyhankova.ann.ftpclient.SimpleFTPClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/***
 * To connect server you should run docker container with vsftpd
 * Then add some directories to the ftp server or use client.makeDirectory(dirName).
 *
 * Command: docker run -d -v /var/ftp:/home/vsftpd -p 20:20 -p 21:21 -p 21100-21110:21100-21110 -e FTP_USER=test -e FTP_PASS=test -e PASV_ADDRESS=192.168.60.128 -e PASV_MIN_PORT=21100 -e PASV_MAX_PORT=21110 --name vsftpd --restart=always fauria/vsftpd
 * See more: https://github.com/fauria/docker-vsftpd
 */
public class Main {
    String ftpServerUrl = "127.0.0.1";
    int port = 21;

    public static void main(String[] args) {
        Main main = new Main();
        main.apacheImpl();
    }

    private void apacheImpl(){
        ImplementedFTPClient client = new ImplementedFTPClient();
        try {
            //connect to ftp server
            client.connect(ftpServerUrl, port);
            //enterLocalPassiveMode
            client.enterLocalPassiveMode();
            //login
            client.login("test", "test");
            //Go to passive mode
            client.enterPassiveMode();
            //get directories names
            List<String> directories = client.getDirectoryNames("/");
            //for every name in list change working dir but only first layer
            for(String directoryName : directories){
                System.out.println("/"+directoryName);
                client.changeWorkingDirectory("/"+directoryName);
            }
            //get back to parent
            client.changeToParentDirectory();
            //upload file
            client.uploadFile("aqa.txt");
            //delete file
            client.deleteFile("aqa.txt");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            client.disconnect();
        }
    }

    private void myImpl(){
        SimpleFTPClient client = new SimpleFTPClient();
        try {
            client.connect(ftpServerUrl, 21, "test", "test");
            client.list();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}