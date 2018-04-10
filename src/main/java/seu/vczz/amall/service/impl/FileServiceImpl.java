package seu.vczz.amall.service.impl;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import seu.vczz.amall.service.IFileService;
import seu.vczz.amall.util.FTPUtil;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * CREATE by vczz on 2018/4/9
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    /**
     * 上传文件，同时传到ftp之后再删除文件
     * 流程：获得file，构造上传文件的路径，创建新的文件名，将上传的文件transfer到文件，将文件上传至ftp服务器，删除文件目录中的文件
     * @param file
     * @param path
     * @return
     */
    public String upload(MultipartFile file, String path){
        //先获取原始的文件名
        String fileName = file.getOriginalFilename();
        //获得文件扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        //上传文件文件名，使用UUID
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("开始上传文件,上传文件名:{},上传路径:{},新文件名:{}",fileName, path, uploadFileName);

        File fileDir = new File(path);
        //判断路径是否存在，不存在则创建
        if (!fileDir.exists()){
            //赋予写文件权限
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path, uploadFileName);
        //将文件transfer到目标文件
        try {
            file.transferTo(targetFile);
            //将文件转移到ftp服务器
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //删除webapp下的文件
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常", e);
            return null;
        }
        //这一步证明了虽然文件不在了，但是还是能获取名字，在出了作用域之后就不行了
        return targetFile.getName();
    }

}
