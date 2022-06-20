package tsyhankova.ann.ftpclient;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ImplementedFTPClient {
    FTPClient client = new FTPClient();

    public void connect(String ftpServerUrl, int port) throws IOException {
        client.connect(ftpServerUrl, port);
        showServerReply(client);
    }

    public void enterLocalPassiveMode() throws IOException {
        client.enterLocalPassiveMode();
        showServerReply(client);
    }

    public void login(String username, String password) throws IOException {
        client.login("test", "test");
        showServerReply(client);
    }

    public void enterPassiveMode() throws IOException {
        client.pasv();
        showServerReply(client);
    }

    public List<String> getDirectoryNames(String parentDirectory) throws IOException {
        List<String> names = Arrays.stream(client.listDirectories(parentDirectory)).map(FTPFile::getName).toList();
        showServerReply(client);
        return names;
    }

    public void changeWorkingDirectory(String pathToDirectory) throws IOException {
        client.changeWorkingDirectory(pathToDirectory);
        showServerReply(client);
    }

    public void changeToParentDirectory() throws IOException {
        changeWorkingDirectory("/");
    }

    public void uploadFile(String filename) throws IOException {
        client.storeFile(filename, new FileInputStream("src/main/resources/" + filename));
        showServerReply(client);
    }

    public void deleteFile(String filename) throws IOException {
        client.deleteFile(filename);
        showServerReply(client);
    }

    public void disconnect(){
        try {
            if (client.isConnected()) {
                client.logout();
                client.disconnect();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
