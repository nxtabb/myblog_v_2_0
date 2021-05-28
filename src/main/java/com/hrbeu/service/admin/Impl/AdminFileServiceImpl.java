package com.hrbeu.service.admin.Impl;

import com.hrbeu.dao.admin.AdminFileDao;
import com.hrbeu.pojo.File;
import com.hrbeu.service.admin.AdminFileService;
import com.hrbeu.utils.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminFileServiceImpl implements AdminFileService {
    @Autowired
    private AdminFileDao adminFileDao;

    @Override
    public List<File> queryFilesByDocument(Long documentId) {
        return adminFileDao.queryFilesByDocument(documentId);
    }

    @Override
    public File queryFileById(Long fileId) {
        return adminFileDao.queryFileById(fileId);
    }

    @Override
    public boolean deleteFile(Long fileId) {
        File file = adminFileDao.queryFileById(fileId);
        try {
            PathUtil.delFileOrPath(file.getFilePath());
            adminFileDao.deleteFileById(fileId);
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
