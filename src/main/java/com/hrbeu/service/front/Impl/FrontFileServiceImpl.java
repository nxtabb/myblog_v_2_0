package com.hrbeu.service.front.Impl;

import com.hrbeu.dao.front.FrontFileDao;
import com.hrbeu.pojo.Comment;
import com.hrbeu.pojo.File;
import com.hrbeu.pojo.vo.File_Len;
import com.hrbeu.service.front.FrontFileService;
import com.hrbeu.utils.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @Classname FrontFileServiceImpl
 * @Description TODO
 * @Date 2021/5/14 10:04
 * @Created by nxt
 */
@Service
public class FrontFileServiceImpl implements FrontFileService {
    @Autowired
    private FrontFileDao frontFileDao;
    @Override
    public List<File_Len> queryFileAndLenByDocument(Long documentId) {
        List<File> fileList = frontFileDao.queryFileListByDocumentId(documentId);
        if(fileList!=null){
            List<File_Len> fileLenList = new ArrayList<>();
            fileList.forEach(new Consumer<File>() {
                @Override
                public void accept(File file) {
                    File_Len file_len = new File_Len();
                    java.io.File fileOfFile = new java.io.File(PathUtil.getBasePath()+file.getFilePath());
                    Double fileLength = fileOfFile.length()/1024.0/1024.0;
                    String fileLengthStr = String.format("%.2f", fileLength);
                    file_len.setFile(file);
                    file_len.setLength(fileLengthStr);
                    fileLenList.add(file_len);
                }
            });
            return fileLenList;
        }else {
            return null;
        }
    }

    @Override
    public File queryFileById(Long fileId) {
        return frontFileDao.queryFileById(fileId);
    }


}
