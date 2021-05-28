package com.hrbeu.service.front.Impl;

import com.hrbeu.dao.front.FrontDocumentDao;
import com.hrbeu.dao.front.FrontDocumentTagDao;
import com.hrbeu.pojo.Document;
import com.hrbeu.service.front.FrontDocumentService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Classname FrontDocumentServiceImpl
 * @Description TODO
 * @Date 2021/5/14 09:59
 * @Created by nxt
 */
@Service
public class FrontDocumentServiceImpl implements FrontDocumentService {
    @Autowired
    private FrontDocumentDao frontDocumentDao;
    @Autowired
    private FrontDocumentTagDao frontDocumentTagDao;
    @Override
    public List<Document> queryPublishedDocumentList(Integer pageIndex, Integer pageSize) {
        Integer begin = pageSize*(pageIndex-1);
        return frontDocumentDao.queryPublishedDocumentList(begin,pageSize);

    }

    @Override
    public Integer queryPublishedDocumentCount() {
        Integer count = frontDocumentDao.queryPublishedDocumentCount();
        if(count==null){
            return 0;
        }else {
            return count;
        }
    }

    @Override
    public List<Document> queryRecommendDocumentList(Integer recommendDocumentCount) {
        return frontDocumentDao.queryRecommendDocumentList(recommendDocumentCount);
    }

    @Override
    public Document queryDocumentById(Long documentId) {
        return frontDocumentDao.queryDocumentById(documentId);
    }

    @Override
    public List<Document> queryDocumentPageByTypeId(int pageIndex, Integer pageSize, Long typeId) {
        int begin = pageSize*(pageIndex-1);
        return frontDocumentDao.queryDocumentPageByTypeId(begin,pageSize,typeId);

    }

    @Override
    public List<Document> queryDocumentPageByTagId(int pageIndex, int pageSize, Long tagId) {
        int begin = pageSize*(pageIndex-1);
        return frontDocumentDao.queryDocumentPageByTagId(begin,pageSize,tagId);
    }

    @Override
    public Integer queryPublishedDocumentCountByTagId(Long tagId) {
        return frontDocumentTagDao.queryTagCountInPublishedDocument(tagId);

    }

    @Override
    public Integer queryPublishedDocumentCountByTypeId(Long typeId) {
        return frontDocumentDao.queryPublishedDocumentCountByTypeId(typeId);
    }

    @Override
    public List<String> queryDocumentByYearAndMonthAndPage(Integer pageIndex, Integer pageSize) {
        Integer begin = pageSize*(pageIndex-1);
        return frontDocumentDao.queryDocumentByYearAndMonthAndPage(begin,pageSize);
    }

    @Override
    public List<Document> queryPublishedDocumentByTime(String time) {
        return frontDocumentDao.queryPublishedDocumentByTime(time);
    }

    @Override
    public List<String> queryDocumentByYearAndMonth() {
        return frontDocumentDao.queryDocumentByYearAndMonth();
    }

    @Override
    public List<Document> queryDocumentByUserId(Integer pageIndex, int pageSize, Long userId) {
        Integer begin = pageSize*(pageIndex-1);
        return frontDocumentDao.queryDocumentByUserId(begin,pageSize,userId);
    }

    @Override
    public Integer queryDocumentCountByUserId(Long userId) {
        return frontDocumentDao.queryDocumentCountByUserId(userId);
    }
}
