package com.mmall.util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FTPUtil {
    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPsw = PropertiesUtil.getProperty("ftp.pass");
    private static Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private String ip;
    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    private String user;
    private String password;
    private FTPClient ftpClient;

    public FTPUtil(String ip,int port, String user, String password) {
        this.ip = ip;
        this.user = user;
        this.password = password;
        this.port = port;
    }

    public static boolean upload(List<File> fileList) throws IOException {
        Boolean isSuccess = true;
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPsw);
        logger.info("开始连接FTP服务器");
        isSuccess = ftpUtil.upload("img", fileList);
        logger.info("结束上传，上传结果:{}", isSuccess);
        return isSuccess;
    }

    private  boolean upload(String remotePath, List<File> fileList) throws IOException {
        boolean upload = true;
        FileInputStream fip = null;
        if(connectServer(this.ip, this.port, this.user, this.password)) {
            try {
                logger.info("已连接FTP服务器");
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setBufferSize(1024);
                for(File file : fileList) {
                    fip = new FileInputStream(file);
                    ftpClient.storeFile(file.getName(), fip);
                }
                ftpClient.logout();
            } catch (IOException e) {
                logger.error("上传文件异常", e);
                upload = false;
                e.printStackTrace();
            } finally {

                if(!ftpClient.isConnected()) {
                    ftpClient.disconnect();
                }
            }
        }
        return upload;
    }

    private boolean connectServer(String ip, int port, String user, String password) {
        ftpClient = new FTPClient();
        Boolean isSuccess = false;
        try {

            ftpClient.connect(ip, port);
            isSuccess = ftpClient.login(user, password);
        } catch (IOException e) {
            logger.error("FTP连接失败", e);
            e.printStackTrace();
        }

        return isSuccess;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
