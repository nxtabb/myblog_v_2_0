package com.hrbeu.controller.front.CommentController;

import com.hrbeu.pojo.Comment;
import com.hrbeu.pojo.User;
import com.hrbeu.service.front.FrontCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

@Controller
public class FrontCommentController {
    @Autowired
    private FrontCommentService frontCommentService;

    @GetMapping("/deletecomment/{commentId}")
    public String deleteComment(@PathVariable("commentId")Long commentId){
        Long documentId = frontCommentService.queryDocumentOfComment(commentId);
        frontCommentService.deleteComment(commentId);
        return "redirect:/document/"+documentId;
    }

    @PostMapping("/comments")
    public void sendComment(Comment comment, HttpSession session){
        User user = (User)session.getAttribute("user");
        if(user!=null){
            comment.setAdminComment(1);
        }else {
            comment.setAdminComment(0);
        }
        frontCommentService.saveComment(comment);
    }
}
