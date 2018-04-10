package seu.vczz.amall.util;


import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * CREATE by vczz on 2018/3/18
 * FTP服务器对接
 */
public class FTPUtil {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);
    //ftp服务器连接信息
    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");


    private FTPUtil(String ip, int port, String user, String pass){
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pass = pass;
    }

    /**
     * 连接服务器
     * @param ip
     * @param port
     * @param user
     * @param pass
     * @return
     */
    private boolean connectServer(String ip, int port, String user, String pass){
        //是否连接成功
        boolean isSuccess = false;
        //ftp client
        ftpClient = new FTPClient();
        try {
            //连接
            ftpClient.connect(ip);
            //连接成功登陆
            isSuccess = ftpClient.login(user, pass);
        } catch (IOException e) {
            logger.error("连接ftp服务器失败", e);
        }
        return isSuccess;
    }

    /**
     * 上传文件,对外开放的方法
     * @param fileList
     * @return
     */
    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp, 21, ftpUser, ftpPass);
        logger.info("开始连接ftp服务器");
        //img代表的是ftp服务器的img路径
        boolean result = ftpUtil.uploadFile("img", fileList);
        logger.info("上传文件结束");
        return result;
    }

    /**
     * 私有的上传文件的方法
     * @param remotePath
     * @param fileList
     * @return
     * @throws IOException
     */
    private boolean uploadFile(String remotePath, List<File> fileList) throws IOException {
        boolean upload = true;
        FileInputStream fis = null;
        //连接服务器
        if (connectServer(this.ip, this.port, this.user, this.pass)){
            try {
                //改变工作目录
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();//被动连接模式
                for (File fileItem : fileList){
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(), fis);
                }
            } catch (IOException e) {
                logger.error("上传文件异常", e);
                e.printStackTrace();
                upload = false;
            }finally {
                fis.close();
                ftpClient.disconnect();
            }
        }else {
            upload = false;
        }
        return upload;

    }

    private String ip;
    private int port;
    private String user;
    private String pass;
    private FTPClient ftpClient;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
