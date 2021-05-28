package com.hrbeu.service.admin;

import com.hrbeu.pojo.File;

import java.util.List;

/**
 * @Classname AdminFileService
 * @Description TODO
 * @Date 2021/5/12 08:58
 * @Created by nxt
 */
public interface AdminFileService {
    List<File> queryFilesByDocument(Long documentId);
    File queryFileById(Long fileId);
    boolean deleteFile(Long fileId);
}
