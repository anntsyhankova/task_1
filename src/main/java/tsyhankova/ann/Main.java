package tsyhankova.ann;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
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

    private void myImpl(){
        SimpleFTPClient client = new SimpleFTPClient();
        try {
            client.connect(ftpServerUrl, 21, "test", "test");
            client.list();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void apacheImpl(){
        FTPClient client = new FTPClient();
        try {
            //connect to ftp server
            client.connect(ftpServerUrl, port);
            showServerReply(client);
            //enterLocalPassiveMode
            client.enterLocalPassiveMode();
            showServerReply(client);
            //login
            client.login("test", "test");
            showServerReply(client);
            //Go to passive mode
            client.pasv();
            showServerReply(client);
            //
            List<String> names = Arrays.stream(client.listDirectories("/")).map(FTPFile::getName).toList();
            showServerReply(client);
            for(String dirName : names){
                System.out.println("/"+dirName);
                client.changeWorkingDirectory("/"+dirName);
                showServerReply(client);
            }
            client.changeToParentDirectory();
            showServerReply(client);
            FileInputStream fileInputStream = new FileInputStream("src/main/resources/aqa.txt");
            client.storeFile("aqa.txt", fileInputStream);
            showServerReply(client);
            client.deleteFile("aqa.txt");
            showServerReply(client);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                if (client.isConnected()) {
                    client.logout();
                    client.disconnect();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void showServerReply(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                System.out.println("SERVER: " + aReply);
            }
        }
    }
}