package com.hrbeu.service.front;

import com.hrbeu.pojo.Comment;

import java.util.List;

/**
 * @Classname FrontCommentService
 * @Description TODO
 * @Date 2021/5/14 09:58
 * @Created by nxt
 */
public interface FrontCommentService {
    List<Comment> queryCommentListByDocument(Long documentId);
    Comment getParentComment(Comment comment);
    Long queryDocumentOfComment(Long commentId);
    void deleteComment(Long commentId);
    void saveComment(Comment comment);
}
