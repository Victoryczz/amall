package seu.vczz.amall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * CREATE by vczz on 2018/4/9
 * 文件上传服务
 */
public interface IFileService {

    String upload(MultipartFile file, String path);
}
