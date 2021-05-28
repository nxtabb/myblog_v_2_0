package com.hrbeu.service.admin;

import com.hrbeu.pojo.Document;
import com.hrbeu.pojo.Tag;
import com.hrbeu.pojo.User;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Classname AdminDocumentService
 * @Description TODO
 * @Date 2021/5/9 21:20
 * @Created by nxt
 */
public interface AdminDocumentService {
    int queryAllDocumentCount();
    List<Document> queryAllDocumentListByPage(int pageIndex, int pageSize);
    Document queryDocumentById(Long documentId);
    boolean deleteDocumentAndFileAndComment(Document document, User user);
    void addDocumentAndDocumentTagAndSaveFiles(Document document, List<Tag> tagList, HttpServletRequest request);
    Document queryDocumentAndUserAndTagsById(Long documentId);
    void updateDocumentAndDocumentTagAndFiles(HttpServletRequest request, Document document,Document oldDocument);
}
