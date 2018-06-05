package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.apache.commons.collections.ListUtils;
import org.apache.ibatis.annotations.Select;
import org.aspectj.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileServicveImpl")
public class FileServiceImpl implements IFileService {

    public static Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String upload(String path, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        //扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        // todo consider uploadFileName
        String uploadFileName = UUID.randomUUID().toString() + "."+fileName;
        logger.info("开始文件上传到tomcat服务器,上传文件的文件名:{},上传的路径:{},新文件名:{}",fileName,path,uploadFileName);
        File fileDir = new File(path);
        if(!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }

        File targetFile = new File(path, uploadFileName);
        try {
            file.transferTo(targetFile);
            FTPUtil.upload(Lists.newArrayList(targetFile));
            targetFile.delete();
        } catch (IOException e) {
            logger.error("文件上传异常", e);
        }
        return targetFile.getName();
    }


}
