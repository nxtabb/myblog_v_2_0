package com.hrbeu.service.front;

import com.hrbeu.pojo.Document;

import java.util.Collection;
import java.util.List;

/**
 * @Classname FrontDocumentService
 * @Description TODO
 * @Date 2021/5/14 09:57
 * @Created by nxt
 */
public interface FrontDocumentService {
    List<Document> queryPublishedDocumentList(Integer pageIndex, Integer pageSize);
    Integer queryPublishedDocumentCount();
    List<Document> queryRecommendDocumentList(Integer recommendDocumentCount);
    Document queryDocumentById(Long documentId);
    List<Document> queryDocumentPageByTypeId(int pageIndex, Integer pageSize, Long typeId);
    List<Document> queryDocumentPageByTagId(int pageIndex, int pageSize, Long tagId);
    Integer queryPublishedDocumentCountByTagId(Long tagId);
    Integer queryPublishedDocumentCountByTypeId(Long typeId);
    List<String> queryDocumentByYearAndMonthAndPage(Integer pageIndex, Integer pageSize);
    List<Document> queryPublishedDocumentByTime(String time);
    List<String> queryDocumentByYearAndMonth();
    List<Document> queryDocumentByUserId(Integer pageIndex, int pageSize, Long userId);
    Integer queryDocumentCountByUserId(Long userId);
}
