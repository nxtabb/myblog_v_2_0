package com.hrbeu.dao.front;

import com.hrbeu.pojo.File;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Classname FrontFileDao
 * @Description TODO
 * @Date 2021/5/14 10:06
 * @Created by nxt
 */
@Repository
public interface FrontFileDao {
    List<File> queryFileListByDocumentId(@Param("documentId") Long documentId);
    File queryFileById(@Param("fileId") Long fileId);
}
