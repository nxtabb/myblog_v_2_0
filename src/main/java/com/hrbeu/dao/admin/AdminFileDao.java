package com.hrbeu.dao.admin;

import com.hrbeu.pojo.Document;
import com.hrbeu.pojo.File;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Classname AdminFileDao
 * @Description TODO
 * @Date 2021/5/10 10:06
 * @Created by nxt
 */
@Repository
public interface AdminFileDao {
    void deleteFileByDocumentId(@Param("documentId") Long documentId);
    void addFile(@Param("fileNameList") List<String> fileNameList,@Param("filePathList")List<String>filePathList,@Param("fileOriginNameList") List<String> fileOriginNameList, @Param("document")Document document, @Param("createTime") Date createTime, @Param("lastEditTime") Date lastEditTime);
    List<File> queryFilesByDocument(@Param("documentId") Long documentId);
    void updateFilePathInfo(@Param("filePath") String filePath,@Param("fileId") Long fileId);
    List<File> queryOldFilesByDocumentAndTitle(@Param("documentId") Long documentId, @Param("title") String title);
    File queryFileById(@Param("fileId") Long fileId);
    void deleteFileById(@Param("fileId") Long fileId);
}
