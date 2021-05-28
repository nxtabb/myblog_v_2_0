package com.hrbeu.service.admin.Impl;

import com.hrbeu.dao.admin.AdminDocumentTagDao;
import com.hrbeu.pojo.Tag;
import com.hrbeu.pojo.vo.Tag_Count;
import com.hrbeu.service.admin.AdminDocumentTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Classname AdminDocumentTagServiceImpl
 * @Description TODO
 * @Date 2021/5/13 21:50
 * @Created by nxt
 */
@Service
public class AdminDocumentTagServiceImpl implements AdminDocumentTagService {
    @Autowired
    private AdminDocumentTagDao adminDocumentTagDao;
    @Override
    public List<Tag_Count> queryTagCountListByPage(int pageIndex, int pageSize) {
        //计算分页时的起始下标
        int begin = pageSize*(pageIndex-1);
        List<Tag> tagList = adminDocumentTagDao.queryTagListByPage(begin,pageSize);
        List<Tag_Count> tag_countList = new ArrayList<>();
        for (Tag tag : tagList) {
            Integer count = adminDocumentTagDao.queryTagCountInDocuments(tag.getTagId());
            if(count==null){
                count = 0;
            }
            Tag_Count tag_count = new Tag_Count();
            tag_count.setTagId(tag.getTagId());
            tag_count.setTagName(tag.getTagName());
            tag_count.setCount(count);
            tag_countList.add(tag_count);
        }
        return tag_countList;

    }

    @Override
    public Integer queryTagInDocumentsCountById(Long tagId) {
        return adminDocumentTagDao.queryTagInDocumentsCountById(tagId);
    }
}
