package com.hrbeu.dao.front;

import com.hrbeu.pojo.Comment;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Classname FrontCommentDao
 * @Description TODO
 * @Date 2021/5/14 10:06
 * @Created by nxt
 */
@Repository
public interface FrontCommentDao {
    List<Comment> queryRootCommentByDocumentId(@Param("documentId") Long documentId);
    List<Comment> queryChildCommentList(@Param("comment") Comment comment);
    Comment queryParentComment(@Param("parentId") Long parentId);
    Long queryDocumentOfComment(@Param("commentId") Long commentId);
    void deleteComment(@Param("commentId") Long commentId);
    void saveComment(@Param("comment") Comment comment);
}
