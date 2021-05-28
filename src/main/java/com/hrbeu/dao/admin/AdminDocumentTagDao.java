package com.hrbeu.dao.admin;

import com.hrbeu.pojo.Tag;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Classname AdminDocumentTagDao
 * @Description TODO
 * @Date 2021/5/10 10:11
 * @Created by nxt
 */
@Repository
public interface AdminDocumentTagDao {
    void deleteDocumentTagByDocumentId(@Param("documentId") Long documentId);
    void addDocumentTag(@Param("documentId") Long documentId,@Param("tagList") List<Tag> tagList);
    List<Tag> queryTagListByPage(@Param("begin") int begin,@Param("pageSize") int pageSize);
    Integer queryTagCountInDocuments(@Param("tagId") Long tagId);
    Integer queryTagInDocumentsCountById(@Param("tagId") Long tagId);
}
