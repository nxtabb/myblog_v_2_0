package com.hrbeu.dao.front;

import com.hrbeu.pojo.Document;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Classname FrontDocumentDao
 * @Description TODO
 * @Date 2021/5/14 10:06
 * @Created by nxt
 */
@Repository
public interface FrontDocumentDao {
    List<Document> queryPublishedDocumentList(@Param("begin") Integer begin,@Param("pageSize") Integer pageSize);
    Integer queryPublishedDocumentCount();
    List<Document> queryRecommendDocumentList(@Param("count") Integer count);
    Document queryDocumentById(@Param("documentId") Long documentId);
    List<Document> queryDocumentPageByTypeId(@Param("begin") int begin,@Param("pageSize") Integer pageSize,@Param("typeId") Long typeId);
    List<Document> queryDocumentPageByTagId(@Param("begin") int begin,@Param("pageSize") int pageSize,@Param("tagId") Long tagId);
    Integer queryPublishedDocumentCountByTypeId(@Param("typeId") Long typeId);
    List<String> queryDocumentByYearAndMonthAndPage(@Param("begin") Integer begin,@Param("pageSize") Integer pageSize);
    List<Document> queryPublishedDocumentByTime(@Param("time") String time);
    List<String> queryDocumentByYearAndMonth();
    List<Document> queryDocumentByUserId(@Param("begin") Integer begin,@Param("pageSize") int pageSize,@Param("userId") Long userId);
    Integer queryDocumentCountByUserId(@Param("userId") Long userId);
}
