package com.hrbeu.service.front;

import com.hrbeu.pojo.File;
import com.hrbeu.pojo.vo.File_Len;

import java.util.List;

/**
 * @Classname FrontFileService
 * @Description TODO
 * @Date 2021/5/14 09:58
 * @Created by nxt
 */
public interface FrontFileService {
    List<File_Len> queryFileAndLenByDocument(Long documentId);
    File queryFileById(Long fileId);
}
