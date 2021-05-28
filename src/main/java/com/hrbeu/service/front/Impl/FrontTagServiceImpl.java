package com.hrbeu.service.front.Impl;

import com.hrbeu.dao.front.FrontDocumentTagDao;
import com.hrbeu.dao.front.FrontTagDao;
import com.hrbeu.pojo.Tag;
import com.hrbeu.pojo.vo.Tag_Count;
import com.hrbeu.service.front.FrontTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FrontTagServiceImpl implements FrontTagService {
    @Autowired
    private FrontDocumentTagDao frontDocumentTagDao;
    @Autowired
    private FrontTagDao frontTagDao;
    @Override
    public List<Tag_Count> getTagAndCount() {
        //1、查询全部tag
        List<Tag> tagList = frontTagDao.queryTagList();
        if(tagList!=null){
            //2、查询关联的个数
            List<Tag_Count> tag_countList = new ArrayList<>();
            for (Tag tag : tagList) {
                Integer count = frontDocumentTagDao.queryTagCountInPublishedDocument(tag.getTagId());
                if(count!=null&&!count.equals(0)){
                    Tag_Count tag_count = new Tag_Count();
                    tag_count.setTagId(tag.getTagId());
                    tag_count.setTagName(tag.getTagName());
                    tag_count.setCount(count);
                    tag_countList.add(tag_count);
                }
                else {
                    continue;
                }
            }
            return tag_countList;
        }else {
            return null;
        }
    }

    @Override
    public List<Tag> queryAllTag() {
        return frontTagDao.queryTagList();
    }
}
