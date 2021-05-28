package com.hrbeu.dao.admin;

import com.hrbeu.pojo.Document;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Classname AdminDocumentDao
 * @Description TODO
 * @Date 2021/5/9 21:23
 * @Created by nxt
 */
@Repository
public interface AdminDocumentDao {
    int queryAllDocumentCount();
    List<Document> queryAllDocumentByPage(@Param("begin")int begin, @Param("pageSize")int pageSize);
    Document queryDocumentById(@Param("documentId") Long documentId);
    void deleteDocumentById(@Param("documentId") Long documentId);
    void addDocument(@Param("document") Document document);
    Document queryDocumentAndUserAndTagsById(@Param("documentId") Long documentId);
    void updateDocument(@Param("document") Document document);
    Integer queryTypeCountInDocuments(@Param("typeId") Long typeId);
}
