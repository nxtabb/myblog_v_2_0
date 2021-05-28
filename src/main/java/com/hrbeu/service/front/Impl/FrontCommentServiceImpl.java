package com.hrbeu.service.front.Impl;

import com.hrbeu.dao.front.FrontCommentDao;
import com.hrbeu.pojo.Comment;
import com.hrbeu.service.front.FrontCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FrontCommentServiceImpl implements FrontCommentService {
    private List<Comment> tempReplies = new ArrayList<>();
    @Autowired
    private FrontCommentDao frontCommentDao;


    @Override
    public List<Comment> queryCommentListByDocument(Long documentId) {
        List<Comment> rootCommentList = frontCommentDao.queryRootCommentByDocumentId(documentId);
        for(Comment comment:rootCommentList){
            List<Comment> childComment = frontCommentDao.queryChildCommentList(comment);
            comment.setCommnets(childComment);
        }
        combineChildren(rootCommentList);
        for(Comment comment:rootCommentList){
            List<Comment> commentsOfComment = comment.getCommnets();
            for(Comment child:commentsOfComment){
                child.setParentComment(getParentComment(child));
            }
        }
        return rootCommentList;
    }

    @Override
    public Comment getParentComment(Comment comment) {
        Long parentId = comment.getParentId();
        if(parentId!=null){
            return frontCommentDao.queryParentComment(parentId);
        }else {
            return null;
        }
    }

    @Override
    public Long queryDocumentOfComment(Long commentId) {
        return frontCommentDao.queryDocumentOfComment(commentId);
    }

    @Override
    public void deleteComment(Long commentId) {
        frontCommentDao.deleteComment(commentId);
    }

    @Override
    public void saveComment(Comment comment) {
        Long commentParentId = comment.getParentId();
        if(commentParentId!=-1){
            comment.setParentId(commentParentId);
            comment.setCreateTime(new Date());
        }
        else {
            comment.setParentId(null);
            comment.setCreateTime(new Date());
        }
        frontCommentDao.saveComment(comment);
    }


    //方法1
    private void combineChildren(List<Comment> comments) {
        for(Comment comment:comments){
            //得到节点的一个子集
            List<Comment> replys1 =comment.getCommnets();
            for(Comment reply1:replys1){
                List<Comment> child = frontCommentDao.queryChildCommentList(reply1);
                reply1.setCommnets(child);
                recursive(reply1);
            }
            comment.setCommnets(tempReplies);
            tempReplies = new ArrayList<>();
        }
    }
    //方法2
    private void recursive(Comment comment){
        tempReplies.add(comment);
        if(comment.getCommnets()!=null&&comment.getCommnets().size()>0){
            List<Comment> replys = comment.getCommnets();
            for(Comment reply:replys){
                List<Comment> child = frontCommentDao.queryChildCommentList(reply);
                reply.setCommnets(child);
                recursive(reply);
            }
        }
    }
}
