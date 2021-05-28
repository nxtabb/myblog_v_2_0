package com.hrbeu.dao.admin;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @Classname AdminCommentDao
 * @Description TODO
 * @Date 2021/5/10 09:44
 * @Created by nxt
 */
@Repository
public interface AdminCommentDao {
    void setParentCommentToNull(@Param("documentId") Long documentId);
    void deleteCommentByDocumentId(@Param("documentId") Long documentId);
}
